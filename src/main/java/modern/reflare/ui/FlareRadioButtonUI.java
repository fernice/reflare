package modern.reflare.ui;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.plaf.ComponentUI;
import modern.reflare.element.ButtonElement;
import modern.reflare.element.RadioButtonElement;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;

public class FlareRadioButtonUI extends FlareToggleButtonUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareRadioButtonUI();
    }

    @Override
    protected ButtonElement createElement(AbstractButton button) {
        return new RadioButtonElement((JRadioButton) button);
    }
}
