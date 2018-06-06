package de.krall.reflare;

import de.krall.flare.Engine;
import de.krall.flare.dom.Device;
import de.krall.flare.font.FontMetricsProvider;
import de.krall.flare.font.FontMetricsQueryResult;
import de.krall.flare.style.ComputedValues;
import de.krall.flare.style.properties.stylestruct.Font;
import de.krall.flare.style.stylesheet.Origin;
import de.krall.flare.style.stylesheet.Stylesheet;
import de.krall.flare.style.value.computed.Au;
import de.krall.flare.style.value.computed.PixelLength;
import de.krall.flare.style.value.generic.Size2D;
import de.krall.reflare.platform.GTKKeybindings;
import de.krall.reflare.platform.WindowsKeybindings;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicLookAndFeel;
import org.jetbrains.annotations.NotNull;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;

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

    private Engine engine;

    public Engine getEngine() {
        return engine;
    }

    @Override
    public void initialize() {
        super.initialize();
        DefaultLookup.setDefaultLookup(new FlareDefaultLookup());

        engine = Engine.Companion.from(new DeviceImpl(), new FontMetricsProviderImpl());

        try {
            Path path = new File(FlareLookAndFeel.class.getResource("/test.css").getFile()).toPath();
            byte[] encoded = Files.readAllBytes(path);

            final String text = new String(encoded);

            engine.getStylist().insertStyleheet(Stylesheet.Companion.from(text, Origin.AUTHOR));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class DeviceImpl implements Device {

        @NotNull
        @Override
        public Size2D<Au> viewportSize() {
            return new Size2D<>(Au.Companion.from(new PixelLength(600)), Au.Companion.from(new PixelLength(400)));
        }

        @NotNull
        @Override
        public Au rootFontSize() {
            return Au.Companion.from(new PixelLength(16));
        }

        @NotNull
        @Override
        public ComputedValues defaultComputedValues() {
            return ComputedValues.Companion.getInitial();
        }
    }

    private static class FontMetricsProviderImpl implements FontMetricsProvider {

        @NotNull
        @Override
        public FontMetricsQueryResult query(@NotNull final Font font, @NotNull final Au fontSize, @NotNull final Device device) {
            return new FontMetricsQueryResult.NotAvailable();
        }
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

            //putAATextInfo(getAATextInfoCondition(), defaults);

            Object aaTextInfo = getAATextInfo();
            defaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, aaTextInfo);

            final String basicPackageName = "de.krall.fusee.ui.";

            defaults.put("TextFieldUI", basicPackageName + "TextFieldUI");
        }

        return defaults;
    }

    private static Object getAATextInfo() {
        String language = Locale.getDefault().getLanguage();
        String desktop = AccessController.doPrivileged(new GetPropertyAction("sun.desktop"));

        boolean isCjkLocale = (Locale.CHINESE.getLanguage().equals(language) || Locale.JAPANESE.getLanguage().equals(language) ||
                Locale.KOREAN.getLanguage().equals(language));
        boolean isGnome = "gnome".equals(desktop);
        boolean isLocal = SwingUtilities2.isLocalDisplay();

        boolean setAA = isLocal && (!isGnome || !isCjkLocale);

        return SwingUtilities2.AATextInfo.getAATextInfo(setAA);
    }

    private static boolean getAATextInfoCondition() {
        final String language = Locale.getDefault().getLanguage();
        final String desktop = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty("sun.desktop"));

        final boolean isCjkLocale = (Locale.CHINESE.getLanguage().equals(language) || Locale.JAPANESE.getLanguage().equals(language) ||
                Locale.KOREAN.getLanguage().equals(language));
        final boolean isGnome = "gnome".equals(desktop);
        final boolean isLocal = SwingUtilities2.isLocalDisplay();

        return isLocal && (!isGnome || !isCjkLocale);
    }

}
