package org.fernice.reflare.element

import fernice.reflare.CSSEngine
import fernice.std.None
import fernice.std.Option
import fernice.std.Some
import fernice.std.mapOr
import fernice.std.unwrap
import org.fernice.flare.dom.Element
import org.fernice.flare.dom.ElementData
import org.fernice.flare.dom.ElementStyles
import org.fernice.flare.selector.NamespaceUrl
import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.selector.PseudoElement
import org.fernice.flare.std.min
import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.MatchingResult
import org.fernice.flare.style.PerPseudoElementMap
import org.fernice.flare.style.ResolvedElementStyles
import org.fernice.flare.style.context.StyleContext
import org.fernice.flare.style.properties.PropertyDeclarationBlock
import org.fernice.flare.style.value.computed.SingleFontFamily
import org.fernice.reflare.geom.Insets
import org.fernice.reflare.geom.toInsets
import org.fernice.reflare.render.Cache
import org.fernice.reflare.render.renderBackground
import org.fernice.reflare.render.renderBorder
import org.fernice.reflare.toAWTColor
import java.awt.AWTEvent
import java.awt.Component
import java.awt.Container
import java.awt.Font
import java.awt.Graphics
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.AWTEventListener
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.MouseEvent
import java.util.WeakHashMap
import java.util.concurrent.CopyOnWriteArrayList
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
                invalidateStyle()
            }

            override fun focusGained(e: FocusEvent) {
                invalidateStyle()
            }
        })

        component.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                invalidateBounds()
            }
        })

        SharedHoverHandler
    }

    // ***************************** Dirty ***************************** //

    private var state: StyleState = StyleState.CLEAN

    fun invalidateBounds() {
        paddingDelegate.invalidate()
        marginDelegate.invalidate()

        cache.invalidateBounds()
        component.repaint()
    }

    fun invalidateStyle() {
        restyle()
    }

    fun restyle() {
        val frame = frame

        when (frame) {
            is Some -> frame.value.markElementDirty(this)
            else -> invokeLater {
                CSSEngine.styleWithLocalContext(this)
            }
        }
    }

    fun restyleImmediately() {
        val frame = frame

        when (frame) {
            is Some -> frame.value.applyStyles(this)
            else -> CSSEngine.styleWithLocalContext(this)
        }
    }

    fun getMatchingStyles(): MatchingResult {
        val frame = frame

        return when (frame) {
            is Some -> frame.value.matchStyle(this)
            else -> CSSEngine.matchStyleWithLocalContext(this)
        }
    }

    // ***************************** Frame & Parent ***************************** //

    var frame: Option<Frame> = None
        internal set (frame) {
            field = frame
            parentChanged(field, frame)
        }

    internal abstract fun parentChanged(old: Option<Frame>, new: Option<Frame>)

    var parent: Option<AWTContainerElement> = None
        internal set (parent) {
            field = parent
        }

    override fun parent(): Option<Element> {
        return parent
    }

    override fun owner(): Option<Element> {
        return Some(this)
    }

    override fun traversalParent(): Option<Element> {
        return parent
    }

    override fun inheritanceParent(): Option<Element> {
        return parent
    }

    override fun pseudoElement(): Option<PseudoElement> {
        return None
    }

    override fun isRoot(): Boolean {
        return parent.isNone()
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
        val own = this.id

        return when (own) {
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

    var styleAttribute: Option<PropertyDeclarationBlock> = None

    override fun styleAttribute(): Option<PropertyDeclarationBlock> {
        return styleAttribute
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

        restyleListener.forEach { listener -> listener.restyleFinished(this) }
    }

    protected open fun updateStyle(style: ComputedValues) {
        paddingDelegate.invalidate()
        marginDelegate.invalidate()
    }

    protected open fun updatePseudoElement(pseudoElement: PseudoElement, style: ComputedValues) {

    }

    private val restyleListener: MutableList<RestyleListener> = mutableListOf()

    fun addRestyleListener(listener: RestyleListener) {
        restyleListener.add(listener)
    }

    fun removeRestyleListener(listener: RestyleListener) {
        restyleListener.remove(listener)
    }

    // ***************************** Render ***************************** //

    val cache: Cache by lazy { Cache(this) }

    fun paintBackground(component: Component, g: Graphics) {
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
            invalidateStyle()
        }

        return old
    }

    fun focusHint(focus: Boolean): Boolean {
        val old = this.focus
        this.focus = focus

        if (old != focus) {
            invalidateStyle()
        }

        return old
    }

    protected var active: Boolean = false

    fun activeHint(active: Boolean): Boolean {
        val old = this.active
        this.active = active

        if (old != active) {
            invalidateStyle()
        }

        return old
    }

    // ***************************** Style Properties ***************************** //

    private val marginDelegate = cssProperty(Insets.empty()) { styles ->
        styles.margin.toInsets(component.bounds)
    }

    val margin: Insets by marginDelegate

    private val paddingDelegate = cssProperty(Insets.empty()) { styles ->
        styles.padding.toInsets(component.bounds)
    }

    val padding: Insets by paddingDelegate

    private val fontSizeDelegate = cssProperty(16) { styles ->
        styles.font.fontSize.size().toPx().toInt()
    }

    val fontSize by fontSizeDelegate
}

open class AWTContainerElement(container: Container) : AWTComponentElement(container) {

    private val children: MutableList<AWTComponentElement> = CopyOnWriteArrayList()

    init {
        container.addContainerListener(object : ContainerListener {
            override fun componentAdded(e: ContainerEvent) {
                childAdded(e.child)
            }

            override fun componentRemoved(e: ContainerEvent) {
                childRemoved(e.child)
            }
        })

        for (child in container.components) {
            childAdded(child)
        }
    }

    private fun childAdded(child: Component) {
        val childElement = child.element

        val container = component as Container
        val index = container.getComponentZOrder(child)

        childElement.frame = frame
        childElement.parent = Some(this)
        children.add(index, childElement)

        invalidateStyle()
    }

    private fun childRemoved(child: Component) {
        val childElement = child.element

        childElement.frame = None
        childElement.parent = None
        children.remove(childElement)

        invalidateStyle()
    }

    fun addVirtualChild(childElement: AWTComponentElement) {
        childElement.frame = frame
        childElement.parent = Some(this)
    }

    fun removeVirtualChild(childElement: AWTComponentElement) {
        childElement.frame = None
        childElement.parent = None
    }

    final override fun parentChanged(old: Option<Frame>, new: Option<Frame>) {
        for (child in children) {
            child.frame = new
        }
    }

    override fun children(): List<Element> {
        return children
    }

    override fun previousSibling(): Option<Element> {
        val parent = parent()

        return when (parent) {
            is Some -> {
                val children = parent.value.children()

                val index = children.indexOf(this) - 1

                if (index >= 0) {
                    Some(children[index])
                } else {
                    None
                }
            }
            is None -> parent
        }
    }

    override fun nextSibling(): Option<Element> {
        val parent = parent()

        return when (parent) {
            is Some -> {
                val children = parent.value.children()

                val index = children.indexOf(this) + 1

                if (index < children.size) {
                    Some(children[index])
                } else {
                    None
                }
            }
            is None -> parent
        }
    }

    override fun isEmpty(): Boolean {
        return children.isEmpty()
    }

    // ***************************** Matching ***************************** //

    // In theory it is possible to construct a Container meaning that needs a
    // local name to styled. In practice hopefully no one will try to do it
    // because even though the element will be considered when it comes to
    // matching, we have no means to render using its computed styles.
    override fun localName(): String {
        return "container"
    }
}

abstract class ComponentElement(component: JComponent) : AWTContainerElement(component) {

    init {
        component.addAncestorListener(object : AncestorListener {
            override fun ancestorAdded(event: AncestorEvent) {
                if (event.ancestor is Window) {
                    val frame = event.ancestor as Window

                    frame.into()
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

        val pick = event.source as Component

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

@Deprecated(message = "Element is now a extension attribute")
fun Component.into(): AWTComponentElement {
    return ensureElement(this)
}

val Component.element: AWTComponentElement
    get() = ensureElement(this)

enum class StyleState {

    CLEAN,

    UPDATE,

    REAPPLY,

    DIRTY_BRANCH
}

interface RestyleListener {

    fun restyleFinished(element: AWTComponentElement)
}