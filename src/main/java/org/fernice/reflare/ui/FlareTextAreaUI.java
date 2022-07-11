package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.element.TextAreaElement;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareTextAreaUI extends BasicTextAreaUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareTextAreaUI();
    }

    private ComponentElement element;

    @Override
    protected void installDefaults() {
        super.installDefaults();
        final JTextArea textField = (JTextArea) getComponent();

        if (element == null) {
            element = new TextAreaElement(textField);
        }

        UIDefaultsHelper.installDefaultProperties(this, textField);
        textField.setTabSize(4);

        StyleTreeElementLookup.registerElement(textField, this);
    }

    @Override
    protected void uninstallDefaults() {
        StyleTreeElementLookup.deregisterElement(getComponent());

        super.uninstallDefaults();
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
    protected void paintSafely(final Graphics graphics) {
        element.paintBackground(graphics);

        super.paintSafely(graphics);
    }

    @Override
    protected void paintBackground(final Graphics g) {
        // already done in paintSafely()
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
