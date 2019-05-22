package fernice.reflare;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLookAndFeel;
import org.fernice.flare.style.properties.PropertiesKt;
import org.fernice.flare.style.properties.module.BackgroundImagePropertyModule;
import org.fernice.flare.style.properties.module.BackgroundPropertyModule;
import org.fernice.flare.style.properties.module.BorderPropertyModule;
import org.fernice.flare.style.properties.module.ColorPropertyModule;
import org.fernice.flare.style.properties.module.FontPropertyModule;
import org.fernice.flare.style.properties.module.MarginPropertyModule;
import org.fernice.flare.style.properties.module.PaddingPropertyModule;
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

    public static void init() {
        PropertiesKt.register( //
                FontPropertyModule.INSTANCE, //
                ColorPropertyModule.INSTANCE, //
                BackgroundPropertyModule.INSTANCE, //
                BorderPropertyModule.INSTANCE, //
                MarginPropertyModule.INSTANCE, //
                PaddingPropertyModule.INSTANCE, //
                //
                BackgroundImagePropertyModule.INSTANCE //
        );

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

    private final PropertyChangeListener focusChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getOldValue() != null) {
                Component component = (Component) evt.getOldValue();
                AWTComponentElement element = StyleTreeHelper.getElement(component);

                element.invalidateStyle();
            }

            if (evt.getNewValue() != null) {
                Component component = (Component) evt.getNewValue();
                AWTComponentElement element = StyleTreeHelper.getElement(component);

                element.invalidateStyle();
            }
        }
    };

    @Override
    public void initialize() {
        super.initialize();
        DefaultLookupHelper.setDefaultLookup(new FlareDefaultLookup());

        SharedHoverHandler.initialize();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", focusChangeListener);
    }

    @Override
    public void uninitialize() {
        super.uninitialize();

        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("focusOwner", focusChangeListener);
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
            final String kotlinPeer = basicPackageName + "FlareKotlinUIPeer";

            defaults.put("ComponentUI", FlareLookAndFeel.class.getName());
            defaults.put("RootPaneUI", basicPackageName + "FlareRootPaneUI");
            defaults.put("ViewportUI", basicPackageName + "FlareViewportUI");
            defaults.put("PanelUI", basicPackageName + "FlarePanelUI");
            defaults.put("TabbedPaneUI", basicPackageName + "FlareTabbedPaneUI");

            defaults.put("ToolTipUI", kotlinPeer);

            defaults.put("TableUI", kotlinPeer);
            defaults.put("TableHeaderUI", kotlinPeer);

            defaults.put("ScrollPaneUI", basicPackageName + "FlareScrollPaneUI");
            defaults.put("ScrollBarUI", kotlinPeer);

            defaults.put("LabelUI", basicPackageName + "FlareLabelUI");

            defaults.put("TextFieldUI", basicPackageName + "FlareTextFieldUI");
            defaults.put("FormattedTextFieldUI", basicPackageName + "FlareFormattedTextFieldUI");
            defaults.put("PasswordFieldUI", basicPackageName + "FlarePasswordFieldUI");
            defaults.put("TextAreaUI", basicPackageName + "FlareTextAreaUI");
            defaults.put("EditorPaneUI", kotlinPeer);
            defaults.put("TextPaneUI", kotlinPeer);

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
