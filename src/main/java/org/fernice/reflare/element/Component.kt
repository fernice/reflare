package org.fernice.reflare.element

import fernice.reflare.CSSEngine
import org.fernice.flare.EngineContext
import org.fernice.flare.dom.Element
import org.fernice.flare.dom.ElementStyles
import org.fernice.flare.selector.NamespaceUrl
import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.selector.PseudoElement
import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.ElementStyleResolver
import org.fernice.flare.style.MatchingResult
import org.fernice.flare.style.StyleRoot
import org.fernice.flare.style.context.StyleContext
import org.fernice.flare.style.source.StyleAttribute
import org.fernice.reflare.Defaults
import org.fernice.reflare.font.FontStyleResolver
import org.fernice.reflare.render.merlin.MerlinRenderer
import org.fernice.reflare.statistics.Statistics
import org.fernice.reflare.toAWTColor
import org.fernice.reflare.toOpaqueAWTColor
import org.fernice.reflare.trace.TraceHelper
import org.fernice.reflare.trace.trace
import org.fernice.reflare.trace.traceElement
import org.fernice.reflare.trace.traceRoot
import org.fernice.reflare.util.VacatingRef
import org.fernice.reflare.util.observableMutableSetOf
import org.fernice.std.systemFlag
import java.awt.Component
import java.awt.Graphics
import java.awt.Window
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.HierarchyEvent
import java.beans.PropertyChangeEvent
import java.util.EventListener
import java.util.EventObject
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.event.EventListenerList

private val REPAINT_TRACE_ENABLED = systemFlag("fernice.reflare.traceRepaint")

abstract class AWTComponentElement(componentInstance: Component) : Element {

    init {
        require(componentInstance !is Window) { "windows cannot be elements" }
    }

    private val componentReference = VacatingRef(componentInstance)
    val component: Component
        get() = componentReference.deref()

    // ***************************** Dirty ***************************** //

    private var dirtyBits: Int = 0

    internal fun markDirty(dirtyBits: DirtyBits) {
        if (isDirtyEmpty()) {
            markElementDirty()
        }

        this.dirtyBits = this.dirtyBits or dirtyBits.mask
    }

    internal fun isDirty(dirtyBits: DirtyBits): Boolean {
        return this.dirtyBits and dirtyBits.mask != 0
    }

    internal fun clearDirty(dirtyBits: DirtyBits) {
        this.dirtyBits = this.dirtyBits and dirtyBits.mask.inv()
    }

    internal fun isDirtyEmpty() = dirtyBits == 0

    internal fun isDirty() = dirtyBits != 0

    private fun markElementDirty() {
        frame?.markElementDirty(this)
    }

    @Volatile
    internal var cssFlag: StyleState = StyleState.CLEAN
    val styleState: StyleState
        get() = cssFlag

    /**
     * Notifies the parent of invalid css by mark every parent as a dirty branch
     * if they are not marked dirty yet. Additionally request a new pulse if the
     * root node was not marked as dirty yet.
     */
    private fun notifyParentOfInvalidatedCSS() {
        val root = frame?.root
        root?.markDirty(DirtyBits.NODE_CSS)

        frame?.requestNextPulse()

        var parent = parent
        while (parent != null) {
            if (parent.cssFlag == StyleState.CLEAN) {
                parent.cssFlag = StyleState.DIRTY_BRANCH
                parent = parent.parent
            } else {
                parent = null
            }
        }
    }

    /**
     * Marks the element's css dirty.
     */
    fun reapplyCSS() {
        reapplyCSS(origin = "reapply:external")
    }

    @JvmName(name = "reapplyCSSFrom")
    internal fun reapplyCSS(origin: String) {
        traceReapplyOrigin(origin)

        if (cssFlag == StyleState.REAPPLY) {
            return
        }

        markBranchAsReapplyCSS()
        notifyParentOfInvalidatedCSS()
    }

    /**
     * Marks this node's and all of direct and indirect children's css flags
     * as [StyleState.REAPPLY]
     */
    private fun markBranchAsReapplyCSS() {
        cssFlag = StyleState.REAPPLY

        if (this is AWTContainerElement) {
            for (element in this.children) {
                element.markBranchAsReapplyCSS()
            }
        }
    }

    /**
     * Applies the CSS immediately.
     */
    fun applyCSS() {
        applyCSS(origin = "apply:external")
    }

    @JvmName(name = "applyCSSFrom")
    internal fun applyCSS(origin: String) {
        traceReapplyOrigin("apply:$origin")

        val frame = frame

        if (frame != null) {
            markBranchAsReapplyCSS()
            notifyParentOfInvalidatedCSS()

            pulseForComputation()
        } else {
            // Make sure this element and its children is suitable for restyling
            markBranchAsReapplyCSS()
            notifyParentOfInvalidatedCSS()

            var parent = this
            while (true) {
                parent = parent.parent ?: break
            }

            // Without a frame we are unable to process a few cases correctly namely
            // viewport relative sizes root font size and more. Because in HTML no elements
            // exists without being in a view hierarchy and because there is only one view
            // hierarchy this edge case does not exist. We're solving it by providing means
            // to at least get it right most cases.
            val localContext = CSSEngine.createLocalEngineContext(parent)

            // Immediately apply the styles
            trace(localContext, name = "apply") { traceContext ->
                traceContext.traceRoot(parent)
                parent.processCSS(traceContext)
            }
        }
    }

    /**
     * Process this elements's dirty state. If this element is marked as reapply [doProcessCSS]
     * will be called in order to restyle the element. If the css flag is [StyleState.DIRTY_BRANCH]
     * it will cascade this call down to all children.
     */
    internal open fun processCSS(context: EngineContext) {
        if (cssFlag == StyleState.CLEAN) return

        if (cssFlag == StyleState.REAPPLY) {
            doProcessCSS(context)
        }

        cssFlag = StyleState.CLEAN
    }

    /**
     * Processes the dirty state by restyling the element and marks this element as clean
     * afterwards. Does nothing if the element is already clean.
     * If this element is a parent, then it will cascade the process down to all of its
     * children.
     */
    private fun doProcessCSS(context: EngineContext) {
        if (componentReference.hasVacated()) return

        context.traceElement(this)
        context.styleContext.prepare(this)

        val styleResolver = ElementStyleResolver(this, context.styleContext)
        val styles = styleResolver.resolveStyleWithDefaultParentStyles()

        val previousStyles = this.styles

        finishRestyle(context.styleContext, previousStyles, styles)
    }

    internal val debug_traceHelper: TraceHelper? = TraceHelper.createTraceHelper()

    private fun traceReapplyOrigin(origin: String) {
        TraceHelper.traceReapplyOrigin(debug_traceHelper, origin)
    }

    // ***************************** Old Dirty ***************************** //

    fun restyle() {
        Statistics.increment("force-restyle")

        applyCSS(origin = "force")
    }

    fun restyleIfNecessary() {
        if (cssFlag != StyleState.CLEAN) {
            restyle()
        }
    }

    fun pulseForComputation() {
        frame?.pulse()
    }

    fun pulseForRendering() {
        frame?.pulse()
    }

    fun getMatchingStyles(): MatchingResult {
        val frame = frame

        return frame?.matchStyle(this) ?: CSSEngine.matchStyleWithLocalContext(this)
    }

    // ***************************** Frame & Parent ***************************** //

    var frame: Frame? = null
        internal set(frame) {
            val old = field
            field = frame
            parentChanged(old, frame)
        }

    internal abstract fun parentChanged(old: Frame?, new: Frame?)

    final override val owner: Element? get() = this

    final override var parent: AWTContainerElement? = null
        internal set

    final override val traversalParent: Element? get() = inheritanceParent
    final override val inheritanceParent: Element? get() = parent

    override val previousSibling: Element?
        get() = when (val parent = parent) {
            null -> null
            else -> {
                val children = parent.children

                children.getOrNull(children.indexOf(this) - 1)
            }
        }

    override val nextSibling: Element?
        get() = when (val parent = parent) {
            null -> null
            else -> {
                val children = parent.children

                children.getOrNull(children.indexOf(this) + 1)
            }
        }

    override val pseudoElement: PseudoElement? get() = null

    override fun isRoot(): Boolean = parent == null

    init {
        component.addHierarchyListener { event ->
            if (event.id == HierarchyEvent.HIERARCHY_CHANGED) {

                if ((event.changeFlags and HierarchyEvent.PARENT_CHANGED.toLong()) != 0L) {
                    SwingUtilities.getWindowAncestor(event.changedParent)?.frame
                } else if ((event.changeFlags and HierarchyEvent.SHOWING_CHANGED.toLong()) != 0L && event.changed === component) {
                    if (component.isVisible) {
//                        applyCSS(origin = "visible")
                    }
                }
            }
        }
        component.addPropertyChangeListener(::componentPropertyChanged)
    }

    open val isVisible: Boolean
        get() = component.isVisible

    // ***************************** Matching ***************************** //

    final override var namespace: NamespaceUrl? = null
    final override var id: String? = null
    final override val classes: MutableSet<String> = observableMutableSetOf {
        reapplyCSS(origin = "classes")
    }

    override fun hasID(id: String): Boolean {
        return id == this.id
    }

    override fun hasClass(styleClass: String): Boolean {
        return classes.contains(styleClass)
    }

    override fun hasPseudoElement(pseudoElement: PseudoElement): Boolean = false
    override fun matchPseudoElement(pseudoElement: PseudoElement): Boolean = false

    private var hover = false
    private var focus = false

    override fun matchNonTSPseudoClass(pseudoClass: NonTSPseudoClass): Boolean {
        return when (pseudoClass) {
            is NonTSPseudoClass.Enabled -> component.isEnabled
            is NonTSPseudoClass.Disabled -> !component.isEnabled
            is NonTSPseudoClass.Focus -> component.isFocusOwner || focus
            is NonTSPseudoClass.Hover -> hover
            is NonTSPseudoClass.Active -> active
            else -> false
        }
    }

    // ***************************** Inline ***************************** //

    final override var styleAttribute: StyleAttribute? = null
        set(styleAttribute) {
            val previousStyleAttribute = field
            if (styleAttribute !== previousStyleAttribute) {
                field = styleAttribute

                applyCSS(origin = "style-attribute")
            }
        }

    var styleAttributeValue: String = ""
        set(value) {
            field = value

            styleAttribute = if (value.isNotBlank()) {
                StyleAttribute.from(value, this)
            } else {
                null
            }
        }

    final override var styleRoot: StyleRoot? = null
        set(styleRoot) {
            val previousStyleRoot = field
            if (styleRoot !== previousStyleRoot) {
                field = styleRoot

                applyCSS(origin = "style-root")
            }
        }

    var styleRootValue: fernice.reflare.StyleRoot? = null
        set(value) {
            field = value

            styleRoot = value?.peer
        }

    // ***************************** Data ***************************** //

    internal var _styles: ElementStyles? = null
    final override val styles: ElementStyles?
        get() = _styles

    override fun finishRestyle(context: StyleContext, previousStyles: ElementStyles?, styles: ElementStyles) {
        if (styles === previousStyles) return

        this._styles = styles

        for ((index, pseudoStyle) in styles.pseudos.iterator().withIndex()) {
            if (pseudoStyle == null) continue

            val pseudoElement = PseudoElement.fromEagerOrdinal(index)

            if (!hasPseudoElement(pseudoElement)) continue

            updatePseudoElement(pseudoElement, pseudoStyle)
        }

        val previousStyle = previousStyles?.primary
        val style = styles.primary

        var repaint = false

        if (style.font != previousStyle?.font) {
            component.font = FontStyleResolver.resolve(style.font)

            val fontSize = style.font.fontSize
            if (isRoot() && fontSize != previousStyle?.font?.fontSize) {
                context.device.rootFontSize = fontSize.size()
            }

            repaint = true
        }

        if (style.padding != previousStyle?.padding
            || style.margin != previousStyle.margin
            || style.border != previousStyle.border
        ) {
            renderer.invalidateShapes()
            renderer.invalidateLayers()

            repaint = true
        }

        if (style.color != previousStyle?.color) {
            repaint = true
        }

        if (style.background != previousStyle?.background) {
            renderer.invalidateLayers()

            repaint = true
        }

        // Enforce the foreground and background to prevent manual overrides
        // from causing weird glitches. Especially when migrating from other
        // LnFs, calls to setForeground() or setBackground() are not uncommon.
        // This operation is very cheap anyway.
        component.foreground = style.color.color.toAWTColor()
        component.background = when {
            component.isOpaque -> style.background.color.toOpaqueAWTColor()
            else -> style.background.color.toAWTColor()
        }

        updateStyle(style)

        if (repaint && frame != null) {
            component.repaint()

            if (REPAINT_TRACE_ENABLED) {
                LOG.trace { "[${repaintCount.getAndIncrement()}] repaint requested for ${this::class.simpleName}" }
            }
        }

        fireElementRestyleListeners(style)
    }

    protected open fun updateStyle(style: ComputedValues) {}
    protected open fun updatePseudoElement(pseudoElement: PseudoElement, style: ComputedValues) {}

    // ***************************** Render ***************************** //

    private val renderer = MerlinRenderer(componentReference, this)

    fun paintBackground(g: Graphics) {
        pulseForRendering()

        renderer.renderBackground(g, styles?.primary)
    }

    open fun paintBorder(g: Graphics) {
        pulseForRendering()

        renderer.renderBorder(g, styles?.primary)
    }

    // ***************************** Renderer Override ***************************** //


    protected open fun componentPropertyChanged(event: PropertyChangeEvent) {
        when (event.propertyName) {
            "enabled" -> reapplyCSS(origin = "enabled")
            "visible" -> if (event.newValue == true) applyCSS(origin = "visible")
        }
    }

    fun hoverHint(hover: Boolean): Boolean {
        val old = this.hover
        this.hover = hover

        if (old != hover) {
            reapplyCSS(origin = "hover:hint")
        }

        return old
    }

    fun focusHint(focus: Boolean): Boolean {
        val old = this.focus
        this.focus = focus

        if (old != focus) {
            reapplyCSS(origin = "focus:hint")
        }

        return old
    }

    protected var active: Boolean = false

    fun activeHint(active: Boolean): Boolean {
        val old = this.active
        this.active = active

        if (old != active) {
            reapplyCSS(origin = "active:hint")
        }

        return old
    }

    // ***************************** Listeners ***************************** //

    private val listenerList = EventListenerList()

    fun addElementRestyleListener(listener: ElementRestyleListener) = listenerList.add(ElementRestyleListener::class.java, listener)
    fun removeElementRestyleListener(listener: ElementRestyleListener) = listenerList.remove(ElementRestyleListener::class.java, listener)

    private fun fireElementRestyleListeners(style: ComputedValues) {
        if (listenerList.getListenerCount(ElementRestyleListener::class.java) > 0) {
            val event = ElementRestyleEvent(this, style)
            for (listener in listenerList.getListeners(ElementRestyleListener::class.java)) {
                listener.elementRestyled(event)
            }
        }
    }
}

fun interface ElementRestyleListener : EventListener {
    fun elementRestyled(event: ElementRestyleEvent)
}

class ElementRestyleEvent(element: Element, val style: ComputedValues) : EventObject(element) {
    val element: Element
        get() = source as Element
}

abstract class ComponentElement(component: JComponent) : AWTContainerElement(component)

enum class StyleState {

    CLEAN,

    REAPPLY,

    DIRTY_BRANCH
}

private val repaintCount = AtomicInteger()

private val LOG = org.fernice.logging.FLogging.logger { }
