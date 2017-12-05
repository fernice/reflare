package de.reflare.lnf;

import flare.graph.Node;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

public class FlareBorder extends AbstractBorder {

    private transient FlareUI ui;

    public FlareBorder(final FlareUI ui) {
        this.ui = ui;
    }

    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {

    }

    @Override
    public Insets getBorderInsets(final Component c, Insets insets) {
        if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        } else {
            insets.top = 0;
            insets.right = 0;
            insets.bottom = 0;
            insets.left = 0;
        }

        final Node cssProperties = ui.getBridge();

        combine(insets, cssProperties.getMargin());
        combine(insets, cssProperties.getMargin());

        final Border border = cssProperties.getBorder();

        if (border != null) {
            combine(insets, border.getWidth());
        }

        if (c instanceof JTextComponent) {
            combine(insets, ((JTextComponent) c).getMargin());
        } else if (c instanceof AbstractButton) {
            combine(insets, ((AbstractButton) c).getMargin());
        } else if (c instanceof JToolBar) {
            combine(insets, ((JToolBar) c).getMargin());
        } else if (c instanceof JMenuBar) {
            combine(insets, ((JMenuBar) c).getMargin());
        }

        return insets;
    }

    private void combine(final Insets insets, final Insets addition) {
        if (addition != null) {
            insets.top += addition.top;
            insets.right += addition.right;
            insets.bottom += addition.bottom;
            insets.left += addition.left;
        }
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
