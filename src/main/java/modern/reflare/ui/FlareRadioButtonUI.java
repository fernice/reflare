package modern.reflare.ui;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.plaf.ComponentUI;
import modern.reflare.element.ButtonElement;
import modern.reflare.element.RadioButtonElement;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import modern.reflare.util.ImageKt;

@SuppressWarnings("unused")
public class FlareRadioButtonUI extends FlareToggleButtonUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareRadioButtonUI();
    }

    @Override
    protected void installDefaults(AbstractButton button) {
        super.installDefaults(button);

        button.setIcon(ImageKt.getScaledIconResource("/radiobutton.png", 17, 17));
        button.setSelectedIcon(ImageKt.getScaledIconResource("/radiobutton-selected.png", 17, 17));
    }

    @Override
    protected ButtonElement createElement(AbstractButton button) {
        return new RadioButtonElement((JRadioButton) button);
    }
}
