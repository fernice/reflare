/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.fernice.reflare.ui;

import fernice.reflare.ScrollPaneHelper;
import fernice.reflare.StyleHelper;
import fernice.reflare.light.IntegrationHelper;
import fernice.reflare.light.FList;
import fernice.reflare.light.FScrollPane;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboPopup;
import org.fernice.reflare.element.AWTComponentElement;
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
    public void updateUI() {
        super.setUI(IntegrationHelper.getIntegrationDependentUI(this, FlareComboBoxPopupUI::new));
    }

    @Override
    protected JList createList() {
        return new FList( comboBox.getModel() ) {
            public void processMouseEvent(MouseEvent e)  {
                if ((e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0)  {
                    // Fix for 4234053. Filter out the Control Key from the list.
                    // ie., don't allow CTRL key deselection.
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    e = new MouseEvent((Component)e.getSource(), e.getID(), e.getWhen(),
                            e.getModifiers() ^ toolkit.getMenuShortcutKeyMask(),
                            e.getX(), e.getY(),
                            e.getXOnScreen(), e.getYOnScreen(),
                            e.getClickCount(),
                            e.isPopupTrigger(),
                            MouseEvent.NOBUTTON);
                }
                super.processMouseEvent(e);
            }
        };
    }

    @Override
    protected void configureList() {
        FlareComboBoxUI comboBoxUI = (FlareComboBoxUI) comboBox.getUI();

        StyleHelper.getClasses(list).add("combobox-list");
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
    protected JScrollPane createScroller() {
        FScrollPane sp = new FScrollPane( list,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        sp.setHorizontalScrollBar(null);
        ScrollPaneHelper.setInline(sp, true);
        return sp;
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

            element.applyCSSFrom("renderer:combobox");

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
