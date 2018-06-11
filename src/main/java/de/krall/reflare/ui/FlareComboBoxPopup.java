package de.krall.reflare.ui;

import de.krall.reflare.element.AWTComponentElement;
import de.krall.reflare.element.AWTContainerElement;
import de.krall.reflare.element.ComponentKt;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboPopup;

public class FlareComboBoxPopup extends BasicComboPopup {

    public FlareComboBoxPopup(JComboBox combo) {
        super(combo);

        setBorder(new FlareBorder((FlareUI) getUI()));
    }

    @Override
    public String getUIClassID() {
        return "ComboBoxPopupUI";
    }

    @Override
    protected void configureList() {
        list.setFont(comboBox.getFont());
        list.setCellRenderer(comboBox.getRenderer());
        list.setFocusable(false);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int selectedIndex = comboBox.getSelectedIndex();
        if (selectedIndex == -1) {
            list.clearSelection();
        } else {
            list.setSelectedIndex(selectedIndex);
            list.ensureIndexIsVisible(selectedIndex);
        }
        installListListeners();
    }

    @Override
    public void setBorder(final Border border) {
        if (border instanceof FlareBorder) {
            super.setBorder(border);
        }
    }

    private boolean vague = true;

    public void show() {
        comboBox.firePopupMenuWillBecomeVisible();
        setListSelection(comboBox.getSelectedIndex());
        if (vague) {
            getPopupLocation();
            revalidate();
            vague = false;
        }

        Point location = getPopupLocation();
        show(comboBox, location.x, location.y);
    }

    private void setListSelection(int selectedIndex) {
        if (selectedIndex == -1) {
            list.clearSelection();
        } else {
            list.setSelectedIndex(selectedIndex);
            list.ensureIndexIsVisible(selectedIndex);
        }
    }

    private Point getPopupLocation() {
        Dimension popupSize = comboBox.getSize();
        Insets insets = getInsets();

        // reduce the width of the scrollpane by the insets so that the popup
        // is the same width as the combo box.
        popupSize.setSize(popupSize.width - (insets.right + insets.left), getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
        Rectangle popupBounds = computePopupBounds(0, comboBox.getBounds().height, popupSize.width, popupSize.height);
        Dimension scrollSize = popupBounds.getSize();
        Point popupLocation = popupBounds.getLocation();

        scroller.setMaximumSize(scrollSize);
        scroller.setPreferredSize(scrollSize);
        scroller.setMinimumSize(scrollSize);

        list.revalidate();

        return popupLocation;
    }

    @Override
    protected Rectangle computePopupBounds(final int px, final int py, final int pw, final int ph) {
        return super.computePopupBounds(px, py, pw, ph);
    }

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }

    @Override
    protected int getPopupHeightForRowCount(int maxRowCount) {
        AWTComponentElement element = ComponentKt.into(this);
        AWTContainerElement comboBoxElement = (AWTContainerElement) ComponentKt.into(comboBox);

        comboBoxElement.addVirtualChild(element);

        element.restyle();

        ListUI listUI = (ListUI) list.getUI();

        listUI.invalidateHeight();

        try {
            int minRowCount = Math.min(maxRowCount, comboBox.getItemCount());
            int height = 0;

            ListCellRenderer renderer = list.getCellRenderer();
            Object value;

            for (int i = 0; i < minRowCount; ++i) {
                value = list.getModel().getElementAt(i);
                Component c = renderer.getListCellRendererComponent(list, value, i, false, false);
                listUI.getRenderPane().add(c);

                height += c.getPreferredSize().height;
            }

            listUI.getRenderPane().removeAll();

            if (height == 0) {
                height = comboBox.getHeight();
            }

            Border border = scroller.getViewportBorder();
            if (border != null) {
                Insets insets = border.getBorderInsets(null);
                height += insets.top + insets.bottom;
            }

            border = scroller.getBorder();
            if (border != null) {
                Insets insets = border.getBorderInsets(null);
                height += insets.top + insets.bottom;
            }

            return height;
        } finally {
            comboBoxElement.removeVirtualChild(element);
        }
    }
}
