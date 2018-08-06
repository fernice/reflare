package modern.reflare.t;

import java.awt.Color;

public class TColor {

    public Color top;
    public Color right;
    public Color bottom;
    public Color left;

    public TColor(){

    }

    public TColor(final Color top, final Color right, final Color bottom, final Color left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public TColor set(final Color top, final Color right, final Color bottom, final Color left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;

        return this;
    }

    public Color getTop() {
        return top;
    }

    public void setTop(final Color top) {
        this.top = top;
    }

    public Color getRight() {
        return right;
    }

    public void setRight(final Color right) {
        this.right = right;
    }

    public Color getBottom() {
        return bottom;
    }

    public void setBottom(final Color bottom) {
        this.bottom = bottom;
    }

    public Color getLeft() {
        return left;
    }

    public void setLeft(final Color left) {
        this.left = left;
    }
}
