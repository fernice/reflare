package de.krall.reflare.element

import de.krall.flare.dom.Element
import de.krall.flare.dom.ElementData
import de.krall.flare.dom.ElementStyles
import de.krall.flare.selector.NamespaceUrl
import de.krall.flare.selector.NonTSPseudoClass
import de.krall.flare.selector.PseudoElement
import de.krall.flare.std.None
import de.krall.flare.std.Option
import de.krall.flare.std.Some
import de.krall.flare.std.unwrap
import de.krall.flare.style.ComputedValues
import de.krall.flare.style.properties.PropertyDeclarationBlock
import de.krall.flare.style.value.computed.SingleFontFamily
import de.krall.reflare.render.renderBackground
import de.krall.reflare.render.renderBorder
import de.krall.reflare.toAWTColor
import java.awt.Component
import java.awt.Container
import java.awt.Font
import java.awt.Graphics
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.*
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLayeredPane
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener
import java.awt.Color as AWTColor

abstract class AWTComponentElement(val component: Component) : Element {

    fun <C : JComponent> component(): C {
        @Suppress("UNCHECKED_CAST")
        return component as C
    }

    init {
        component.addFocusListener(object : FocusListener {
            override fun focusLost(e: FocusEvent) {
                reapplyCss()
            }

            override fun focusGained(e: FocusEvent) {
                reapplyCss()
            }
        })
    }

    // ***************************** Dirty ***************************** //

    private var state: StyleState = StyleState.CLEAN

    fun reapplyCss() {
        restyle()
    }

    // ***************************** Frame & Parent ***************************** //

    var frame: Option<Frame> = None()
        internal set (frame) {
            field = frame
            parentChanged(field, frame)
        }

    internal abstract fun parentChanged(old: Option<Frame>, new: Option<Frame>)

    var parent: Option<AWTContainerElement> = None()
        internal set

    fun restyle() {
        val frame = frame

        when (frame) {
            is Some -> frame.value.restyle()
        }
    }

    override fun parent(): Option<Element> {
        return parent
    }

    override fun owner(): Option<Element> {
        return parent
    }

    override fun inheritanceParent(): Option<Element> {
        return parent
    }

    override fun pseudoElement(): Option<PseudoElement> {
        return None()
    }

    override fun isRoot(): Boolean {
        return parent.isNone()
    }

    internal open fun reapplyFont() {
        val style = getStyle()

        val values = when (style) {
            is Some -> style.value
            is None -> return
        }

        val font = values.font
        val fontSize = font.fontSize.size().toPx().toInt()

        for (fontFamily in values.font.fontFamily.values) {
            when (fontFamily) {
                is SingleFontFamily.Generic -> {
                    component.font = when (fontFamily.name) {
                        "serif" -> Font("Times", 0, fontSize)
                        "sans-serif" -> Font("Helvetica", 0, fontSize)
                        "monospace" -> Font("Courier", 0, fontSize)
                        else -> Font("Times", 0, fontSize)
                    }
                }
                is SingleFontFamily.FamilyName -> {
                    component.font = Font(fontFamily.name.value, 0, fontSize)
                }
            }
        }

        component.foreground = values.color.color.toAWTColor()
    }

    // ***************************** Matching ***************************** //

    var namespace: Option<NamespaceUrl> = None()

    override fun namespace(): Option<NamespaceUrl> {
        return namespace
    }

    var id: Option<String> = None()

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

    val classes: List<String> = mutableListOf()

    override fun classes(): List<String> {
        return classes
    }

    override fun hasClass(styleClass: String): Boolean {
        return classes.contains(styleClass)
    }

    override fun matchPseudoElement(pseudoElement: PseudoElement): Boolean {
        return false
    }

    override fun matchNonTSPseudoClass(pseudoClass: NonTSPseudoClass): Boolean {
        return when (pseudoClass) {
            is NonTSPseudoClass.Enabled -> component.isEnabled
            is NonTSPseudoClass.Disabled -> !component.isEnabled
            is NonTSPseudoClass.Focus -> component.isFocusOwner
            else -> false
        }
    }

    // ***************************** Inline ***************************** //

    var styleAttribute: Option<PropertyDeclarationBlock> = None()

    override fun styleAttribute(): Option<PropertyDeclarationBlock> {
        return styleAttribute
    }

    // ***************************** Data ***************************** //

    private var data: Option<ElementData> = None()

    override fun ensureData(): ElementData {
        return when (data) {
            is Some -> data.unwrap()
            is None -> {
                val new = ElementData(ElementStyles(None()))
                data = Some(new)

                new
            }
        }
    }

    override fun getData(): Option<ElementData> {
        return data
    }

    override fun clearData() {
        data = None()
    }

    fun getStyle(): Option<ComputedValues> {
        val elementData = getData()

        val data = when (elementData) {
            is Some -> elementData.value
            is None -> return None()
        }

        return data.getStyles().primary
    }

    fun paintBackground(component: Component, g: Graphics) {
        renderBackground(g, component, getStyle())
    }

    fun paintBorder(component: Component, g: Graphics) {
        renderBorder(g, component, getStyle())
    }
}

open class AWTContainerElement(container: Container) : AWTComponentElement(container) {

    private val children: MutableList<AWTComponentElement> = mutableListOf()

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
        val childElement = child.into()

        val container = component as Container
        val index = container.getComponentZOrder(child)

        childElement.parent = parent
        children.add(index, childElement)

        restyle()
    }

    private fun childRemoved(child: Component) {
        val childElement = child.into()

        childElement.parent = None()
        children.remove(childElement)
    }

    final override fun parentChanged(old: Option<Frame>, new: Option<Frame>) {
        for (child in children) {
            child.frame = new
        }
    }

    override fun reapplyFont() {
        super.reapplyFont()

        for (child in children) {
            child.reapplyFont()
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
                    None()
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
                    None()
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
                if (event.ancestor is JFrame) {
                    val frame = event.ancestor as JFrame

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

fun Component.into(): AWTComponentElement {
    return ensureElement(this)
}

enum class StyleState {

    CLEAN,

    UPDATE,

    REAPPLY,

    DIRTY_BRANCH
}