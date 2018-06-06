package de.krall.reflare.style;

import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import de.krall.reflare.std.Vec;
import de.krall.reflare.value.computed.Clip;
import java.awt.Color;

public class Background {

    private Color color;
    private Vec<Clip> clip;

    public Background() {
        color = Color.WHITE;
        clip = Vec.of(Clip.PADDING_BOX);
    }

    @DefinedBy(Api.CSS)
    public Color getColor() {
        return color;
    }

    @DefinedBy(Api.CASCADE)
    public void setColor(final Color color) {
        this.color = color;
    }

    @DefinedBy(Api.CSS)
    public Vec<Clip> getClip() {
        return clip;
    }

    @DefinedBy(Api.CASCADE)
    public void setClip(final Vec<Clip> clip) {
        this.clip = clip;
    }
}
