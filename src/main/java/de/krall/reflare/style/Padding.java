package de.krall.reflare.style;

import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import de.krall.reflare.t.TInsets;

public class Padding {

    private TInsets padding = new TInsets(3, 3, 3, 3);

    @DefinedBy(Api.CSS)
    public float getTop() {
        return padding.top;
    }

    @DefinedBy(Api.CASCADE)
    public void setTop(final float top) {
        padding.top = top;
    }

    @DefinedBy(Api.CSS)
    public float getRight() {
        return padding.right;
    }

    @DefinedBy(Api.CASCADE)
    public void setRight(final float right) {
        padding.right = right;
    }

    @DefinedBy(Api.CSS)
    public float getBottom() {
        return padding.bottom;
    }

    @DefinedBy(Api.CASCADE)
    public void setBottom(final float bottom) {
        padding.bottom = bottom;
    }

    @DefinedBy(Api.CSS)
    public float getLeft() {
        return padding.left;
    }

    @DefinedBy(Api.CASCADE)
    public void setLeft(final float left) {
        padding.left = left;
    }

    public TInsets getInsets() {
        return padding;
    }
}
