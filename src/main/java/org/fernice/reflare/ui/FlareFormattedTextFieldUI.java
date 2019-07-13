package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.FormattedTextFieldElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareFormattedTextFieldUI extends BasicFormattedTextFieldUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareFormattedTextFieldUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        super.installDefaults();
        final JFormattedTextField textField = (JFormattedTextField) getComponent();

        if (element == null) {
            element = new FormattedTextFieldElement(textField);
        }

        UIDefaultsHelper.installDefaultProperties(this, textField);

        StyleTreeElementLookup.registerElement(textField, this);
    }

    @Override
    protected void uninstallDefaults() {
        StyleTreeElementLookup.deregisterElement(getComponent());

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
