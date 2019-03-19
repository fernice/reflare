package org.fernice.reflare.ui;

import java.awt.Cursor;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import org.fernice.reflare.element.ButtonElement;
import org.fernice.reflare.element.ToggleButtonElement;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;

public class FlareToggleButtonUI extends FlareButtonUI {

    @SuppressWarnings("unused")
    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareToggleButtonUI();
    }

    @Override
    protected void installDefaults(AbstractButton button) {
        super.installDefaults(button);

        button.setCursor(Cursor.getDefaultCursor());

        SwingUtilities.invokeLater(() -> button.setBorderPainted(true));
    }

    @Override
    protected ButtonElement createElement(AbstractButton button) {
        return new ToggleButtonElement((JToggleButton) button);
    }
}
