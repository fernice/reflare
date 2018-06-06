package de.krall.reflare.t;

public class TRadius {

    public float topLeft;
    public float topRight;
    public float bottomRight;
    public float bottomLeft;

    public TRadius(){

    }

    public TRadius(final float topLeft, final float topRight, final float bottomRight, final float bottomLeft) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.bottomLeft = bottomLeft;
    }

    public float getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(final float topLeft) {
        this.topLeft = topLeft;
    }

    public float getTopRight() {
        return topRight;
    }

    public void setTopRight(final float topRight) {
        this.topRight = topRight;
    }

    public float getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(final float bottomRight) {
        this.bottomRight = bottomRight;
    }

    public float getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(final float bottomLeft) {
        this.bottomLeft = bottomLeft;
    }
}
