/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fernice.reflare.style

import fernice.reflare.CSSEngine
import fernice.reflare.StyleRoot
import org.fernice.flare.dom.Device
import org.fernice.flare.dom.Element
import org.fernice.flare.dom.ElementStyles
import org.fernice.flare.selector.NamespaceUrl
import org.fernice.flare.selector.NonTSFPseudoClass
import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.selector.PseudoElement
import org.fernice.flare.style.ComputedValues
import org.fernice.flare.style.QuirksMode
import org.fernice.flare.style.context.StyleContext
import org.fernice.flare.style.source.StyleAttribute
import org.fernice.flare.style.stylesheet.CssRuleType
import org.fernice.flare.style.value.computed.Au
import org.fernice.flare.style.value.computed.Fill
import org.fernice.flare.style.value.generic.Size2D
import org.fernice.flare.url.Url
import org.fernice.reflare.awt.toAWTColor
import org.fernice.reflare.font.FontStyleResolver
import org.fernice.reflare.util.concurrentList
import org.fernice.reflare.util.observableMutableSetOf
import org.fernice.reflare.util.setAll
import java.awt.Color
import java.awt.Font
import java.lang.ref.WeakReference
import javax.swing.SwingUtilities
import org.fernice.flare.style.StyleRoot as StyleRootPeer

class StyleMatcher(
    localName: String? = null,
    id: String? = null,
    classes: Set<String> = setOf(),
) {

    var localName = localName
        set(value) {
            field = value
            notifyStyleInvalidated()
        }

    val classes: MutableSet<String> = observableMutableSetOf(classes) { notifyStyleInvalidated() }

    var id = id
        set(value) {
            field = value
            notifyStyleInvalidated()
        }

    private var styleAttributeBlock: StyleAttribute? = null
    var styleAttribute: String? = null
        set(value) {
            field = value

            styleAttributeBlock = if (value != null) {
                StyleAttribute.from(value, Url(""), QuirksMode.NoQuirks, CssRuleType.Style)
            } else {
                null
            }

            notifyStyleInvalidated()
        }

    var styleRoot: StyleRoot? = null
        set(value) {
            field = value
            notifyStyleInvalidated()
        }

    private val element = StyleMatcherElement()
    private val styles = mutableMapOf<Set<PseudoClass>, Style>()

    private val invalidationListeners = mutableListOf<StyleMatcherInvalidationListener>()
    private val invalidationListener = StyleInvalidationListener { notifyStyleInvalidated() }

    init {
        StyleMatcherEngine.addStyleInvalidationListener(invalidationListener)
    }

    private fun notifyStyleInvalidated() {
        styles.clear()

        SwingUtilities.invokeLater {
            invalidationListeners.forEach { it.styleChanged(this) }
        }
    }

    val style: Style
        get() = getStyle(emptySet())

    fun getStyle(vararg pseudoClasses: PseudoClass): Style = getStyle(setOf(*pseudoClasses))

    fun getStyle(pseudoClasses: Set<PseudoClass>): Style {
        return styles.computeIfAbsent(pseudoClasses) { matchStyle(it) }
    }

    private fun matchStyle(pseudoClasses: Set<PseudoClass>): Style {
        element.localName = localName ?: "-flr-style-matcher"
        element.id = id
        element.classes.setAll(classes)
        element.pseudoClasses.setAll(pseudoClasses.mapTo(mutableSetOf()) { it.toNonTSPseudoClass() })
        element.styleAttribute = styleAttributeBlock
        element.root.styleRoot = styleRoot?.peer

        val styles = StyleMatcherEngine.style(element)

        return styles.primary.toStyle()
    }

    private fun ComputedValues.toStyle(): Style {
        val font = FontStyleResolver.resolve(this.font)
        val color = this.color.color.toAWTColor()
        val backgroundColor = this.background.color.toAWTColor()
        val fill = when (val fill = this.color.fill) {
            is Fill.Color -> fill.rgba.toAWTColor()
            else -> null
        }

        return Style(this, font, color, backgroundColor, fill)
    }

    inner class Style(
        val computedValues: ComputedValues,
        val font: Font,
        val color: Color,
        val backgroundColor: Color,
        val fill: Color?,
    ) {
        operator fun invoke(vararg pseudoClass: PseudoClass): Style = getStyle(setOf(*pseudoClass))
    }

    fun addStyleInvalidationListener(listener: StyleMatcherInvalidationListener) {
        invalidationListeners.add(listener)
    }

    fun removeStyleInvalidationListener(listener: StyleMatcherInvalidationListener) {
        invalidationListeners.remove(listener)
    }

    companion object {

        @JvmStatic
        fun forStyleClasses(vararg classes: String): StyleMatcher = StyleMatcher(localName = null, id = null, classes = setOf(*classes))

        @JvmStatic
        fun forLocalName(localName: String): StyleMatcher = StyleMatcher(localName = localName, id = null, classes = emptySet())

        @JvmStatic
        fun forId(id: String): StyleMatcher = StyleMatcher(localName = null, id = id, classes = emptySet())

        @JvmStatic
        fun forLocalNameAndStyleClasses(localName: String, vararg classes: String): StyleMatcher =
            StyleMatcher(localName = localName, id = null, classes = setOf(*classes))

        @JvmStatic
        fun forIdAndStyleClasses(id: String, vararg classes: String): StyleMatcher =
            StyleMatcher(localName = null, id = id, classes = setOf(*classes))

        @JvmStatic
        fun forLocalNameAndId(localName: String, id: String): StyleMatcher =
            StyleMatcher(localName = localName, id = id, classes = emptySet())
    }
}

fun interface StyleMatcherInvalidationListener {
    fun styleChanged(styleMatcher: StyleMatcher)
}

enum class PseudoClass {

    Hover, Active, Focus, Checked, Enabled, Disabled, ReadWrite, ReadOnly, Visited
}

private fun PseudoClass.toNonTSPseudoClass(): NonTSPseudoClass {
    return when (this) {
        PseudoClass.Hover -> NonTSPseudoClass.Hover
        PseudoClass.Active -> NonTSPseudoClass.Active
        PseudoClass.Focus -> NonTSPseudoClass.Focus
        PseudoClass.Checked -> NonTSPseudoClass.Checked
        PseudoClass.Enabled -> NonTSPseudoClass.Enabled
        PseudoClass.Disabled -> NonTSPseudoClass.Disabled
        PseudoClass.ReadWrite -> NonTSPseudoClass.ReadWrite
        PseudoClass.ReadOnly -> NonTSPseudoClass.ReadOnly
        PseudoClass.Visited -> NonTSPseudoClass.Visited
    }
}

private class StyleMatcherRootElement : Element {

    override val namespace: NamespaceUrl? = null

    override val localName: String = "root"
    override val id: String? get() = null
    override val classes: Set<String> get() = emptySet()

    override fun hasID(id: String): Boolean = false
    override fun hasClass(styleClass: String): Boolean = false

    override fun hasPseudoElement(pseudoElement: PseudoElement): Boolean = false
    override fun matchPseudoElement(pseudoElement: PseudoElement): Boolean = false

    override fun matchNonTSPseudoClass(pseudoClass: NonTSPseudoClass): Boolean = false
    override fun matchNonTSFPseudoClass(pseudoClass: NonTSFPseudoClass): Boolean = false

    override fun isRoot(): Boolean = true
    override fun isLink(): Boolean = false

    override val owner: Element? get() = null
    override val parent: Element? get() = null

    override val traversalParent: Element? get() = null
    override val inheritanceParent: Element? get() = null

    override fun isEmpty(): Boolean = true

    override val previousSibling: Element? get() = null
    override val nextSibling: Element? get() = null
    override val children: List<Element> = listOf()
    override val firstChild: Element? get() = children.firstOrNull()
    override val lastChild: Element? get() = children.lastOrNull()

    override val styleAttribute: StyleAttribute? get() = null
    override var styleRoot: StyleRootPeer? = null

    override val pseudoElement: PseudoElement? get() = null

    private val localStyles = ThreadLocal<ElementStyles>()
    override val styles: ElementStyles?
        get() = localStyles.get()

    override fun finishRestyle(context: StyleContext, previousStyles: ElementStyles?, styles: ElementStyles) {
        localStyles.set(styles)
    }
}

private class StyleMatcherElement : Element {

    override val namespace: NamespaceUrl? = null

    override var localName: String = "-flr-style-matcher"
    override var id: String? = null
    override val classes: MutableSet<String> = mutableSetOf()

    override fun hasID(id: String): Boolean = id == this.id
    override fun hasClass(styleClass: String): Boolean = classes.contains(styleClass)

    val pseudoClasses = mutableSetOf<NonTSPseudoClass>()

    override fun hasPseudoElement(pseudoElement: PseudoElement): Boolean = false
    override fun matchPseudoElement(pseudoElement: PseudoElement): Boolean = false

    override fun matchNonTSPseudoClass(pseudoClass: NonTSPseudoClass): Boolean = pseudoClasses.contains(pseudoClass)
    override fun matchNonTSFPseudoClass(pseudoClass: NonTSFPseudoClass): Boolean = false

    override fun isRoot(): Boolean = false
    override fun isLink(): Boolean = false

    val root = StyleMatcherRootElement()

    override val owner: Element? get() = null
    override val parent: Element get() = root

    override val traversalParent: Element get() = root
    override val inheritanceParent: Element get() = root

    override fun isEmpty(): Boolean = true

    override val previousSibling: Element? get() = null
    override val nextSibling: Element? get() = null
    override val children: List<Element> = listOf()
    override val firstChild: Element? get() = null
    override val lastChild: Element? get() = null

    override var styleAttribute: StyleAttribute? = null
    override val styleRoot: StyleRootPeer? get() = null

    override val pseudoElement: PseudoElement? get() = null

    override var styles: ElementStyles? = null

    override fun finishRestyle(context: StyleContext, previousStyles: ElementStyles?, styles: ElementStyles) {
        this.styles = styles
    }
}

object StyleMatcherEngine {

    private val device = object : Device {
        override fun invalidate() = notifyStyleInvalidationListeners()

        override val viewportSize: Size2D<Au> = Size2D(Au.fromPx(64), Au.fromPx(64))
        override var rootFontSize: Au = Au.fromPx(16)
        override val systemFontSize: Au = Au.fromPx(12)
    }

    private val engine = CSSEngine.createEngine(device)

    internal fun style(element: Element): ElementStyles {
        require(element is StyleMatcherElement)

        val context = engine.createEngineContext()

        // style the parent
        engine.style(element.parent, context)
        // so it cascades to the element
        return engine.style(element, context)
    }

    private val invalidationListeners = concurrentList<WeakReference<StyleInvalidationListener>>()

    private fun notifyStyleInvalidationListeners() {
        removeVacatedStyleInvalidationListeners()
        for (reference in invalidationListeners) {
            val listener = reference.get()

            listener?.stylesInvalidated()
        }
    }

    @JvmStatic
    fun addStyleInvalidationListener(listener: StyleInvalidationListener) {
        removeVacatedStyleInvalidationListeners()
        invalidationListeners.add(WeakReference(listener))
    }

    @JvmStatic
    fun removeStyleInvalidationListener(listener: StyleInvalidationListener) {
        removeVacatedStyleInvalidationListeners()
        for (reference in invalidationListeners) {
            val candidate = reference.get()

            if (candidate === listener) {
                invalidationListeners.remove(reference)
                break
            }
        }
    }

    private fun removeVacatedStyleInvalidationListeners() {
        val vacated = mutableListOf<WeakReference<StyleInvalidationListener>>()
        for (reference in invalidationListeners) {
            val listener = reference.get()

            if (listener == null) {
                vacated.add(reference)
            }
        }
        invalidationListeners.removeAll(vacated)
    }
}

fun interface StyleInvalidationListener {
    fun stylesInvalidated()
}
