/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import sun.swing.MenuItemLayoutHelper;

public class FlareMenuItemLayoutHelper extends MenuItemLayoutHelper {

    public static final ColumnAlignment LTR_ALIGNMENT_2 = new ColumnAlignment(SwingConstants.LEFT, SwingConstants.LEFT, SwingConstants.LEFT,
            SwingConstants.RIGHT, SwingConstants.RIGHT);

    public FlareMenuItemLayoutHelper(JMenuItem mi, Icon checkIcon, Icon arrowIcon, Rectangle viewRect, int gap, String accDelimiter, boolean isLeftToRight,
            Font font, Font accFont, boolean useCheckAndArrow, String propertyPrefix) {
        super(mi, checkIcon, arrowIcon, viewRect, gap, accDelimiter, isLeftToRight, font, accFont, useCheckAndArrow, propertyPrefix);
    }

    private String accDelimiter;

    @Override
    protected void reset(JMenuItem mi, Icon checkIcon, Icon arrowIcon, Rectangle viewRect, int gap, String accDelimiter, boolean isLeftToRight, Font font,
            Font accFont, boolean useCheckAndArrow, String propertyPrefix) {
        this.accDelimiter = accDelimiter;
        super.reset(mi, checkIcon, arrowIcon, viewRect, gap, accDelimiter, isLeftToRight, font, accFont, useCheckAndArrow, propertyPrefix);
    }

    @Override
    protected void calcWidthsAndHeights() {
        setAccText(getAccText(accDelimiter));

        super.calcWidthsAndHeights();

        if (!isTopLevelMenu()) {
            getArrowSize().setWidth(getArrowSize().getWidth() + 5);
        }
    }

    protected void calcMaxWidths() {
        calcMaxWidth(getCheckSize(), MAX_CHECK_WIDTH);
        calcMaxWidth(getArrowSize(), MAX_ARROW_WIDTH);
        calcMaxWidth(getAccSize(), MAX_ACC_WIDTH);

        if (isColumnLayout()) {
            calcMaxWidth(getIconSize(), MAX_ICON_WIDTH);
            calcMaxWidth(getTextSize(), MAX_TEXT_WIDTH);
            int curGap = getGap();
            if ((getIconSize().getMaxWidth() == 0) || (getTextSize().getMaxWidth() == 0)) {
                curGap = 0;
            }
            getLabelSize().setMaxWidth(calcMaxValue(MAX_LABEL_WIDTH, getIconSize().getMaxWidth() + getTextSize().getMaxWidth() + curGap));
        } else {
            // We shouldn't use current icon and text widths
            // in maximal widths calculation for complex layout.
            getIconSize().setMaxWidth(getParentIntProperty(MAX_ICON_WIDTH));
            calcMaxWidth(getLabelSize(), MAX_LABEL_WIDTH);
            // If maxLabelWidth is wider
            // than the widest icon + the widest text + gap,
            // we should update the maximal text witdh
            int candidateTextWidth = getLabelSize().getMaxWidth() - getIconSize().getMaxWidth();
            if (getIconSize().getMaxWidth() > 0) {
                candidateTextWidth -= getGap();
            }
            getTextSize().setMaxWidth(calcMaxValue(MAX_TEXT_WIDTH, candidateTextWidth));
        }
    }

    private String getAccText(String acceleratorDelimiter) {
        StringBuilder builder = new StringBuilder();
        KeyStroke accelerator = getMenuItem().getAccelerator();
        if (accelerator != null) {
            builder.append("    ");
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                if ((modifiers & InputEvent.META_MASK) != 0) {
                    builder.append(Toolkit.getProperty("AWT.meta", "Meta"));
                    builder.append(acceleratorDelimiter);
                }
                if ((modifiers & InputEvent.CTRL_MASK) != 0) {
                    builder.append(Toolkit.getProperty("AWT.control", "Ctrl"));
                    builder.append(acceleratorDelimiter);
                }
                if ((modifiers & InputEvent.ALT_MASK) != 0) {
                    builder.append(Toolkit.getProperty("AWT.alt", "Alt"));
                    builder.append(acceleratorDelimiter);
                }
                if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
                    builder.append(Toolkit.getProperty("AWT.shift", "Shift"));
                    builder.append(acceleratorDelimiter);
                }
                if ((modifiers & InputEvent.ALT_GRAPH_MASK) != 0) {
                    builder.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
                    builder.append(acceleratorDelimiter);
                }
                if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
                    builder.append(Toolkit.getProperty("AWT.button1", "Button1"));
                    builder.append(acceleratorDelimiter);
                }
            }
            int keyCode = accelerator.getKeyCode();
            if (keyCode != 0) {
                builder.append(KeyEvent.getKeyText(keyCode));
            } else {
                builder.append(accelerator.getKeyChar());
            }
        }
        return builder.toString();
    }

    @Override
    public LayoutResult layoutMenuItem() {
        LayoutResult lr = createLayoutResult();
        prepareForLayout(lr);

        if (isColumnLayout()) {
            if (isLeftToRight()) {
                doLTRColumnLayout(lr, getLTRColumnAlignment());
            } else {
                doRTLColumnLayout(lr, getRTLColumnAlignment());
            }
        } else {
            if (isLeftToRight()) {
                doLTRComplexLayout(lr, getLTRColumnAlignment());
            } else {
                doRTLComplexLayout(lr, getRTLColumnAlignment());
            }
        }

        alignAccCheckAndArrowVertically(lr);
        return lr;
    }

    private LayoutResult createLayoutResult() {
        return new LayoutResult( //
                new Rectangle(getIconSize().getWidth(), getIconSize().getHeight()), //
                new Rectangle(getTextSize().getWidth(), getTextSize().getHeight()), //
                new Rectangle(getAccSize().getWidth(), getAccSize().getHeight()), //
                new Rectangle(getCheckSize().getWidth(), getCheckSize().getHeight()), //
                new Rectangle(getArrowSize().getWidth(), getArrowSize().getHeight()), //
                new Rectangle(getLabelSize().getWidth(), getLabelSize().getHeight()) //
        );
    }

    private void doLTRColumnLayout(LayoutResult lr, ColumnAlignment alignment) {
        // Set maximal width for all the five basic rects
        // (three other ones are already maximal)
        lr.getIconRect().width = getIconSize().getMaxWidth();
        lr.getTextRect().width = getTextSize().getMaxWidth();

        // Set X coordinates
        // All rects will be aligned at the left side
        calcXPositionsLTR(getViewRect().x, getLeadingGap(), getGap(), lr.getCheckRect(), lr.getIconRect(), lr.getTextRect());

        // Tune getAfterCheckIconGap()
        if (lr.getCheckRect().width > 0) { // there is the getAfterCheckIconGap()
            lr.getIconRect().x += getAfterCheckIconGap() - getGap();
            lr.getTextRect().x += getAfterCheckIconGap() - getGap();
        }

        calcXPositionsRTL(getViewRect().x + getViewRect().width, getLeadingGap(), getGap(), lr.getArrowRect(), lr.getAccRect());

        // Take into account minimal text offset
        int textOffset = lr.getTextRect().x - getViewRect().x;
        if (!isTopLevelMenu() && (textOffset < getMinTextOffset())) {
            lr.getTextRect().x += getMinTextOffset() - textOffset;
        }

        alignRects(lr, alignment);

        // Set Y coordinate for text and icon.
        // Y coordinates for other rects
        // will be calculated later in layoutMenuItem.
        calcTextAndIconYPositions(lr);

        // Calculate valid X and Y coordinates for getLabelRect()
        lr.setLabelRect(lr.getTextRect().union(lr.getIconRect()));
    }

    private void doLTRComplexLayout(LayoutResult lr, ColumnAlignment alignment) {
        lr.getLabelRect().width = getLabelSize().getMaxWidth();

        // Set X coordinates
        calcXPositionsLTR(getViewRect().x, getLeadingGap(), getGap(), lr.getCheckRect(), lr.getLabelRect());

        // Tune getAfterCheckIconGap()
        if (lr.getCheckRect().width > 0) { // there is the getAfterCheckIconGap()
            lr.getLabelRect().x += getAfterCheckIconGap() - getGap();
        }

        calcXPositionsRTL(getViewRect().x + getViewRect().width, getLeadingGap(), getGap(), lr.getArrowRect(), lr.getAccRect());

        // Take into account minimal text offset
        int labelOffset = lr.getLabelRect().x - getViewRect().x;
        if (!isTopLevelMenu() && (labelOffset < getMinTextOffset())) {
            lr.getLabelRect().x += getMinTextOffset() - labelOffset;
        }

        alignRects(lr, alignment);

        // Center getLabelRect() vertically
        calcLabelYPosition(lr);

        layoutIconAndTextInLabelRect(lr);
    }

    private void doRTLColumnLayout(LayoutResult lr, ColumnAlignment alignment) {
        // Set maximal width for all the five basic rects
        // (three other ones are already maximal)
        lr.getIconRect().width = getIconSize().getMaxWidth();
        lr.getTextRect().width = getTextSize().getMaxWidth();

        // Set X coordinates
        calcXPositionsRTL(getViewRect().x + getViewRect().width, getLeadingGap(), getGap(), lr.getCheckRect(), lr.getIconRect(), lr.getTextRect());

        // Tune the gap after check icon
        if (lr.getCheckRect().width > 0) { // there is the gap after check icon
            lr.getIconRect().x -= getAfterCheckIconGap() - getGap();
            lr.getTextRect().x -= getAfterCheckIconGap() - getGap();
        }

        calcXPositionsLTR(getViewRect().x, getLeadingGap(), getGap(), lr.getArrowRect(), lr.getAccRect());

        // Take into account minimal text offset
        int textOffset = (getViewRect().x + getViewRect().width) - (lr.getTextRect().x + lr.getTextRect().width);
        if (!isTopLevelMenu() && (textOffset < getMinTextOffset())) {
            lr.getTextRect().x -= getMinTextOffset() - textOffset;
        }

        alignRects(lr, alignment);

        // Set Y coordinates for text and icon.
        // Y coordinates for other rects
        // will be calculated later in layoutMenuItem.
        calcTextAndIconYPositions(lr);

        // Calculate valid X and Y coordinate for getLabelRect()
        lr.setLabelRect(lr.getTextRect().union(lr.getIconRect()));
    }

    private void doRTLComplexLayout(LayoutResult lr, ColumnAlignment alignment) {
        lr.getLabelRect().width = getLabelSize().getMaxWidth();

        // Set X coordinates
        calcXPositionsRTL(getViewRect().x + getViewRect().width, getLeadingGap(), getGap(), lr.getCheckRect(), lr.getLabelRect());

        // Tune the gap after check icon
        if (lr.getCheckRect().width > 0) { // there is the gap after check icon
            lr.getLabelRect().x -= getAfterCheckIconGap() - getGap();
        }

        calcXPositionsLTR(getViewRect().x, getLeadingGap(), getGap(), lr.getArrowRect(), lr.getAccRect());

        // Take into account minimal text offset
        int labelOffset = (getViewRect().x + getViewRect().width) - (lr.getLabelRect().x + lr.getLabelRect().width);
        if (!isTopLevelMenu() && (labelOffset < getMinTextOffset())) {
            lr.getLabelRect().x -= getMinTextOffset() - labelOffset;
        }

        alignRects(lr, alignment);

        // Center getLabelRect() vertically
        calcLabelYPosition(lr);

        layoutIconAndTextInLabelRect(lr);
    }

    private void alignRects(LayoutResult lr, ColumnAlignment alignment) {
        alignRect(lr.getCheckRect(), alignment.getCheckAlignment(), getCheckSize().getOrigWidth());
        alignRect(lr.getIconRect(), alignment.getIconAlignment(), getIconSize().getOrigWidth());
        alignRect(lr.getTextRect(), alignment.getTextAlignment(), getTextSize().getOrigWidth());
        alignRect(lr.getAccRect(), alignment.getAccAlignment(), getAccSize().getOrigWidth());
        alignRect(lr.getArrowRect(), alignment.getArrowAlignment(), getArrowSize().getOrigWidth());
    }

    private void alignRect(Rectangle rect, int alignment, int origWidth) {
        if (alignment == SwingConstants.RIGHT) {
            rect.x = rect.x + rect.width - origWidth;
        }
        rect.width = origWidth;
    }

    protected void layoutIconAndTextInLabelRect(LayoutResult lr) {
        lr.setTextRect(new Rectangle());
        lr.setIconRect(new Rectangle());
        SwingUtilities.layoutCompoundLabel(getMenuItem(), getFontMetrics(), getText(), getIcon(), getVerticalAlignment(), getHorizontalAlignment(),
                getVerticalTextPosition(), getHorizontalTextPosition(), lr.getLabelRect(), lr.getIconRect(), lr.getTextRect(), getGap());
    }

    private void calcXPositionsLTR(int startXPos, int leadingGap, int gap, Rectangle... rects) {
        int curXPos = startXPos + leadingGap;
        for (Rectangle rect : rects) {
            rect.x = curXPos;
            if (rect.width > 0) {
                curXPos += rect.width + gap;
            }
        }
    }

    private void calcXPositionsRTL(int startXPos, int leadingGap, int gap, Rectangle... rects) {
        int curXPos = startXPos - leadingGap;
        for (Rectangle rect : rects) {
            rect.x = curXPos - rect.width;
            if (rect.width > 0) {
                curXPos -= rect.width + gap;
            }
        }
    }

    /**
     * Sets Y coordinates of text and icon
     * taking into account the vertical alignment
     */
    private void calcTextAndIconYPositions(LayoutResult lr) {
        if (getVerticalAlignment() == SwingUtilities.TOP) {
            lr.getTextRect().y = (int) (getViewRect().y + (float) lr.getLabelRect().height / 2 - (float) lr.getTextRect().height / 2);
            lr.getIconRect().y = (int) (getViewRect().y + (float) lr.getLabelRect().height / 2 - (float) lr.getIconRect().height / 2);
        } else if (getVerticalAlignment() == SwingUtilities.CENTER) {
            lr.getTextRect().y = (int) (getViewRect().y + (float) getViewRect().height / 2 - (float) lr.getTextRect().height / 2);
            lr.getIconRect().y = (int) (getViewRect().y + (float) getViewRect().height / 2 - (float) lr.getIconRect().height / 2);
        } else if (getVerticalAlignment() == SwingUtilities.BOTTOM) {
            lr.getTextRect().y = (int) (getViewRect().y + getViewRect().height - (float) lr.getLabelRect().height / 2 - (float) lr.getTextRect().height / 2);
            lr.getIconRect().y = (int) (getViewRect().y + getViewRect().height - (float) lr.getLabelRect().height / 2 - (float) lr.getIconRect().height / 2);
        }
    }

    /**
     * Sets getLabelRect() Y coordinate
     * taking into account the vertical alignment
     */
    private void calcLabelYPosition(LayoutResult lr) {
        if (getVerticalAlignment() == SwingUtilities.TOP) {
            lr.getLabelRect().y = getViewRect().y;
        } else if (getVerticalAlignment() == SwingUtilities.CENTER) {
            lr.getLabelRect().y = (int) (getViewRect().y + (float) getViewRect().height / 2 - (float) lr.getLabelRect().height / 2);
        } else if (getVerticalAlignment() == SwingUtilities.BOTTOM) {
            lr.getLabelRect().y = getViewRect().y + getViewRect().height - lr.getLabelRect().height;
        }
    }

    private void alignAccCheckAndArrowVertically(LayoutResult lr) {
        lr.getAccRect().y = (int) (lr.getLabelRect().y + (float) lr.getLabelRect().height / 2 - (float) lr.getAccRect().height / 2);
        fixVerticalAlignment(lr, lr.getAccRect());
        if (useCheckAndArrow()) {
            lr.getArrowRect().y = (int) (lr.getLabelRect().y + (float) lr.getLabelRect().height / 2 - (float) lr.getArrowRect().height / 2);
            lr.getCheckRect().y = (int) (lr.getLabelRect().y + (float) lr.getLabelRect().height / 2 - (float) lr.getCheckRect().height / 2);
            fixVerticalAlignment(lr, lr.getArrowRect());
            fixVerticalAlignment(lr, lr.getCheckRect());
        }
    }

    private void fixVerticalAlignment(LayoutResult lr, Rectangle r) {
        int delta = 0;
        if (r.y < getViewRect().y) {
            delta = getViewRect().y - r.y;
        } else if (r.y + r.height > getViewRect().y + getViewRect().height) {
            delta = getViewRect().y + getViewRect().height - r.y - r.height;
        }
        if (delta != 0) {
            lr.getCheckRect().y += delta;
            lr.getIconRect().y += delta;
            lr.getTextRect().y += delta;
            lr.getAccRect().y += delta;
            lr.getArrowRect().y += delta;
            lr.getLabelRect().y += delta;
        }
    }

    @Override
    public ColumnAlignment getLTRColumnAlignment() {
        return LTR_ALIGNMENT_2;
    }
}
