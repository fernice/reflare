package de.reflare.lnf.ui;

import de.flare.helper.NodeHelper;
import de.flare.peer.PGRegion;
import de.flare.render.rf.RFComponentPainter;
import de.flare.render.rf.RFRenderPipeline;
import de.reflare.lnf.FlareBorder;
import de.reflare.lnf.FlareUI;
import flare.graph.Node;
import flare.graph.Region;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class TextFieldUI extends BasicTextFieldUI implements FlareUI {

    public static ComponentUI createUI(JComponent c) {
        return new TextFieldUI();
    }

    private Region bridge;
    private PGRegion peer;

    @Override
    protected void installDefaults() {
        super.installDefaults();

        final JComponent component = getComponent();

        component.setOpaque(false);
        component.setBorder(new FlareBorder(this));

        bridge = new Region();

        // We need the helper here as getPeer() is protected
        peer = (PGRegion) NodeHelper.getPeer(bridge);
    }

    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();

        bridge = null;
        peer = null;
    }

    @Override
    protected void paintSafely(final Graphics graphics) {
        final RFComponentPainter componentPainter = RFRenderPipeline.getInstance().getSharedComponentPainter();

        componentPainter.paint(peer, getComponent(), graphics);

        super.paintSafely(graphics);
    }

    @Override
    protected void paintBackground(final Graphics g) {
        // already done in paintSafely()
    }

    @Override
    public Node getBridge() {
        return bridge;
    }

}
