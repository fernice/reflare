package org.fernice.reflare.element

import fernice.reflare.CSSEngine
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
import org.fernice.flare.std.min
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
import org.fernice.reflare.geom.Insets
import org.fernice.reflare.geom.toInsets
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
import org.fernice.reflare.util.Broadcast
import org.fernice.reflare.util.broadcast
import java.awt.AWTEvent
import java.awt.Component
import java.awt.Container
import java.awt.Font
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.AWTEventListener
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.MouseEvent
import java.util.WeakHashMap
import javax.swing.CellRendererPane
import javax.swing.JComponent
import javax.swing.JLayeredPane
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener
import org.fernice.reflare.render.CellRendererPane as ModernCellRenderPane
import java.awt.Color as AWTColor

abstract class AWTComponentElement(val component: Component) : Element {

    fun <C : JComponent> component(): C {
        @Suppress("UNCHECKED_CAST")
        return component as C
    }

    init {
        component.addFocusListener(object : FocusListener {
            override fun focusLost(e: FocusEvent) {
                reapplyCSS()
            }

            override fun focusGained(e: FocusEvent) {
                reapplyCSS()
            }
        })

        component.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                boundsChange.fire(component.bounds)
            }
        })

        SharedHoverHandler
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

    internal fun reapplyCSS() {
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
            parent.doReapplyCSS()

            // Without a frame we are unable to process a few cases correctly namely
            // viewport relative sizes root font size and more. Because in HTML no elements
            // exists without being in a view hierarchy and because there is only one view
            // hierarchy this edge case does not exist. We're solving it by providing means
            // to at least get it right most cases.
            val localContext = CSSEngine.createLocalEngineContext(parent)

            // Immediately apply the styles
            parent.processCSS(localContext)
        } else {
            doReapplyCSS()
            notifyParentOfInvalidatedCSS()
        }
    }

    private fun doReapplyCSS() {
        cssFlag = StyleState.REAPPLY

        if (this is AWTContainerElement) {
            for (element in this.children) {
                element.doReapplyCSS()
            }
        }
    }

    internal fun processCSS(context: EngineContext) {
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

    fun applyCSS() {
        val frame = frame

        if (frame != null) {
            reapplyCSS()

            frame.doCSSPass()
        } else {
            var parent = this
            while (parent.parent != null) {
                parent = parent.parent!!
            }

            // Make sure this element and its children is suitable for restyling
            parent.doReapplyCSS()

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

    internal open fun doProcessCSS(context: EngineContext) {
        if (cssFlag == StyleState.CLEAN) {
            return
        }

        if (cssFlag == StyleState.REAPPLY) {
            context.styleContext.bloomFilter.insertParent(this)

            val styleResolver = ElementStyleResolver(this, context.styleContext)
            val styles = styleResolver.resolvePrimaryStyleWithDefaultParentStyles()

            val data = this.ensureData()

            this.finishRestyle(context.styleContext, data, styles)
        }

        cssFlag = StyleState.CLEAN
    }

    // ***************************** Stuff ***************************** //

    fun invalidateBounds() {

        // cache.invalidateBounds()
        component.repaint()
    }

    fun getMatchingStyles(): MatchingResult {
        val frame = frame

        return frame?.matchStyle(this) ?: CSSEngine.matchStyleWithLocalContext(this)
    }

    // ***************************** Frame & Parent ***************************** //

    var frame: Frame? = null
        internal set (frame) {
            val old = field
            field = frame
            parentChanged(old, frame)
        }

    internal abstract fun parentChanged(old: Frame?, new: Frame?)

    var parent: AWTContainerElement? = null
        internal set (parent) {
            field = parent

            if (parent != null) {
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
        return parent.into()
    }

    override fun inheritanceParent(): Option<Element> {
        return parent.into()
    }

    override fun pseudoElement(): Option<PseudoElement> {
        return None
    }

    override fun isRoot(): Boolean {
        return parent == null
    }

    private fun reapplyFont() {
        val style = getStyle()

        val values = when (style) {
            is Some -> style.value
            is None -> return
        }

        val font = values.font
        val fontSize = font.fontSize.size().toPx().toInt()

        var awtFont: Font? = null

        for (fontFamily in values.font.fontFamily.values) {
            awtFont = when (fontFamily) {
                is SingleFontFamily.Generic -> {
                    when (fontFamily.name) {
                        "serif" -> Font("Times", 0, fontSize)
                        "sans-serif" -> Font("Helvetica", 0, fontSize)
                        "monospace" -> Font("Courier", 0, fontSize)
                        else -> Font("Times", 0, fontSize)
                    }
                }
                is SingleFontFamily.FamilyName -> {
                    Font(fontFamily.name.value, 0, fontSize)
                }
            }
        }

        if (awtFont != null) {
            component.font = awtFont
        }

        component.foreground = values.color.color.toAWTColor()
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

    val classes: MutableList<String> = mutableListOf()

    override fun classes(): List<String> {
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
        val elementData = getData()

        val data = when (elementData) {
            is Some -> elementData.value
            is None -> return None
        }

        return data.styles.primary
    }

    override fun finishRestyle(context: StyleContext, data: ElementData, elementStyles: ResolvedElementStyles) {
        val oldStyle = data.setStyles(elementStyles)

        val primaryStyle = getStyle().unwrap()

        updateStyle(primaryStyle)

        if (isRoot()) {
            val fontSize = primaryStyle.font.fontSize

            if (oldStyle.primary.mapOr({ style -> style.font.fontSize != fontSize }, false)) {
                context.device.setRootFontSize(fontSize.size())
            }
        }

        reapplyFont()

        for ((i, style) in data.styles.pseudos.iter().withIndex()) {
            if (style is Some) {
                updatePseudoElement(PseudoElement.fromEagerOrdinal(i), style.value)
            }
        }

        restyle.fire(primaryStyle)
    }

    protected open fun updateStyle(style: ComputedValues) {

    }

    protected open fun updatePseudoElement(pseudoElement: PseudoElement, style: ComputedValues) {

    }

    // ***************************** Render ***************************** //

    val cache: RenderCache by lazy { RenderCache() }

    fun paintBackground(component: Component, g: Graphics) {
        if (isDirty()) {
            frame?.pulse()
        }

        renderBackground(g, component, this, getStyle())
    }

    fun paintBorder(component: Component, g: Graphics) {
        renderBorder(g, component, this, getStyle())
    }

    // ***************************** Renderer Override ***************************** //

    fun hoverHint(hover: Boolean): Boolean {
        val old = this.hover
        this.hover = hover

        if (old != hover) {
            reapplyCSS()
        }

        return old
    }

    fun focusHint(focus: Boolean): Boolean {
        val old = this.focus
        this.focus = focus

        if (old != focus) {
            reapplyCSS()
        }

        return old
    }

    protected var active: Boolean = false

    fun activeHint(active: Boolean): Boolean {
        val old = this.active
        this.active = active

        if (old != active) {
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

object SharedHoverHandler : AWTEventListener {

    init {
        Toolkit.getDefaultToolkit().addAWTEventListener(
            this,
            AWTEvent.MOUSE_MOTION_EVENT_MASK
        )
    }

    private var component: Component? = null

    override fun eventDispatched(event: AWTEvent) {
        fun <E> MutableList<E>.removeFirst(): E {
            return this.removeAt(0)
        }

        if (event !is MouseEvent || event.id != MouseEvent.MOUSE_MOVED) {
            return
        }

        val pick = if (event.source is Container) {
            val container = event.source as Container

            container.findComponentAt(event.point)
        } else {
            event.source as Component
        }

        if (pick == component) {
            return
        }

        val component = component
        this.component = pick

        val componentStack = component.selfAndAncestorsList()
        val pickStack = pick.selfAndAncestorsList()

        val maxCommon = componentStack.size.min(pickStack.size)

        for (i in maxCommon until componentStack.size) {
            val exitedComponent = componentStack.removeFirst()

            exitedComponent.element.hoverHint(false)
        }

        for (i in maxCommon until pickStack.size) {
            val enteredComponent = pickStack.removeFirst()

            enteredComponent.element.hoverHint(true)
        }

        for (i in 0 until maxCommon) {
            val exitedComponent = componentStack.removeFirst()
            val enteredComponent = pickStack.removeFirst()

            if (exitedComponent == enteredComponent) {
                return
            } else {
                exitedComponent.element.hoverHint(false)
                enteredComponent.element.hoverHint(true)
            }
        }
    }
}

private fun Component?.selfAndAncestorsList(): MutableList<Component> {
    val stack: MutableList<Component> = mutableListOf()

    if (this == null) {
        return stack
    }

    for (component in this.selfAndAncestorsIterator()) {
        stack.add(component)
    }

    return stack
}

private fun Component.selfAndAncestorsIterator(): Iterator<Component> {
    return SelfAndAncestorIterator(this)
}

private class SelfAndAncestorIterator(private var component: Component) : Iterator<Component> {

    override fun hasNext(): Boolean {
        return component.parent != null
    }

    override fun next(): Component {
        val current = component
        component = component.parent
        return current
    }
}

private val elements: MutableMap<Component, AWTComponentElement> = WeakHashMap()

fun registerElement(component: Component, element: AWTComponentElement) {
    elements[component] = element
}

fun deregisterElement(component: Component) {
    elements.remove(component)
}

private fun ensureElement(component: Component): AWTComponentElement {
    val element = elements[component]

    return if (element == null) {
        val new = when (component) {
            is CellRendererPane -> CellRendererPaneElement(component)
            is ModernCellRenderPane -> ModernCellRendererPaneElement(component)
            is JLayeredPane -> LayeredPaneElement(component)
            is Container -> AWTContainerElement(component)
            else -> throw IllegalArgumentException("unsupported component ${component.javaClass.name}")
        }

        elements[component] = new

        new
    } else {
        element
    }
}

@Deprecated(message = "Element is now a extension attribute", level = DeprecationLevel.ERROR)
fun Component.into(): AWTComponentElement {
    return ensureElement(this)
}

val Component.element: AWTComponentElement
    get() = ensureElement(this)

enum class StyleState {

    CLEAN,

    REAPPLY,

    DIRTY_BRANCH
}