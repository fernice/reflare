/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.fernice.reflare.render;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import org.fernice.reflare.element.AWTComponentElement;
import org.fernice.reflare.element.StyleTreeHelper;
import org.fernice.reflare.ui.FlareBorder;

/**
 * This class is inserted in between cell renderers and the components that
 * use them.  It just exists to thwart the repaint() and invalidate() methods
 * which would otherwise propagate up the tree when the renderer was configured.
 * It's used by the implementations of JTable, JTree, and JList.  For example,
 * here's how CellRendererPane is used in the code the paints each row
 * in a JList:
 * <pre>
 *   cellRendererPane = next CellRendererPane();
 *   ...
 *   Component rendererComponent = renderer.getListCellRendererComponent();
 *   renderer.configureListCellRenderer(dataModel.getElementAt(row), row);
 *   cellRendererPane.paintComponent(g, rendererComponent, this, x, y, w, h);
 * </pre>
 * <p>
 * A renderer component must override isShowing() and unconditionally return
 * true to work correctly because the Swing paint does nothing for components
 * with isShowing false.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans&trade;
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author Hans Muller
 */
public class CellRendererPane extends javax.swing.CellRendererPane implements Accessible {

    private Boolean suppressRestyling = false;

    /**
     * Construct a CellRendererPane object.
     */
    public CellRendererPane() {
        super();
        setLayout(null);
        setVisible(false);
    }

    /**
     * Overridden to avoid propagating a invalidate up the tree when the
     * cell renderer child is configured.
     */
    @Override
    public void invalidate() {
    }


    /**
     * Shouldn't be called.
     */
    @Override
    public void paint(Graphics g) {
    }


    /**
     * Shouldn't be called.
     */
    @Override
    public void update(Graphics g) {
    }


    /**
     * If the specified component is already a child of this then we don't
     * bother doing anything - stacking order doesn't matter for cell
     * renderer components (CellRendererPane doesn't paint anyway).
     */
    @Override
    protected void addImpl(Component x, Object constraints, int index) {
        if (x.getParent() != this) {
            super.addImpl(x, constraints, index);

            // fixme(kralli) most likely redundant, but could be used in
            //  case the component is already part of the parent
            if (!suppressRestyling) {
                AWTComponentElement element = StyleTreeHelper.getElement(x);
                element.applyCSSFrom("renderer:cell");
            }
        }
    }


    /**
     * Paint a cell renderer component c on graphics object g.  Before the component
     * is drawn it's reparented to this (if that's necessary), it's bounds
     * are set to w,h and the graphics object is (effectively) translated to x,y.
     * If it's a JComponent, double buffering is temporarily turned off. After
     * the component is painted it's bounds are reset to -w, -h, 0, 0 so that, if
     * it's the last renderer component painted, it will not start consuming input.
     * The Container p is the component we're actually drawing on, typically it's
     * equal to this.getParent(). If shouldValidate is true the component c will be
     * validated before painted.
     */
    @Override
    public void paintComponent(Graphics g, Component c, Container p, int x, int y, int w, int h, boolean shouldValidate) {
        if (c == null) {
            if (p != null) {
                Color oldColor = g.getColor();
                g.setColor(p.getBackground());
                g.fillRect(x, y, w, h);
                g.setColor(oldColor);
            }
            return;
        }

        suppressRestyling = true;
        try {
            add(c);
        } finally {
            suppressRestyling = false;
        }

        c.setBounds(x, y, w, h);

        if (c instanceof JComponent && !(((JComponent) c).getBorder() instanceof FlareBorder)) {
            ((JComponent) c).setBorder(FlareBorder.create(StyleTreeHelper.getUi(c)));
        }

        c.validate();

        StyleTreeHelper.getElement(c).restyleIfNecessary();

        boolean wasDoubleBuffered = false;
        if ((c instanceof JComponent) && c.isDoubleBuffered()) {
            wasDoubleBuffered = true;
            ((JComponent) c).setDoubleBuffered(false);
        }

        Graphics cg = g.create(x, y, w, h);
        try {
            c.paint(cg);
        } finally {
            cg.dispose();
        }

        if (wasDoubleBuffered) {
            ((JComponent) c).setDoubleBuffered(true);
        }

        c.setBounds(-w, -h, 0, 0);
    }


    /**
     * Calls this.paintComponent(g, c, p, x, y, w, h, false).
     */
    @Override
    public void paintComponent(Graphics g, Component c, Container p, int x, int y, int w, int h) {
        paintComponent(g, c, p, x, y, w, h, false);
    }


    /**
     * Calls this.paintComponent() with the rectangles x,y,width,height fields.
     */
    @Override
    public void paintComponent(Graphics g, Component c, Container p, Rectangle r) {
        paintComponent(g, c, p, r.x, r.y, r.width, r.height);
    }
}
