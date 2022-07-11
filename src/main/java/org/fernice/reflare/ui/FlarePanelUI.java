package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

    private static final Object focusDismissClientPropertyKey = new Object();
    private static final MouseListener focusDismissListener = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            FocusManager.getCurrentManager().clearFocusOwner();
        }
    };

    public static void installFocusDismissHandling(@NotNull JPanel panel) {
        panel.addMouseListener(focusDismissListener);
        panel.putClientProperty(focusDismissClientPropertyKey, true);
    }

    public static void uninstallFocusDismissHandling(@NotNull JPanel panel) {
        panel.putClientProperty(focusDismissClientPropertyKey, null);
        panel.removeMouseListener(focusDismissListener);
    }

    public static boolean isFocusDismissHandlingInstalled(@NotNull JPanel panel) {
        return panel.getClientProperty(focusDismissClientPropertyKey) != null;
    }

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

        UIDefaultsHelper.installDefaultProperties(this, panel);

        StyleTreeElementLookup.registerElement(panel, this);

        installFocusDismissHandling(panel);
    }

    @Override
    protected void uninstallDefaults(JPanel panel) {
        StyleTreeElementLookup.deregisterElement(panel);
    }

    @Override
    public Dimension getMinimumSize(final JComponent c) {
        element.pulseForComputation();
        return super.getMinimumSize(c);
    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        element.pulseForComputation();
        return super.getPreferredSize(c);
    }

    @Override
    public Dimension getMaximumSize(final JComponent c) {
        element.pulseForComputation();
        return super.getMaximumSize(c);
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        element.paintBackground(graphics);
    }

    @Override
    public void paintBorder(@NotNull final Component c, @NotNull final Graphics g) {
        element.paintBorder(g);
    }

    @NotNull
    @Override
    public ComponentElement getElement() {
        return element;
    }
}
