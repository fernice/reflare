package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.ComponentKt;
import org.fernice.reflare.element.TextFieldElement;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareTextFieldUI extends BasicTextFieldUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareTextFieldUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        super.installDefaults();
        final JTextField textField = (JTextField) getComponent();

        if (element == null) {
            element = new TextFieldElement(textField);
        }

        textField.setOpaque(false);
        textField.setBorder(new FlareBorder(this));
        textField.addPropertyChangeListener("enabled", evt -> textField.repaint());

        ComponentKt.registerElement(textField, element);
    }

    @Override
    protected void uninstallDefaults() {
        ComponentKt.deregisterElement(getComponent());

        super.uninstallDefaults();
    }

    @Override
    protected void paintSafely(final Graphics graphics) {
        element.paintBackground(getComponent(), graphics);

        super.paintSafely(graphics);
    }

    @Override
    protected void paintBackground(final Graphics g) {
        // already done in paintSafely()
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