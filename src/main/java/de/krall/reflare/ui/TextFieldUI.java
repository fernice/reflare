package de.krall.reflare.ui;

import de.krall.reflare.FlareBorder;
import de.krall.reflare.element.ComponentElement;
import de.krall.reflare.element.TextFieldElement;
import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import org.jetbrains.annotations.NotNull;

public class TextFieldUI extends BasicTextFieldUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new TextFieldUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        super.installDefaults();
        final JTextField textField = (JTextField) getComponent();

        element = new TextFieldElement(textField);

        textField.setOpaque(false);
        textField.setBorder(new FlareBorder(this));

        UIKt.registerUI(textField, this);
    }

    @Override
    protected void uninstallDefaults() {
        UIKt.deregisterUI(getComponent());

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
