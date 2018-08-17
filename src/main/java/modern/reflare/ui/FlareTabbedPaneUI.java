package modern.reflare.ui;

import modern.reflare.FlareLookAndFeel;
import modern.reflare.element.ComponentElement;
import modern.reflare.element.ComponentKt;
import modern.reflare.element.TabbedPaneElement;
import modern.reflare.meta.DefinedBy;
import modern.reflare.meta.DefinedBy.Api;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FlareTabbedPaneUI extends BasicTabbedPaneUI implements FlareUI {

    @DefinedBy(Api.LOOK_AND_FEEL)
    public static ComponentUI createUI(JComponent c) {
        return new FlareTabbedPaneUI();
    }

    private TabbedPaneElement element;

    @Override
    protected void installDefaults() {
        if (element == null) {
            element = new TabbedPaneElement(tabPane);
        }

        tabPane.setOpaque(false);
        tabPane.setBorder(new FlareBorder(this));
        tabPane.setFont(FlareLookAndFeel.DEFAULT_FONT);
        tabInsets = new Insets(0, 0, 0, 0);
        selectedTabPadInsets = new Insets(0, 0, 0, 0);
        tabAreaInsets = new Insets(0, 0, 0, 0);
        contentBorderInsets = new Insets(0, 0, 0, 0);

        ComponentKt.registerElement(tabPane, element);
    }

    @Override
    protected void uninstallDefaults() {
        ComponentKt.deregisterElement(tabPane);
    }

    @Override
    public void paint(final Graphics graphics, JComponent component) {
        paintBackground(component, graphics);

        //paint(graphics);
        super.paint(graphics, component);
    }

    private final Rectangle tabAreaBounds = new Rectangle(0, 0, 0, 0);

    protected void paint(Graphics g) {
        int selectedIndex = tabPane.getSelectedIndex();
        int tabPlacement = tabPane.getTabPlacement();

        ensureCurrentLayout();

        if (!scrollableTabLayoutEnabled()) { // WRAP_TAB_LAYOUT
            Insets insets = tabPane.getInsets();
            int x = insets.left;
            int y = insets.top;
            int width = tabPane.getWidth() - insets.left - insets.right;
            int height = tabPane.getHeight() - insets.top - insets.bottom;
            int size;
            switch (tabPlacement) {
                case LEFT:
                    width = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                    break;
                case RIGHT:
                    size = calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                    x = x + width - size;
                    width = size;
                    break;
                case BOTTOM:
                    size = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                    y = y + height - size;
                    height = size;
                    break;
                case TOP:
                default:
                    height = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
            }

            tabAreaBounds.setBounds(x, y, width, height);

            if (g.getClipBounds().intersects(tabAreaBounds)) {
                paintTabArea(g, tabAreaBounds);
            }
        }

        // Paint content border
        paintContentBorder(g, tabPlacement, selectedIndex);
    }

    private void paintTabArea(Graphics g, Rectangle tabAreaBounds) {
        element.renderTabArea(g, tabAreaBounds);
    }

    private void ensureCurrentLayout() {
        if (!tabPane.isValid()) {
            tabPane.validate();
        }

        if (!tabPane.isValid()) {
            TabbedPaneLayout layout = (TabbedPaneLayout) tabPane.getLayout();
            layout.calculateLayoutInfo();
        }
    }

    private boolean scrollableTabLayoutEnabled() {
        return (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    @Override
    protected void paintContentBorder(final Graphics g, final int tabPlacement, final int selectedIndex) {

    }

    private void paintBackground(JComponent component, Graphics g) {
        element.paintBackground(component, g);
    }

    @Override
    public void paintBorder(@NotNull final Component c, @NotNull final Graphics g, final int x, final int y, final int width, final int height) {
        element.paintBorder(c, g);
    }

    @NotNull
    @Override
    public ComponentElement getElement() {
        return element;
    }
}
