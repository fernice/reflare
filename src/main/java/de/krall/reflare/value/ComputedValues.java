package de.krall.reflare.value;

import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import de.krall.reflare.style.Background;
import de.krall.reflare.style.Border;
import de.krall.reflare.style.Margin;
import de.krall.reflare.style.Padding;

public class ComputedValues {

    private Background background;
    private Border border;

    private Margin margin;
    private Padding padding;

    public ComputedValues(){
        background = new Background();
        border = new Border();
        margin = new Margin();
        padding = new Padding();
    }

    @DefinedBy(Api.CSS)
    public Background getBackground() {
        return background;
    }

    @DefinedBy(Api.CASCADE)
    public void setBackground(final Background background) {
        this.background = background;
    }

    @DefinedBy(Api.CSS)
    public Border getBorder() {
        return border;
    }

    @DefinedBy(Api.CASCADE)
    public void setBorder(final Border border) {
        this.border = border;
    }

    @DefinedBy(Api.CSS)
    public Margin getMargin() {
        return margin;
    }

    @DefinedBy(Api.CASCADE)
    public void setMargin(final Margin margin) {
        this.margin = margin;
    }

    @DefinedBy(Api.CSS)
    public Padding getPadding() {
        return padding;
    }

    @DefinedBy(Api.CASCADE)
    public void setPadding(final Padding padding) {
        this.padding = padding;
    }
}
