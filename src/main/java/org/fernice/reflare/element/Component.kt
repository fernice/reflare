package org.fernice.reflare.element

import fernice.reflare.CSSEngine
import fernice.reflare.FlareLookAndFeel
import fernice.std.None
import fernice.std.Option
import fernice.std.Some
import fernice.std.into
import fernice.std.mapOr
import fernice.std.unwrap
import org.fernice.flare.EngineContext
import org.fernice.flare.cssparser.Parser
import org.fernice.flare.cssparser.ParserInput
import org.fernice.flare.dom.Element
import org.fernice.flare.dom.ElementData
import org.fernice.flare.dom.ElementStyles
import org.fernice.flare.selector.NamespaceUrl
import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.selector.PseudoElement
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
import org.fernice.flare.style.value.computed.SingleFontFamily
import org.fernice.flare.url.Url
import org.fernice.reflare.Defaults
import org.fernice.reflare.geom.Insets
import org.fernice.reflare.geom.toInsets
import org.fernice.reflare.internal.SunFontHelper
import org.fernice.reflare.platform.Platform
import org.fernice.reflare.render.BackgroundLayers
import org.fernice.reflare.render.RenderCache
import org.fernice.reflare.render.computeBackgroundLayers
import org.fernice.reflare.render.renderBackground
import org.fernice.reflare.render.renderBorder
import org.fernice.reflare.shape.BackgroundShape
import org.fernice.reflare.shape.BorderShape
import org.fernice.reflare.shape.computeBackgroundShape
import org.fernice.reflare.shape.computeBorderShape
import org.fernice.reflare.toAWTColor
import org.fernice.reflare.trace.TraceHelper
import org.fernice.reflare.util.Broadcast
import org.fernice.reflare.util.Observables
import org.fernice.reflare.util.broadcast
import java.awt.Component
import java.awt.Dialog
import java.awt.Font
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.Window
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JComponent
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener
import org.fernice.flare.style.properties.stylestruct.Font as FontStyle
import java.awt.Color as AWTColor

abstract class AWTComponentElement(val component: Component) : Element {

    fun <C : JComponent> component(): C {
        @Suppress("UNCHECKED_CAST")
        return component as C
    }

    init {
        component.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                boundsChange.fire(component.bounds)
            }
        })
    }

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

    internal var cssFlag: StyleState = StyleState.CLEAN

    /**
     * Notifies the parent of invalid css by mark every parent as a dirty branch
     * if they are not marked dirty yet. Additionally request a new pulse if the
     * root node was not marked as dirty yet.
     */
    private fun notifyParentOfInvalidatedCSS() {
        val root = frame?.root

        if (root != null && !root.isDirty(DirtyBits.NODE_CSS)) {
            root.markDirty(DirtyBits.NODE_CSS)
            frame?.requestNextPulse(this)
        }

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

    private fun forcePulseForDeferredRendering() {
        component.repaint()
    }

    /**
     * Marks the element's css dirty.
     */
    fun reapplyCSS() {
        if (cssFlag == StyleState.REAPPLY) {
            return
        }

        // if the element has no frame find the root and restyle the element
        // immediately to allow for size calculation off the scene graph
        if (frame == null) {
            var parent = this
            while (parent.parent != null) {
                parent = parent.parent!!
            }

            // Make sure this element and its children is suitable for restyling
            parent.markBranchAsReapplyCSS()

            // Without a frame we are unable to process a few cases correctly namely
            // viewport relative sizes root font size and more. Because in HTML no elements
            // exists without being in a view hierarchy and because there is only one view
            // hierarchy this edge case does not exist. We're solving it by providing means
            // to at least get it right most cases.
            val localContext = CSSEngine.createLocalEngineContext(parent)

            // Immediately apply the styles
            parent.processCSS(localContext)
        } else {
            markBranchAsReapplyCSS()
            notifyParentOfInvalidatedCSS()
            forcePulseForDeferredRendering()
        }
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
        val frame = frame

        if (frame != null) {
            traceReapplyOrigin("apply")
            markBranchAsReapplyCSS()
            notifyParentOfInvalidatedCSS()

            pulseForComputation()
        } else {
            var parent = this
            while (parent.parent != null) {
                parent = parent.parent!!
            }

            // Make sure this element and its children is suitable for restyling
            parent.markBranchAsReapplyCSS()

            // Without a frame we are unable to process a few cases correctly namely
            // viewport relative sizes root font size and more. Because in HTML no elements
            // exists without being in a view hierarchy and because there is only one view
            // hierarchy this edge case does not exist. We're solving it by providing means
            // to at least get it right most cases.
            val localContext = CSSEngine.createLocalEngineContext(parent)

            // Immediately apply the styles
            parent.processCSS(localContext)
        }
    }

    /**
     * Processes the dirty state by restyling the element and marks this element as clean
     * afterwards. Does nothing if the element is already clean.
     * If this element is a parent, then it will cascade the process down to all of its
     * children.
     */
    internal open fun doProcessCSS(context: EngineContext) {
        if (cssFlag == StyleState.CLEAN) {
            return
        }

        if (cssFlag == StyleState.REAPPLY) {
            context.traceElement(this)
            TraceHelper.resetReapplyOrigins(debug_traceHelper)

            context.styleContext.bloomFilter.insertParent(this)

            val styleResolver = ElementStyleResolver(this, context.styleContext)
            val styles = styleResolver.resolvePrimaryStyleWithDefaultParentStyles()

            val data = this.ensureData()

            this.finishRestyle(context.styleContext, data, styles)
        }

        cssFlag = StyleState.CLEAN
    }

    internal val debug_traceHelper: TraceHelper? = TraceHelper.createTraceHelper()

    fun traceReapplyOrigin(origin: String) {
        TraceHelper.traceReapplyOrigin(debug_traceHelper, origin)
    }

    // ***************************** Old Dirty ***************************** //

    fun forceRestyle() {
        forceApplyStyle = true
        fontStyle = FontStyle.initial

        applyCSS()
    }

    fun getMatchingStyles(): MatchingResult {
        val frame = frame

        return frame?.matchStyle(this) ?: CSSEngine.matchStyleWithLocalContext(this)
    }

    fun pulseForComputation() {
        frame?.pulse()
    }

    fun pulseForRendering() {
        frame?.pulse()
    }

    // ***************************** Frame & Parent ***************************** //

    var frame: Frame? = null
        internal set(frame) {
            val old = field
            field = frame
            parentChanged(old, frame)
        }

    internal abstract fun parentChanged(old: Frame?, new: Frame?)

    var parent: AWTContainerElement? = null
        internal set(parent) {
            field = parent

            if (parent != null) {
                traceReapplyOrigin("parent")
                reapplyCSS()
            }
        }

    override fun parent(): Option<Element> {
        return parent.into()
    }

    override fun owner(): Option<Element> {
        return Some(this)
    }

    override fun traversalParent(): Option<Element> {
        return parent()
    }

    override fun inheritanceParent(): Option<Element> {
        return parent()
    }

    override fun pseudoElement(): Option<PseudoElement> {
        return None
    }

    override fun isRoot(): Boolean {
        return parent == null
    }

    private var fontStyle: FontStyle by Observables.observable(FontStyle.initial) { _, _, fontStyle ->
        val fontSize = fontStyle.fontSize.size().toPx().toInt()
        val fontWeight = fontStyle.fontWeight.value

        var awtFont: Font? = null

        loop@
        for (fontFamily in fontStyle.fontFamily.values) {
            awtFont = when (fontFamily) {
                is SingleFontFamily.Generic -> {
                    when (fontFamily.name) {
                        "serif" -> Platform.SystemSerifFont
                        "sans-serif" -> Platform.SystemSansSerifFont
                        "monospace" -> Platform.SystemMonospaceFont
                        else -> Platform.SystemSerifFont
                    }
                }
                is SingleFontFamily.FamilyName -> {
                    SunFontHelper.findFont(fontFamily.name.value, fontWeight.toInt(), false) ?: continue@loop
                }
            }
            break
        }

        if (awtFont != null) {
            awtFont = awtFont.deriveFont(fontSize.toFloat())
            component.font = awtFont
        }
    }

    // ***************************** Matching ***************************** //

    var namespace: Option<NamespaceUrl> = None

    override fun namespace(): Option<NamespaceUrl> {
        return namespace
    }

    var id: Option<String> = None

    override fun id(): Option<String> {
        return id
    }

    override fun hasID(id: String): Boolean {
        return when (val own = this.id) {
            is Some -> own.value == id
            is None -> false
        }
    }

    val classes: MutableSet<String> = mutableSetOf()

    override fun classes(): Set<String> {
        return classes
    }

    override fun hasClass(styleClass: String): Boolean {
        return classes.contains(styleClass)
    }

    override fun matchPseudoElement(pseudoElement: PseudoElement): Boolean {
        return false
    }

    protected var hover = false
    protected var focus = false

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

    private var styleAttributeInternal: Option<PropertyDeclarationBlock> = None

    override fun styleAttribute(): Option<PropertyDeclarationBlock> {
        return styleAttributeInternal
    }

    var styleAttribute: String = ""
        set(value) {
            field = value

            styleAttributeInternal = if (value.isNotBlank()) {
                val input = Parser.new(ParserInput(value))
                val context = ParserContext(ParseMode.Default, QuirksMode.NO_QUIRKS, Url(""))

                Some(parsePropertyDeclarationList(context, input))
            } else {
                None
            }

            traceReapplyOrigin("style-attribute")
            reapplyCSS()
        }


    // ***************************** Data ***************************** //

    private var data: Option<ElementData> = None

    override fun ensureData(): ElementData {
        return when (data) {
            is Some -> data.unwrap()
            is None -> {
                val new = ElementData(
                    ElementStyles(
                        None,
                        PerPseudoElementMap()
                    )
                )
                data = Some(new)

                new
            }
        }
    }

    override fun getData(): Option<ElementData> {
        return data
    }

    override fun clearData() {
        data = None
    }

    fun getStyle(): Option<ComputedValues> {
        val data = when (val elementData = getData()) {
            is Some -> elementData.value
            is None -> return None
        }

        return data.styles.primary
    }

    private var forceApplyStyle = false

    override fun finishRestyle(context: StyleContext, data: ElementData, elementStyles: ResolvedElementStyles) {
        val oldStyle = data.setStyles(elementStyles)

        val primaryStyle = elementStyles.primary.style()

        if (!forceApplyStyle && oldStyle.primary !is None && primaryStyle == oldStyle.primary()) {
            for ((i, style) in data.styles.pseudos.iter().withIndex()) {
                if (style is Some) {
                    updatePseudoElement(PseudoElement.fromEagerOrdinal(i), style.value)
                }
            }

            return
        }

        updateStyle(primaryStyle)

        if (isRoot()) {
            val fontSize = primaryStyle.font.fontSize

            if (oldStyle.primary.mapOr({ style -> style.font.fontSize != fontSize }, false)) {
                context.device.setRootFontSize(fontSize.size())
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

        for ((i, style) in data.styles.pseudos.iter().withIndex()) {
            if (style is Some) {
                updatePseudoElement(PseudoElement.fromEagerOrdinal(i), style.value)
            }
        }

        restyle.fire(primaryStyle)
    }

    protected open fun updateStyle(style: ComputedValues) {}
    protected open fun updatePseudoElement(pseudoElement: PseudoElement, style: ComputedValues) {}

    private fun isBackgroundColorApplicable(color: AWTColor) = when {
        color.alpha == 255 -> true
        component is java.awt.Frame -> component.isUndecorated
        component is Dialog -> component.isUndecorated
        else -> true
    }
    // ***************************** Render ***************************** //

    val cache: RenderCache by lazy { RenderCache() }

    fun paintBackground(component: Component, g: Graphics) {
        pulseForRendering()

        renderBackground(g, component, this, getStyle())
    }

    open fun paintBorder(component: Component, g: Graphics) {
        pulseForRendering()

        renderBorder(g, component, this, getStyle())
    }

    // ***************************** Renderer Override ***************************** //

    fun hoverHint(hover: Boolean): Boolean {
        val old = this.hover
        this.hover = hover

        if (old != hover) {
            traceReapplyOrigin("hover:hint")
            reapplyCSS()
        }

        return old
    }

    fun focusHint(focus: Boolean): Boolean {
        val old = this.focus
        this.focus = focus

        if (old != focus) {
            traceReapplyOrigin("focus:hint")
            reapplyCSS()
        }

        return old
    }

    protected var active: Boolean = false

    fun activeHint(active: Boolean): Boolean {
        val old = this.active
        this.active = active

        if (old != active) {
            traceReapplyOrigin("active:hint")
            reapplyCSS()
        }

        return old
    }

    private val boundsChange: Broadcast<Rectangle> = broadcast()
    val restyle: Broadcast<ComputedValues> = broadcast()

    // ***************************** Style Properties ***************************** //

    val margin: Insets by property(Insets.empty()) {
        dependsOn(boundsChange) { component.bounds }
        dependsOn(restyle) { style -> style.margin }

        computeStyle { styles -> styles.margin.toInsets(component.bounds) }
    }

    val padding: Insets by property(Insets.empty()) {
        dependsOn(boundsChange) { component.bounds }
        dependsOn(restyle) { style -> style.padding }

        computeStyle { styles -> styles.padding.toInsets(component.bounds) }
    }

    val fontSize by property(16) {
        dependsOn(restyle) { style -> style.font.fontSize }

        computeStyle { styles -> styles.font.fontSize.size().toPx().toInt() }
    }

    val backgroundShape: BackgroundShape by property {
        dependsOn(boundsChange) { component.bounds }
        dependsOn(restyle)

        computeStyle { element, style -> BackgroundShape.computeBackgroundShape(style, element) }
    }

    fun invalidateShape() {
        this::padding.invalidate()
        this::margin.invalidate()
        this::backgroundShape.invalidate()
        this::borderShape.invalidate()
        this::backgroundLayers.invalidate()
    }

    val borderShape: BorderShape by property {
        dependsOn(boundsChange) { component.bounds }
        dependsOn(restyle)

        computeStyle { element, style -> BorderShape.computeBorderShape(style, element) }
    }

    val backgroundLayers: BackgroundLayers by property {
        dependsOn(boundsChange)
        dependsOn(restyle) { style -> style.margin }
        dependsOn(restyle) { style -> style.padding }
        dependsOn(restyle) { style -> style.border }
        dependsOn(restyle) { style -> style.background }

        computeStyle { style -> BackgroundLayers.computeBackgroundLayers(component, style) }
    }
}

abstract class ComponentElement(component: JComponent) : AWTContainerElement(component) {

    init {
        component.addAncestorListener(object : AncestorListener {
            override fun ancestorAdded(event: AncestorEvent) {
                if (event.ancestor is Window) {
                    val frame = event.ancestor as Window

                    frame.frame
                }
            }

            override fun ancestorMoved(event: AncestorEvent) {
            }

            override fun ancestorRemoved(event: AncestorEvent) {
            }
        })
    }
}

enum class StyleState {

    CLEAN,

    REAPPLY,

    DIRTY_BRANCH
}