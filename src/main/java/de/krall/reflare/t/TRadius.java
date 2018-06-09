package de.krall.reflare.t;

public class TRadius {

    public float topLeftWidth;
    public float topLeftHeight;
    public float topRightWidth;
    public float topRightHeight;
    public float bottomRightWidth;
    public float bottomRightHeight;
    public float bottomLeftWidth;
    public float bottomLeftHeight;

    public TRadius() {

    }

    public TRadius(final float topLeftWidth, final float topLeftHeight, final float topRightWidth, final float topRightHeight, final float bottomRightWidth,
            final float bottomRightHeight, final float bottomLeftWidth, final float bottomLeftHeight) {
        this.topLeftWidth = topLeftWidth;
        this.topLeftHeight = topLeftHeight;
        this.topRightWidth = topRightWidth;
        this.topRightHeight = topRightHeight;
        this.bottomRightWidth = bottomRightWidth;
        this.bottomRightHeight = bottomRightHeight;
        this.bottomLeftWidth = bottomLeftWidth;
        this.bottomLeftHeight = bottomLeftHeight;
    }

    public TRadius set(final float topLeftWidth, final float topLeftHeight, final float topRightWidth, final float topRightHeight, final float bottomRightWidth,
            final float bottomRightHeight, final float bottomLeftWidth, final float bottomLeftHeight) {
        this.topLeftWidth = topLeftWidth;
        this.topLeftHeight = topLeftHeight;
        this.topRightWidth = topRightWidth;
        this.topRightHeight = topRightHeight;
        this.bottomRightWidth = bottomRightWidth;
        this.bottomRightHeight = bottomRightHeight;
        this.bottomLeftWidth = bottomLeftWidth;
        this.bottomLeftHeight = bottomLeftHeight;

        return this;
    }

    public float getTopLeftWidth() {
        return topLeftWidth;
    }

    public void setTopLeftWidth(final float topLeftWidth) {
        this.topLeftWidth = topLeftWidth;
    }

    public float getTopLeftHeight() {
        return topLeftHeight;
    }

    public void setTopLeftHeight(final float topLeftHeight) {
        this.topLeftHeight = topLeftHeight;
    }

    public float getTopRightWidth() {
        return topRightWidth;
    }

    public void setTopRightWidth(final float topRightWidth) {
        this.topRightWidth = topRightWidth;
    }

    public float getTopRightHeight() {
        return topRightHeight;
    }

    public void setTopRightHeight(final float topRightHeight) {
        this.topRightHeight = topRightHeight;
    }

    public float getBottomRightWidth() {
        return bottomRightWidth;
    }

    public void setBottomRightWidth(final float bottomRightWidth) {
        this.bottomRightWidth = bottomRightWidth;
    }

    public float getBottomRightHeight() {
        return bottomRightHeight;
    }

    public void setBottomRightHeight(final float bottomRightHeight) {
        this.bottomRightHeight = bottomRightHeight;
    }

    public float getBottomLeftWidth() {
        return bottomLeftWidth;
    }

    public void setBottomLeftWidth(final float bottomLeftWidth) {
        this.bottomLeftWidth = bottomLeftWidth;
    }

    public float getBottomLeftHeight() {
        return bottomLeftHeight;
    }

    public void setBottomLeftHeight(final float bottomLeftHeight) {
        this.bottomLeftHeight = bottomLeftHeight;
    }
}
