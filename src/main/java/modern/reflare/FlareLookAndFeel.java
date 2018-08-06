package modern.reflare;

import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import modern.reflare.platform.GTKKeybindings;
import modern.reflare.platform.WindowsKeybindings;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLookAndFeel;
import sun.swing.DefaultLookup;

public class FlareLookAndFeel extends BasicLookAndFeel {

    public static void init() {
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
        DefaultLookup.setDefaultLookup(new FlareDefaultLookup());
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

            Object aaTextInfo = AATextInfo.getAATextInfo();
            defaults.put(AATextInfo.AA_TEXT_INFO_KEY, aaTextInfo);

            final String basicPackageName = "modern.reflare.ui.";

            defaults.put("RootPaneUI", basicPackageName + "RootPaneUI");
            defaults.put("PanelUI", basicPackageName + "PanelUI");
            defaults.put("TabbedPaneUI", basicPackageName + "FlareTabbedPaneUI");
            defaults.put("TextFieldUI", basicPackageName + "TextFieldUI");
            defaults.put("FormattedTextFieldUI", basicPackageName + "FormattedTextFieldUI");
            defaults.put("PasswordFieldUI", basicPackageName + "PasswordFieldUI");
            defaults.put("TextAreaUI", basicPackageName + "TextAreaUI");
            defaults.put("ButtonUI", basicPackageName + "FlareButtonUI");
            defaults.put("ToggleButtonUI", basicPackageName + "FlareToggleButtonUI");
            defaults.put("RadioButtonUI", basicPackageName + "FlareRadioButtonUI");
            defaults.put("CheckBoxUI", basicPackageName + "FlareCheckBoxUI");
            defaults.put("LabelUI", basicPackageName + "LabelUI");
            defaults.put("ComboBoxUI", basicPackageName + "ComboBoxUI");
            defaults.put("PopupMenuUI", basicPackageName + "PopupMenuUI");
            defaults.put("ListUI", basicPackageName + "FlareListUI");
            defaults.put("ScrollPaneUI", basicPackageName + "ScrollPaneUI");
            defaults.put("ViewportUI", basicPackageName + "ViewportUI");
            defaults.put("ComboBoxPopupUI", basicPackageName + "ComboBoxPopupUI");
            defaults.put("ComponentUI", FlareLookAndFeel.class.getName());
        }

        return defaults;
    }

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        throw new IllegalArgumentException();
    }


    public static final Font DEFAULT_FONT = new Font("sans-serif", java.awt.Font.PLAIN, 12);
}
