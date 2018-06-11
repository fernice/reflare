package de.krall.reflare.ui;

import de.krall.reflare.element.ComponentElement;
import de.krall.reflare.element.ComponentKt;
import de.krall.reflare.element.TextAreaElement;
import de.krall.reflare.meta.DefinedBy;
import de.krall.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import org.jetbrains.annotations.NotNull;

public class TextAreaUI extends BasicTextAreaUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new TextAreaUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        super.installDefaults();
        final JTextArea textField = (JTextArea) getComponent();

        element = new TextAreaElement(textField);

        textField.setOpaque(false);
        textField.setBorder(new FlareBorder(this));

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
