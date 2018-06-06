package de.krall.reflare.style;

import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import de.krall.reflare.t.TColor;
import de.krall.reflare.t.TInsets;
import de.krall.reflare.t.TRadius;
import de.krall.reflare.t.TStyle;
import de.krall.reflare.value.computed.Style;
import java.awt.Color;

/**
 * This class is meant to be accessed from CSS only via its dedicated methods. This leaves us with the
 * freedom of internal implementation/representation. Our current model packs up all properties of the
 * same kind into one T object, but is free to change anytime in the future. Therefore only internal
 * implementation (i.e. the renderer) should be build specific to this class.
 */
public class Border {

    private static final Color test_transparent = new Color(0, 0, 0, 0);
    private static final Color test_green = new Color(41, 163, 136);

    private final TRadius radius = new TRadius(5, 5, 5, 5);
    private final TInsets width = new TInsets(2, 2, 2, 2);
    private final TColor color = new TColor(test_green, test_green, test_green, test_green);
    private final TStyle style = new TStyle();

    //********************************************
    // Top
    //********************************************

    @DefinedBy(Api.CSS)
    public float getTopWidth() {
        return width.top;
    }

    @DefinedBy(Api.CASCADE)
    public void setTopWidth(final float top) {
        width.top = top;
    }

    @DefinedBy(Api.CSS)
    public Color getTopColor() {
        return color.top;
    }

    @DefinedBy(Api.CASCADE)
    public void setTopColor(final Color top) {
        color.top = top;
    }

    @DefinedBy(Api.CSS)
    public Style getTopStyle() {
        return style.top;
    }

    @DefinedBy(Api.CASCADE)
    public void setTopStyle(final Style top) {
        style.top = top;
    }

    @DefinedBy(Api.CSS)
    public float getTopLeftRadius() {
        return radius.topLeft;
    }

    @DefinedBy(Api.CASCADE)
    public void setTopLeftRadius(final float radius) {
        this.radius.topLeft = radius;
    }

    @DefinedBy(Api.CSS)
    public float getTopRightRadius() {
        return radius.topRight;
    }

    @DefinedBy(Api.CASCADE)
    public void setTopRightRadius(final float radius) {
        this.radius.topRight = radius;
    }

    //********************************************
    // Right
    //********************************************

    @DefinedBy(Api.CSS)
    public float getRightWidth() {
        return width.right;
    }

    @DefinedBy(Api.CASCADE)
    public void setRightWidth(final float right) {
        width.right = right;
    }

    @DefinedBy(Api.CSS)
    public Color getRightColor() {
        return color.right;
    }

    @DefinedBy(Api.CASCADE)
    public void setRightColor(final Color right) {
        color.right = right;
    }

    @DefinedBy(Api.CSS)
    public Style getRightStyle() {
        return style.right;
    }

    @DefinedBy(Api.CASCADE)
    public void setRightStyle(final Style right) {
        style.right = right;
    }

    //********************************************
    // Bottom
    //********************************************

    @DefinedBy(Api.CSS)
    public float getBottomWidth() {
        return width.bottom;
    }

    @DefinedBy(Api.CASCADE)
    public void setBottomWidth(final float bottom) {
        width.bottom = bottom;
    }

    @DefinedBy(Api.CSS)
    public Color getBottomColor() {
        return color.bottom;
    }

    @DefinedBy(Api.CASCADE)
    public void setBottomColor(final Color bottom) {
        color.bottom = bottom;
    }

    @DefinedBy(Api.CSS)
    public Style getBottomStyle() {
        return style.bottom;
    }

    @DefinedBy(Api.CASCADE)
    public void setBottomStyle(final Style bottom) {
        style.bottom = bottom;
    }

    @DefinedBy(Api.CSS)
    public float getBottomRightRadius() {
        return radius.bottomRight;
    }

    @DefinedBy(Api.CASCADE)
    public void setBottomRightRadius(final float radius) {
        this.radius.bottomRight = radius;
    }

    @DefinedBy(Api.CSS)
    public float getBottomLeftRadius() {
        return radius.bottomLeft;
    }

    @DefinedBy(Api.CASCADE)
    public void setBottomLeftRadius(final float radius) {
        this.radius.bottomLeft = radius;
    }

    //********************************************
    // Left
    //********************************************

    @DefinedBy(Api.CSS)
    public float getLeftWidth() {
        return width.left;
    }

    @DefinedBy(Api.CASCADE)
    public void setLeftWidth(final float left) {
        width.left = left;
    }

    @DefinedBy(Api.CSS)
    public Color getLeftColor() {
        return color.left;
    }

    @DefinedBy(Api.CASCADE)
    public void setLeftColor(final Color left) {
        color.left = left;
    }

    @DefinedBy(Api.CSS)
    public Style getLeftStyle() {
        return style.left;
    }

    @DefinedBy(Api.CASCADE)
    public void setLeftStyle(final Style left) {
        style.left = left;
    }

    //********************************************
    // Renderer
    //********************************************

    public TInsets getWidth() {
        return width;
    }

    public TRadius getRadius() {
        return radius;
    }

    public TColor getColor() {
        return color;
    }

    public TStyle getStyle() {
        return style;
    }
}
