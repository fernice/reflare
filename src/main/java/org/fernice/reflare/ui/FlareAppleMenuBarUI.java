package org.fernice.reflare.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.MenuBar;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuBarUI;
import org.fernice.reflare.meta.DefinedBy;
import org.fernice.reflare.meta.DefinedBy.Api;

@SuppressWarnings("unused")
public class FlareAppleMenuBarUI extends FlareMenuBarUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareAppleMenuBarUI();
    }

    private static final Class<?> screenMenuBarClass;

    static {
        try {
            screenMenuBarClass = Class.forName("com.apple.laf.ScreenMenuBar");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    boolean useScreenMenuBar = getScreenMenuBarProperty();
    private MenuBar screenMenuBar;

    public void uninstallUI(JComponent var1) {
        if (this.screenMenuBar != null) {
            JFrame var2 = (JFrame) var1.getTopLevelAncestor();
            if (var2.getMenuBar() == this.screenMenuBar) {
                var2.setMenuBar(null);
            }

            this.screenMenuBar = null;
        }

        super.uninstallUI(var1);
    }

    public MenuBar getScreenMenuBar() {
        synchronized (this) {
            if (screenMenuBar == null) {
                try {
                    Constructor<?> constructor = screenMenuBarClass.getConstructor(JMenuBar.class);

                    screenMenuBar = (MenuBar) constructor.newInstance(menuBar);
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
        }
        return screenMenuBar;
    }

    public Dimension getPreferredSize(JComponent var1) {
        return isScreenMenuBar((JMenuBar) var1) && this.setScreenMenuBar(((JFrame) var1.getTopLevelAncestor())) ? new Dimension(0, 0) : null;
    }

    void clearScreenMenuBar(JFrame var1) {
        if (this.useScreenMenuBar) {
            var1.setMenuBar(null);
        }

    }

    boolean setScreenMenuBar(JFrame var1) {
        if (this.useScreenMenuBar) {
            try {
                this.getScreenMenuBar();
            } catch (Throwable var3) {
                return false;
            }

            var1.setMenuBar(this.screenMenuBar);
        }

        return true;
    }

    public static boolean isScreenMenuBar(JMenuBar var0) {
        MenuBarUI var1 = var0.getUI();
        if (var1 instanceof FlareAppleMenuBarUI) {
            if (!((FlareAppleMenuBarUI) var1).useScreenMenuBar) {
                return false;
            }

            Container var2 = var0.getTopLevelAncestor();
            if (var2 instanceof JFrame) {
                MenuBar var3 = ((JFrame) var2).getMenuBar();
                boolean var4 = ((JFrame) var2).getJMenuBar() == var0;
                if (var3 == null) {
                    return var4;
                }

                return var4 && screenMenuBarClass.isInstance(var3);
            }
        }

        return false;
    }

    static boolean getScreenMenuBarProperty() {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> Boolean.parseBoolean(System.getProperty("apple.laf.useScreenMenuBar")));
    }
}
