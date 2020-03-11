package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.LabelElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.fernice.reflare.ui.text.FlareHTML;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareLabelUI extends BasicLabelUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareLabelUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults(JLabel label) {
        if (element == null) {
            element = new LabelElement(label);
        }

        UIDefaultsHelper.installDefaultProperties(this, label);

        StyleTreeElementLookup.registerElement(label, this);
    }

    @Override
    protected void uninstallDefaults(JLabel label) {
        StyleTreeElementLookup.deregisterElement(label);
    }

    @Override
    protected void installComponents(JLabel c) {
        //        super.installComponents(c);
        FlareHTML.updateRenderer(c, c.getText());
        c.setInheritsPopupMenu(true);
    }


    @Override
    public void propertyChange(@NotNull PropertyChangeEvent e) {
        String name = e.getPropertyName();
        if (name == "text" || "font" == name || "foreground" == name) {
            // remove the old html view client property if one
            // existed, and install a new one if the text installed
            // into the JLabel is html source.
            JLabel lbl = (JLabel) e.getSource();
            String text = lbl.getText();
            FlareHTML.updateRenderer(lbl, text);
        } else {
            super.propertyChange(e);
        }
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
        paintBackground(component, graphics);

        super.paint(graphics, component);
    }

    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        paintEnabledText(l, g, s, textX, textY);
    }

    private void paintBackground(JComponent component, Graphics g) {
        element.paintBackground(component, g);
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
