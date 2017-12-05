package de.reflare;

import java.awt.Color;
import java.security.AccessController;
import java.util.Locale;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicLookAndFeel;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.plaf.GTKKeybindings;
import sun.swing.plaf.WindowsKeybindings;

public class FlareLookAndFeel extends BasicLookAndFeel {

    public static final Color GREEN = new Color(0, 105, 78);

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

            Object aaTextInfo = getAATextInfo();
            defaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, aaTextInfo);

            final String basicPackageName = "de.bewotec.newmyjack.";

            defaults.put("TextFieldUI", basicPackageName + "TextFieldUI");
        }

        return defaults;
    }

    private static Object getAATextInfo() {
        final String language = Locale.getDefault().getLanguage();
        final String desktop = AccessController.doPrivileged(new GetPropertyAction("sun.desktop"));

        final boolean isCjkLocale = (Locale.CHINESE.getLanguage().equals(language) || Locale.JAPANESE.getLanguage().equals(language) ||
                Locale.KOREAN.getLanguage().equals(language));
        final boolean isGnome = "gnome".equals(desktop);
        final boolean isLocal = SwingUtilities2.isLocalDisplay();

        final boolean setAA = isLocal && (!isGnome || !isCjkLocale);

        return SwingUtilities2.AATextInfo.getAATextInfo(setAA);
    }

}
