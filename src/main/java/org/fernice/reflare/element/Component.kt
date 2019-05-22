package org.fernice.reflare.element

import fernice.reflare.CSSEngine
import fernice.std.None
import fernice.std.Option
import fernice.std.Some
import fernice.std.mapOr
import fernice.std.unwrap
import org.fernice.flare.cssparser.Parser
import org.fernice.flare.cssparser.ParserInput
import org.fernice.flare.dom.Element
import org.fernice.flare.dom.ElementData
import org.fernice.flare.dom.ElementStyles
import org.fernice.flare.selector.NamespaceUrl
import org.fernice.flare.selector.NonTSPseudoClass
import org.fernice.flare.selector.PseudoElement
import org.fernice.flare.style.ComputedValues
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
import org.fernice.reflare.render.CellRendererPane as ModernCellRenderPane
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

    private var state: StyleState = StyleState.CLEAN

    fun invalidateStyle() {
        restyle()
    }

    fun forceRestyle() {
        forceApplyStyle = true
        fontStyle = FontStyle.initial

        restyleImmediately()
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
        val own = this.id

        return when (own) {
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

            restyle()
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

    private var forceApplyStyle = false

    override fun finishRestyle(context: StyleContext, data: ElementData, elementStyles: ResolvedElementStyles) {
        val oldStyle = data.setStyles(elementStyles)

        val primaryStyle = getStyle().unwrap()

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

        component.repaint()
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
        renderBackground(g, component, this, getStyle())
    }

    open fun paintBorder(component: Component, g: Graphics) {
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

enum class StyleState {

    CLEAN,

    UPDATE,

    REAPPLY,

    DIRTY_BRANCH
}