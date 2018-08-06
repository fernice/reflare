package modern.reflare.ui;

import modern.reflare.element.ComponentElement;
import modern.reflare.element.FormattedTextFieldElement;
import modern.reflare.element.ComponentKt;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;
import org.jetbrains.annotations.NotNull;

public class FormattedTextFieldUI extends BasicFormattedTextFieldUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FormattedTextFieldUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        super.installDefaults();
        final JFormattedTextField textField = (JFormattedTextField) getComponent();

        if (element == null) {
            element = new FormattedTextFieldElement(textField);
        }

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