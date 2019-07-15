package fernice.reflare;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLookAndFeel;
import org.fernice.reflare.FlareDefaultLookup;
import org.fernice.reflare.element.AWTComponentElement;
import org.fernice.reflare.element.StyleTreeHelper;
import org.fernice.reflare.element.support.SharedHoverHandler;
import org.fernice.reflare.internal.AATextInfoHelper;
import org.fernice.reflare.internal.DefaultLookupHelper;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.fernice.reflare.platform.GTKKeybindings;
import org.fernice.reflare.platform.WindowsKeybindings;

public class FlareLookAndFeel extends BasicLookAndFeel {

    @Deprecated
    public static void init() {
        install();
    }

    private static final AtomicBoolean integrationInstalled = new AtomicBoolean();

    public static void installIntegration() {
        if (!integrationInstalled.getAndSet(true)) {
            SharedHoverHandler.initialize();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", focusChangeListener);
        }
    }

    public static void install() {
        try {

            UIManager.setLookAndFeel(FlareLookAndFeel.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Flare Look and Feel";
    }

    @Override
    public String getID() {
        return "flare";
    }

    @Override
    public String getDescription() {
        return "Css based Look and Feel";
    }

    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }

    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }

    private static final PropertyChangeListener focusChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getOldValue() != null) {
                Component component = (Component) evt.getOldValue();
                AWTComponentElement element = StyleTreeHelper.getElement(component);

                element.traceReapplyOrigin("focus:gain");
                element.reapplyCSS();
            }

            if (evt.getNewValue() != null) {
                Component component = (Component) evt.getNewValue();
                AWTComponentElement element = StyleTreeHelper.getElement(component);

                element.traceReapplyOrigin("focus:loss");
                element.reapplyCSS();
            }
        }
    };

    @Override
    public void initialize() {
        super.initialize();
        DefaultLookupHelper.setDefaultLookup(new FlareDefaultLookup());

        SharedHoverHandler.initialize();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", focusChangeListener);
        integrationInstalled.set(true);
    }

    @Override
    public void uninitialize() {
        super.uninitialize();

        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("focusOwner", focusChangeListener);
        integrationInstalled.set(false);
    }

    private UIDefaults defaults;

    @Override
    public UIDefaults getDefaults() {
        if (defaults == null) {
            defaults = super.getDefaults();

            final String osName = System.getProperty("os.name");
            final boolean isWindows = osName != null && osName.contains("Windows");

            if (isWindows) {
                WindowsKeybindings.installKeybindings(defaults);
            } else {
                GTKKeybindings.installKeybindings(defaults);
            }

            AATextInfoHelper.aaTextInfo(defaults);

            final String basicPackageName = "org.fernice.reflare.ui.";

            defaults.put("ComponentUI", FlareLookAndFeel.class.getName());
            defaults.put("RootPaneUI", basicPackageName + "FlareRootPaneUI");
            defaults.put("ViewportUI", basicPackageName + "FlareViewportUI");
            defaults.put("PanelUI", basicPackageName + "FlarePanelUI");
            defaults.put("TabbedPaneUI", basicPackageName + "FlareTabbedPaneUI");

            defaults.put("ToolTipUI", basicPackageName + "FlareToolTipUI");

            defaults.put("TableUI", basicPackageName + "FlareTableUI");
            defaults.put("TableHeaderUI", basicPackageName + "FlareTableHeaderUI");

            defaults.put("ScrollPaneUI", basicPackageName + "FlareScrollPaneUI");
            defaults.put("ScrollBarUI", basicPackageName + "FlareScrollBarUI");

            defaults.put("LabelUI", basicPackageName + "FlareLabelUI");

            defaults.put("TextFieldUI", basicPackageName + "FlareTextFieldUI");
            defaults.put("FormattedTextFieldUI", basicPackageName + "FlareFormattedTextFieldUI");
            defaults.put("PasswordFieldUI", basicPackageName + "FlarePasswordFieldUI");
            defaults.put("TextAreaUI", basicPackageName + "FlareTextAreaUI");
            defaults.put("EditorPaneUI", basicPackageName + "FlareEditorPaneUI");
            defaults.put("TextPaneUI", basicPackageName + "FlareTextPaneUI");

            defaults.put("ButtonUI", basicPackageName + "FlareButtonUI");
            defaults.put("ToggleButtonUI", basicPackageName + "FlareToggleButtonUI");
            defaults.put("RadioButtonUI", basicPackageName + "FlareRadioButtonUI");
            defaults.put("CheckBoxUI", basicPackageName + "FlareCheckBoxUI");

            defaults.put("ComboBoxUI", basicPackageName + "FlareComboBoxUI");
            defaults.put("ComboBoxPopupUI", basicPackageName + "FlareComboBoxPopupUI");
            defaults.put("ListUI", basicPackageName + "FlareListUI");

            defaults.put("PopupMenuUI", basicPackageName + "FlarePopupMenuUI");

            defaults.put("MenuBarUI", basicPackageName + "FlareMenuBarUI");
            defaults.put("MenuItemUI", basicPackageName + "FlareMenuItemUI");
            defaults.put("MenuUI", basicPackageName + "FlareMenuUI");

            defaults.put("FileChooserUI", basicPackageName + "FlareAbstractFileChooserUI");
        }

        return defaults;
    }

    @SuppressWarnings("unused")
    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        throw new IllegalArgumentException();
    }
}
