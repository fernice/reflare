/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboPopup;
import org.fernice.reflare.element.AWTComponentElement;
import org.fernice.reflare.element.AWTContainerElement;
import org.fernice.reflare.element.StyleTreeHelper;

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
        FlareComboBoxUI comboBoxUI = (FlareComboBoxUI) comboBox.getUI();

        list.setCellRenderer(comboBoxUI.getRendererWrapper());
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

    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        PropertyChangeListener parent = super.createPropertyChangeListener();

        return (evt) -> {
            if (evt.getPropertyName() == "renderer") {
                FlareComboBoxUI comboBoxUI = (FlareComboBoxUI) comboBox.getUI();

                list.setCellRenderer(comboBoxUI.getRendererWrapper());
                if (isVisible()) {
                    hide();
                }
            } else {
                parent.propertyChange(evt);
            }
        };
    }

    @Override
    protected int getPopupHeightForRowCount(int maxRowCount) {
        AWTComponentElement element = StyleTreeHelper.getElement(this);

        FlareListUI listUI = (FlareListUI) list.getUI();

        listUI.invalidateHeight();

        int minRowCount = Math.min(maxRowCount, comboBox.getItemCount());
        int height = 0;

        ListCellRenderer renderer = list.getCellRenderer();
        Object value;

        for (int i = 0; i < minRowCount; ++i) {
            value = list.getModel().getElementAt(i);
            Component c = renderer.getListCellRendererComponent(list, value, i, false, false);
            listUI.getRenderPane().add(c);

            element.applyCSS();

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
    }
}
