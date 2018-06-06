package de.krall.reflare.t;

import java.awt.Dimension;
import java.awt.Rectangle;

public class TBounds {

    public float x;
    public float y;
    public float width;
    public float height;

    public TBounds(){

    }

    public TBounds(final Rectangle rectangle){
        this.x = rectangle.x;
        this.y = rectangle.y;
        this.width = rectangle.width;
        this.height = rectangle.height;
    }

    public TBounds(final Dimension dimension){
        this.width = dimension.width;
        this.height = dimension.height;
    }

    public TBounds(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public void setX(final float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(final float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(final float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(final float height) {
        this.height = height;
    }

    public void reduce(final TInsets insets){
        x += insets.left;
        y += insets.top;
        width -= insets.left + insets.right;
        height -= insets.top + insets.bottom;
    }
}
