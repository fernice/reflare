package org.fernice.reflare.element

import fernice.reflare.CSSEngine
import org.fernice.flare.EngineContext
import org.fernice.flare.cssparser.Parser
import org.fernice.flare.cssparser.ParserInput
import org.fernice.flare.dom.Element
import org.fernice.flare.dom.ElementData
import org.fernice.flare.dom.ElementStyles
import org.fernice.flare.selector.NamespaceUrl
import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.selector.PseudoElement
import org.fernice.flare.std.systemFlag
import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.ElementStyleResolver
import org.fernice.flare.style.MatchingResult
import org.fernice.flare.style.PerPseudoElementMap
import org.fernice.flare.style.ResolvedElementStyles
import org.fernice.flare.style.context.StyleContext
import org.fernice.flare.style.parser.ParseMode
import org.fernice.flare.style.parser.ParserContext
import org.fernice.flare.style.parser.QuirksMode
import org.fernice.flare.style.properties.PropertyDeclarationBlock
import org.fernice.flare.style.properties.parsePropertyDeclarationList
import org.fernice.flare.url.Url
import org.fernice.logging.FLogging
import org.fernice.reflare.Defaults
import org.fernice.reflare.font.FontStyleResolver
import org.fernice.reflare.render.merlin.MerlinRenderer
import org.fernice.reflare.statistics.Statistics
import org.fernice.reflare.toAWTColor
import org.fernice.reflare.trace.TraceHelper
import org.fernice.reflare.trace.trace
import org.fernice.reflare.trace.traceElement
import org.fernice.reflare.trace.traceRoot
import org.fernice.reflare.util.Observables
import org.fernice.reflare.util.VacatingRef
import org.fernice.reflare.util.observableMutableSetOf
import java.awt.Component
import java.awt.Dialog
import java.awt.Graphics
import java.awt.event.HierarchyEvent
import java.beans.PropertyChangeEvent
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.JComponent
import javax.swing.SwingUtilities
import org.fernice.flare.style.properties.stylestruct.Font as FontStyle
import java.awt.Color as AWTColor

private val REPAINT_TRACE_ENABLED = systemFlag("fernice.reflare.traceRepaint")

abstract class AWTComponentElement(componentInstance: Component) : Element {

    private val componentReference = VacatingRef(componentInstance)
    val component by componentReference

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

        if (root != null && !root.isDirty(DirtyBits.NODE_CSS)) {
            root.markDirty(DirtyBits.NODE_CSS)
        }

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
     * Process this elements's dirty state. If this element is marked as reapply [doProcessCSS]
     * will be called in order to restyle the element. If the css flag is [StyleState.DIRTY_BRANCH]
     * it will cascade this call down to all children.
     */
    internal fun processCSS(context: EngineContext) {
        if (cssFlag != StyleState.CLEAN) {
            clearDirty(DirtyBits.NODE_CSS)
        }

        when (cssFlag) {
            StyleState.CLEAN -> return
            StyleState.DIRTY_BRANCH -> {
                val parent = this as AWTContainerElement
                parent.cssFlag = StyleState.CLEAN

                for (child in parent.children) {
                    child.processCSS(context)
                }
            }
            StyleState.REAPPLY -> doProcessCSS(context)
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
            while (parent.parent != null) {
                parent = parent.parent!!
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
     * Processes the dirty state by restyling the element and marks this element as clean
     * afterwards. Does nothing if the element is already clean.
     * If this element is a parent, then it will cascade the process down to all of its
     * children.
     */
    internal open fun doProcessCSS(context: EngineContext) {
        //if (cssFlag == StyleState.CLEAN || !isVisible) return
        if (cssFlag == StyleState.CLEAN) return

        if (cssFlag == StyleState.REAPPLY) {
            if (componentReference.hasVacated()) return

            context.traceElement(this)
            context.styleContext.bloomFilter.insertParent(this)

            val styleResolver = ElementStyleResolver(this, context.styleContext)
            val styles = styleResolver.resolvePrimaryStyleWithDefaultParentStyles()

            val data = getData()

            finishRestyle(context.styleContext, data, styles)
        }

        cssFlag = StyleState.CLEAN
    }

    internal val debug_traceHelper: TraceHelper? = TraceHelper.createTraceHelper()

    private fun traceReapplyOrigin(origin: String) {
        TraceHelper.traceReapplyOrigin(debug_traceHelper, origin)
    }

    // ***************************** Old Dirty ***************************** //

    fun restyle() {
        Statistics.increment("force-restyle")

        forceApplyStyle = true
        fontStyle = FontStyle.initial

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

    final override var styleAttribute: PropertyDeclarationBlock? = null

    var styleAttributeValue: String = ""
        set(value) {
            field = value

            styleAttribute = if (value.isNotBlank()) {
                val input = Parser.new(ParserInput(value))
                val context = ParserContext(ParseMode.Default, QuirksMode.NO_QUIRKS, Url(""))

                parsePropertyDeclarationList(context, input)
            } else {
                null
            }

            reapplyCSS(origin = "style-attribute")
        }


    // ***************************** Data ***************************** //

    private var data: ElementData? = null

    override fun getData(): ElementData {
        val current = data
        return if (current != null) {
            current
        } else {
            val new = ElementData(
                ElementStyles(
                    primary = null,
                    pseudos = PerPseudoElementMap()
                )
            )
            data = new
            new
        }
    }

    override fun getDataOrNull(): ElementData? {
        return data
    }

    override fun clearData() {
        data = null
    }

    fun getStyle(): ComputedValues? {
        return getDataOrNull()?.styles?.primary
    }

    private var forceApplyStyle = false

    override fun finishRestyle(context: StyleContext, data: ElementData, elementStyles: ResolvedElementStyles) {
        val oldStyle = data.setStyles(elementStyles)

        val primaryStyle = elementStyles.primary.style()

        if (!forceApplyStyle && oldStyle.primary != null && primaryStyle == oldStyle.primary()) {
            for ((i, style) in data.styles.pseudos.iterator().withIndex()) {
                if (style != null) {
                    updatePseudoElement(PseudoElement.fromEagerOrdinal(i), style)
                }
            }

            fireRestyleListeners(primaryStyle)
            return
        }

        updateStyle(primaryStyle)

        if (isRoot()) {
            val fontSize = primaryStyle.font.fontSize
            val oldFontSize = oldStyle.primary?.font?.fontSize

            if (oldFontSize != null && oldFontSize != fontSize) {
                context.device.rootFontSize = fontSize.size()
            }
        }

        fontStyle = primaryStyle.font
        component.foreground = primaryStyle.color.color.toAWTColor()

        val background = primaryStyle.background.color.toAWTColor()

        if (component.isOpaque) {
            component.background = Defaults.COLOR_WHITE
        } else if (isBackgroundColorApplicable(background)) {
            component.background = background
        }

        for ((i, style) in data.styles.pseudos.iterator().withIndex()) {
            if (style != null) {
                updatePseudoElement(PseudoElement.fromEagerOrdinal(i), style)
            }
        }

        forceApplyStyle = false

        if (frame != null) {
            component.repaint()

            if (REPAINT_TRACE_ENABLED) {
                LOG.trace { "[${repaintCount.getAndIncrement()}] repaint requested for ${this::class.simpleName}" }
            }
        }

        fireRestyleListeners(primaryStyle)
    }

    protected open fun updateStyle(style: ComputedValues) {}
    protected open fun updatePseudoElement(pseudoElement: PseudoElement, style: ComputedValues) {}

    private fun isBackgroundColorApplicable(color: AWTColor) = when {
        color.alpha == 255 -> true
        component is java.awt.Frame -> (component as java.awt.Frame).isUndecorated
        component is Dialog -> (component as Dialog).isUndecorated
        else -> true
    }

    private var fontStyle: FontStyle by Observables.observable(FontStyle.initial) { _, _, fontStyle ->
        component.font = FontStyleResolver.resolve(fontStyle)
    }

    // ***************************** Render ***************************** //

    private val renderer by lazy { MerlinRenderer(componentReference, this) }

    fun paintBackground(component: Component, g: Graphics) {
        pulseForRendering()

        renderer.renderBackground(g, getStyle())
    }

    open fun paintBorder(component: Component, g: Graphics) {
        pulseForRendering()

        renderer.renderBorder(g, getStyle())
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

    private val restyleListenersProperty = lazy { mutableListOf<(ComputedValues) -> Unit>() }
    private val restyleListeners by restyleListenersProperty
    fun addRestyleListener(listener: (ComputedValues) -> Unit) = restyleListeners.add(listener)
    fun removeRestyleListener(listener: (ComputedValues) -> Unit) = restyleListeners.remove(listener)
    private fun fireRestyleListeners(style: ComputedValues) = if (restyleListenersProperty.isInitialized()) restyleListeners.forEach { it(style) } else Unit
}

abstract class ComponentElement(component: JComponent) : AWTContainerElement(component)

enum class StyleState {

    CLEAN,

    REAPPLY,

    DIRTY_BRANCH
}

private val repaintCount = AtomicInteger()

private val LOG = FLogging.logger { }