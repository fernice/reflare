package de.flare.render.rf;

import de.flare.render.Graphics;
import de.flare.render.Presentable;
import de.flare.render.RTTexture;
import de.flare.render.ResourceFactory;
import de.flare.render.ViewState;
import de.flare.render.sw.SWGraphics;
import javax.swing.JComponent;

public class RFResourceFactory implements ResourceFactory {

    @Override
    public Presentable createPresentable(final ViewState state) {
        return null;
    }

    @Override
    public RTTexture createRTTexture(final int width, final int height) {
        return null;
    }


    public RFDelegateTexture createRTTexture(final JComponent component, final java.awt.Graphics awtGraphics) {
        final Graphics graphics = SWGraphics.create(awtGraphics);

        return new RFDelegateTexture(graphics, component.getWidth(), component.getHeight());
    }
}
