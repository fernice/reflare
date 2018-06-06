package de.krall.reflare.ui;

import de.krall.reflare.FlareBorder;
import de.krall.reflare.Styleable;
import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class TextFieldUI extends BasicTextFieldUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new TextFieldUI();
    }

    private Styleable styleable;

    @Override
    protected void installDefaults() {
        super.installDefaults();

        final JComponent component = getComponent();

        component.setOpaque(false);
        component.setBorder(new FlareBorder(this));

        styleable = new Styleable();
    }

    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
    }

    @Override
    protected void paintSafely(final Graphics graphics) {
        styleable.paintBackground(getComponent(), graphics);

        super.paintSafely(graphics);
    }

    @Override
    protected void paintBackground(final Graphics g) {
        // already done in paintSafely()
    }

    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
        styleable.paintBorder(c, g);
    }

    @Override
    public Styleable getStyleable() {
        return styleable;
    }
}
