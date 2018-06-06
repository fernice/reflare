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
import de.krall.flare.style.properties.PropertyDeclarationBlock
import javax.swing.JComponent

abstract class ComponentElement(val component: JComponent) : Element {

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

    var parent: Option<ComponentElement> = None()

    override fun isRoot(): Boolean {
        return parent.isNone()
    }

    override fun parent(): Option<Element> {
        return parent
    }

    override fun owner(): Option<Element> {
        return None()
    }

    override fun inheritanceParent(): Option<Element> {
        // This probably does not cover all cases (excluding pseudo elements) as
        // in swing there might be components we never want to inherit from
        return parent
    }

    override fun isEmpty(): Boolean {
        return component.componentCount == 0
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

    val children: List<Element> = mutableListOf()

    override fun children(): List<Element> {
        return children
    }

    var styleAttribute: Option<PropertyDeclarationBlock> = None()

    override fun styleAttribute(): Option<PropertyDeclarationBlock> {
        return styleAttribute
    }

    override fun pseudoElement(): Option<PseudoElement> {
        return None()
    }

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
}