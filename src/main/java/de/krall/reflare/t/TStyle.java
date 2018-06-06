package de.krall.reflare.t;

import de.krall.reflare.value.computed.Style;

public class TStyle {

    public Style top;
    public Style right;
    public Style bottom;
    public Style left;

    public TStyle() {

    }

    public TStyle(final Style top, final Style right, final Style bottom, final Style left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public Style getTop() {
        return top;
    }

    public void setTop(final Style top) {
        this.top = top;
    }

    public Style getRight() {
        return right;
    }

    public void setRight(final Style right) {
        this.right = right;
    }

    public Style getBottom() {
        return bottom;
    }

    public void setBottom(final Style bottom) {
        this.bottom = bottom;
    }

    public Style getLeft() {
        return left;
    }

    public void setLeft(final Style left) {
        this.left = left;
    }
}
