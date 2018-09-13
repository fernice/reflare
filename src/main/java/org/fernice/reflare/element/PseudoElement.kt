package org.fernice.reflare.element

import org.fernice.flare.dom.Element
import org.fernice.flare.dom.ElementData
import org.fernice.flare.dom.ElementStyles
import org.fernice.flare.selector.NamespaceUrl
import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.PerPseudoElementMap
import org.fernice.flare.style.properties.PropertyDeclarationBlock
import fernice.std.None
import fernice.std.Option
import fernice.std.Some
import fernice.std.unwrap

abstract class FlarePseudoElement(private val owner: AWTComponentElement) : Element {

    var namespace: Option<NamespaceUrl> = None

    override fun namespace(): Option<NamespaceUrl> {
        return namespace
    }

    override fun id(): Option<String> {
        return None
    }

    override fun hasID(id: String): Boolean {
        return false
    }

    override fun classes(): List<String> {
        return emptyList()
    }

    override fun hasClass(styleClass: String): Boolean {
        return false
    }

    override fun matchNonTSPseudoClass(pseudoClass: NonTSPseudoClass): Boolean {
        return owner.matchNonTSPseudoClass(pseudoClass)
    }

    override fun isRoot(): Boolean {
        return false
    }

    override fun isEmpty(): Boolean {
        return true
    }

    override fun parent(): Option<Element> {
        return None
    }

    override fun owner(): Option<Element> {
        return Some(owner)
    }

    override fun traversalParent(): Option<Element> {
        return Some(owner)
    }

    override fun inheritanceParent(): Option<Element> {
        return Some(owner)
    }

    override fun previousSibling(): Option<Element> {
        return None
    }

    override fun nextSibling(): Option<Element> {
        return None
    }

    override fun children(): List<Element> {
        return emptyList()
    }

    override fun styleAttribute(): Option<PropertyDeclarationBlock> {
        return None
    }

    private var data: Option<ElementData> = None

    override fun ensureData(): ElementData {
        return when (data) {
            is Some -> data.unwrap()
            is None -> {
                val new = ElementData(ElementStyles(None, PerPseudoElementMap()))
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
}