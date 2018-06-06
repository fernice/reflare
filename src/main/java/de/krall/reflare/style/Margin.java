package de.krall.reflare.style;

import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import de.krall.reflare.t.TInsets;

public class Margin {

    private TInsets margin = new TInsets(2,2,2,2);

    @DefinedBy(Api.CSS)
    public float getTop() {
        return margin.top;
    }

    @DefinedBy(Api.CASCADE)
    public void setTop(final float top) {
        margin.top = top;
    }

    @DefinedBy(Api.CSS)
    public float getRight() {
        return margin.right;
    }

    @DefinedBy(Api.CASCADE)
    public void setRight(final float right) {
        margin.right = right;
    }

    @DefinedBy(Api.CSS)
    public float getBottom() {
        return margin.bottom;
    }

    @DefinedBy(Api.CASCADE)
    public void setBottom(final float bottom) {
        margin.bottom = bottom;
    }

    @DefinedBy(Api.CSS)
    public float getLeft() {
        return margin.left;
    }

    @DefinedBy(Api.CASCADE)
    public void setLeft(final float left) {
        margin.left = left;
    }

    public TInsets getInsets() {
        return margin;
    }
}
