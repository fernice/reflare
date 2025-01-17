package fernice.reflare;

import java.awt.Component;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.PopupFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLookAndFeel;

import org.fernice.reflare.FlareDefaultLookup;
import org.fernice.reflare.element.AWTComponentElement;
import org.fernice.reflare.element.StyleTreeHelper;
import org.fernice.reflare.element.support.SharedHoverHandler;
import org.fernice.reflare.internal.AATextInfoHelper;
import org.fernice.reflare.internal.CompatibilityHelper;
import org.fernice.reflare.internal.DefaultLookupHelper;
import org.fernice.reflare.internal.PopupFactoryHelper;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;
import org.fernice.reflare.platform.GTKKeybindings;
import org.fernice.reflare.platform.MacosKeybindings;
import org.fernice.reflare.platform.Platform;
import org.fernice.reflare.platform.WindowsKeybindings;
import org.fernice.reflare.ui.text.FlareHTMLEditorKit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.swing.ImageIconUIResource;

public class FlareLookAndFeel extends BasicLookAndFeel {

    static {
        CompatibilityHelper.ensureCompatibility();
    }

    private static final Logger LOG = LoggerFactory.getLogger(FlareLookAndFeel.class);

    @Deprecated
    public static void init() {
        install();
    }

    private static final AtomicBoolean integrationInstalled = new AtomicBoolean();
    private static final AtomicBoolean lightweightMode = new AtomicBoolean(true);

    public static void installIntegration() {
        if (!integrationInstalled.getAndSet(true)) {
            SharedHoverHandler.initialize();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", focusChangeListener);
            if (Platform.isMac()) {
                try {
                    Class.forName("com.apple.laf.ScreenPopupFactory");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            JEditorPane.registerEditorKitForContentType("text/html", FlareHTMLEditorKit.class.getName());
        }
    }

    public static void install() {
        try {
            UIManager.setLookAndFeel(FlareLookAndFeel.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isLightweightMode() {
        return lightweightMode.get();
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
            if (evt.getOldValue() != null && !(evt.getOldValue() instanceof Window)) {
                Component component = (Component) evt.getOldValue();
                AWTComponentElement element = StyleTreeHelper.getElement(component);

                element.reapplyCSSFrom("focus:loss");
            }

            if (evt.getNewValue() != null && !(evt.getNewValue() instanceof Window)) {
                Component component = (Component) evt.getNewValue();
                AWTComponentElement element = StyleTreeHelper.getElement(component);

                element.reapplyCSSFrom("focus:gain");
            }
        }
    };

    @Override
    public void initialize() {
        super.initialize();
        DefaultLookupHelper.setDefaultLookup(new FlareDefaultLookup());

        installIntegration();
        installPreferredPopupFactory();
        lightweightMode.set(false);
    }

    @Override
    public void uninitialize() {
        super.uninitialize();

        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("focusOwner", focusChangeListener);
        integrationInstalled.set(false);
        lightweightMode.set(true);
    }

    private UIDefaults defaults;

    @Override
    public UIDefaults getDefaults() {
        if (defaults == null) {
            defaults = super.getDefaults();

            if (Platform.isWindows()) {
                WindowsKeybindings.installKeybindings(defaults);
            } else if (Platform.isMac()) {
                MacosKeybindings.installKeybindings(defaults);
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

            defaults.put("MenuBarUI", basicPackageName + (Platform.isMac() ? "FlareAppleMenuBarUI" : "FlareMenuBarUI"));
            defaults.put("MenuUI", basicPackageName + "FlareMenuUI");
            defaults.put("MenuItemUI", basicPackageName + "FlareMenuItemUI");
            defaults.put("SeparatorUI", basicPackageName + "FlareSeparatorUI");
            defaults.put("PopupMenuSeparatorUI", basicPackageName + "FlareSeparatorUI");

            defaults.put("CheckBoxMenuItemUI", basicPackageName + "FlareCheckBoxMenuItemUI");
            defaults.put("RadioButtonMenuItemUI", basicPackageName + "FlareRadioButtonMenuItemUI");

            defaults.put("FileChooserUI", basicPackageName + "FlareAbstractFileChooserUI");
        }

        return defaults;
    }

    @SuppressWarnings("unused")
    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        throw new IllegalArgumentException();
    }

    private static void installPreferredPopupFactory() {
        if (Platform.isMac()) {
            installApplePopupFactory();
        }
    }

    private static void installApplePopupFactory() {
        try {
            PopupFactory screenPopupFactory = PopupFactoryHelper.createScreenPopupFactory();

            PopupFactory.setSharedInstance(screenPopupFactory);
        } catch (Exception e) {
            LOG.error("cannot install apple popup factory", e);
        }
    }

    @Override
    public Icon getDisabledIcon(JComponent component, Icon icon) {
        Icon disabledIcon = createDisabledIcon(icon);
        return disabledIcon != null ? disabledIcon : super.getDisabledIcon(component, icon);
    }

    public static @Nullable Icon createDisabledIcon(@Nullable Icon icon) {
        if (icon == null) {
            return null;
        }
        Image image = ImageIcon.findImage(icon);
        if (image != null) {
            return new ImageIconUIResource(GrayFilter.createDisabledImage(image));
        }
        return null;
    }
}
