package de.flare.render.rf;

import de.flare.helper.PeerHelper;
import de.flare.peer.PGNode;
import de.flare.render.Graphics;
import de.flare.render.RTTexture;
import javax.swing.JComponent;

public class RFComponentPainter {

    private RFResourceFactory resourceFactory;

    public RFComponentPainter(final RFResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public void paint(final PGNode peer, final JComponent component, final java.awt.Graphics awtGraphics) {
        final RTTexture texture = resourceFactory.createRTTexture(component, awtGraphics);

        final Graphics graphics = texture.getGraphics();

        try {
            if (PeerHelper.prepare(peer, graphics)) {
                PeerHelper.renderContent(peer, graphics);
            }
        } finally {
            texture.dispose();
        }
    }
}
