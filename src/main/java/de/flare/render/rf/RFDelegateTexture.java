package de.flare.render.rf;

import de.flare.render.Graphics;
import de.flare.render.RTTexture;

public class RFDelegateTexture implements RTTexture {

    private Graphics graphics;
    private double width;
    private double height;

    public RFDelegateTexture(final Graphics graphics, final double width, final double height) {
        this.graphics = graphics;
        this.width = width;
        this.height = height;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void dispose() {
        graphics.dispose();
    }
}
