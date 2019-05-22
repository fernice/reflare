package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.PanelElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlarePanelUI extends BasicPanelUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlarePanelUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JPanel panel) {
        if (element == null) {
            element = new PanelElement(panel);
        }

        FlareUIHelper.installDefaults(this,panel);

        StyleTreeElementLookup.registerElement(panel, this);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                FocusManager.getCurrentManager().clearFocusOwner();
            }
        });
    }

    @Override
    protected void uninstallDefaults(JPanel panel) {
        StyleTreeElementLookup.deregisterElement(panel);
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        element.paintBackground(component, graphics);
    }

    @Override
    public void paintBorder(@NotNull final Component c, @NotNull final Graphics g, final int x, final int y, final int width, final int height) {
        element.paintBorder(c, g);
    }

    @NotNull
    @Override
    public ComponentElement getElement() {
        return element;
    }
}
