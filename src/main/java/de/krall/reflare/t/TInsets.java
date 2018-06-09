package de.krall.reflare.t;

public class TInsets {

    public float top;
    public float right;
    public float bottom;
    public float left;

    public TInsets() {

    }

    public TInsets(final float top, final float right, final float bottom, final float left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public TInsets set(float top, float right, float bottom, float left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;

        return this;
    }

    public boolean isZero() {
        return top == 0 && right == 0 && bottom == 0 && left == 0;
    }

    public float getTop() {
        return top;
    }

    public void setTop(final float top) {
        this.top = top;
    }

    public float getRight() {
        return right;
    }

    public void setRight(final float right) {
        this.right = right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(final float bottom) {
        this.bottom = bottom;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(final float left) {
        this.left = left;
    }

    public void increase(final TInsets insets) {
        top += insets.top;
        right += insets.right;
        bottom += insets.bottom;
        left += insets.left;
    }
}
