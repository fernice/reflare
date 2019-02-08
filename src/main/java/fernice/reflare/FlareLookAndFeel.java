package fernice.reflare;

import java.awt.Font;
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

    @Override
    public void initialize() {
        super.initialize();
        DefaultLookupHelper.setDefaultLookup(new FlareDefaultLookup());
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

            defaults.put("ScrollPaneUI", basicPackageName + "FlareScrollPaneUI");
            defaults.put("ScrollBarUI", basicPackageName + "FlareKotlinUIPeer");

            defaults.put("LabelUI", basicPackageName + "FlareLabelUI");

            defaults.put("TextFieldUI", basicPackageName + "FlareTextFieldUI");
            defaults.put("FormattedTextFieldUI", basicPackageName + "FlareFormattedTextFieldUI");
            defaults.put("PasswordFieldUI", basicPackageName + "FlarePasswordFieldUI");
            defaults.put("TextAreaUI", basicPackageName + "FlareTextAreaUI");

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
        }

        return defaults;
    }

    @SuppressWarnings("unused")
    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        throw new IllegalArgumentException();
    }


    public static final Font DEFAULT_FONT = new Font("sans-serif", java.awt.Font.PLAIN, 12);
}
