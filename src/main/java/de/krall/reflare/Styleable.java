package de.krall.reflare;

import de.krall.reflare.std.Vec;
import de.krall.reflare.style.Background;
import de.krall.reflare.style.Border;
import de.krall.reflare.t.TBounds;
import de.krall.reflare.t.TColor;
import de.krall.reflare.t.TInsets;
import de.krall.reflare.t.TRadius;
import de.krall.reflare.value.ComputedValues;
import de.krall.reflare.value.computed.Clip;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Arc2D.Float;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

public class Styleable {

    private ComputedValues computedValues = new ComputedValues();

    public static void setRenderingHints(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public void paintBackground(final Component component, final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;

        setRenderingHints(g);

        final Background background = computedValues.getBackground();
        final Border border = computedValues.getBorder();

        final Dimension size = component.getSize();

        final TInsets borderWidth = border.getWidth();
        final TRadius borderRadius = border.getRadius();

        final TInsets margin = computedValues.getMargin().getInsets();
        final TInsets padding = computedValues.getPadding().getInsets();

        final Vec<Clip> backgroundClips = background.getClip();

        final Clip backgroundClip = backgroundClips.getLast();

        final Shape clip = computeBackgroundClip(backgroundClip, size, borderWidth, borderRadius, margin, padding);

        g2.setClip(clip);

        g2.setColor(background.getColor());
        g2.fill(clip);
    }

    public void paintBorder(Component component, Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;

        setRenderingHints(g);

        final Border border = computedValues.getBorder();

        final Dimension size = component.getSize();

        final TInsets borderWidth = border.getWidth();
        final TRadius borderRadius = border.getRadius();
        final TColor borderColor = border.getColor();

        final TInsets margin = computedValues.getMargin().getInsets();
        final TInsets padding = computedValues.getPadding().getInsets();

        if (hasOnlyOneColor(borderColor)) {
            final Shape borderClip = computeBackgroundClip(Clip.BORDER_BOX, size, borderWidth, borderRadius, margin, padding);
            final Shape paddingClip = computeBackgroundClip(Clip.PADDING_BOX, size, borderWidth, borderRadius, margin, padding);

            final Area clip = new Area(borderClip);
            clip.subtract(new Area(paddingClip));

            g2.setColor(borderColor.top);
            g2.fill(clip);
        } else {
            final Shape[] edges = computeBorderEdges(size, borderWidth, borderRadius, margin);

            g2.setColor(borderColor.top);
            g2.fill(edges[0]);

            g2.setColor(borderColor.right);
            g2.fill(edges[1]);

            g2.setColor(borderColor.bottom);
            g2.fill(edges[2]);

            g2.setColor(borderColor.left);
            g2.fill(edges[3]);
        }
    }

    private static boolean hasOnlyOneColor(final TColor color) {
        return color.top.equals(color.right) && color.right.equals(color.bottom) && color.bottom.equals(color.left);
    }

    private static Shape computeBackgroundClip(final Clip clip, final Dimension size, final TInsets borderWidth, final TRadius borderRadius,
            final TInsets margin, final TInsets padding) {
        final TInsets width = new TInsets();
        final TBounds bounds = new TBounds(size);

        switch (clip) {
            case CONTENT_BOX:
                width.increase(padding);
                bounds.reduce(padding);
                // fallthrough
            case PADDING_BOX:
                width.increase(borderWidth);
                bounds.reduce(borderWidth);
                // fallthrough
            case BORDER_BOX:
                bounds.reduce(margin);
        }

        return computeRoundedRectangle(bounds, borderRadius, width);
    }

    /**
     * Computes a rounded rectangle shape using the bounds as a basic basic rectangle an refining it by adding round corners
     * accordingly. The corner radius shrinks with the specified "border" (width).
     *
     * @param rect  the basic rectangle
     * @param radii the corner radius at the corner of the virtual space
     * @param width the width that define the virtual space together with the rectangle
     * @return the rounded rectangle
     */
    private static Path2D computeRoundedRectangle(final TBounds rect, final TRadius radii, final TInsets width) {
        final Path2D path = new Path2D.Float();

        float tls = width.top < radii.topLeft ? radii.topLeft - width.top : 0;
        float tlt = width.left < radii.topLeft ? radii.topLeft - width.left : 0;
        float trs = width.top < radii.topRight ? radii.topRight - width.top : 0;
        float trt = width.right < radii.topRight ? radii.topRight - width.right : 0;
        float brs = width.bottom < radii.bottomRight ? radii.bottomRight - width.bottom : 0;
        float brt = width.right < radii.bottomRight ? radii.bottomRight - width.right : 0;
        float bls = width.bottom < radii.bottomLeft ? radii.bottomLeft - width.bottom : 0;
        float blt = width.left < radii.bottomLeft ? radii.bottomLeft - width.left : 0;

        path.moveTo(rect.x + tlt, rect.y);
        path.lineTo(rect.x + rect.width - trt, rect.y);
        path.quadTo(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + trs);
        path.lineTo(rect.x + rect.width, rect.y + rect.height - brs);
        path.quadTo(rect.x + rect.width, rect.y + rect.height, rect.x + rect.width - brt, rect.y + rect.height);
        path.lineTo(rect.x + blt, rect.y + rect.height);
        path.quadTo(rect.x, rect.y + rect.height, rect.x, rect.y + rect.height - bls);
        path.lineTo(rect.x, rect.y + tls);
        path.quadTo(rect.x, rect.y, rect.x + tlt, rect.y);

        return path;
    }

    private static Shape[] computeBorderEdges(final Dimension size, final TInsets borderWidth, final TRadius borderRadius, final TInsets margin) {
        final TBounds bounds = new TBounds(size);

        bounds.reduce(margin);

        final Shape[] edges = new Shape[4];

        edges[0] = computeTopBorder(bounds, borderRadius, borderWidth);
        edges[1] = computeRightBorder(bounds, borderRadius, borderWidth);
        edges[2] = computeBottomBorder(bounds, borderRadius, borderWidth);
        edges[3] = computeLeftBorder(bounds, borderRadius, borderWidth);

        return edges;
    }

    private static Path2D computeTopBorder(final TBounds rect, final TRadius radii, final TInsets width) {
        final Path2D path = new Path2D.Float();

        float tlt = width.top < radii.topLeft ? radii.topLeft - width.top : 0;
        float tls = width.left < radii.topLeft ? radii.topLeft - width.left : 0;
        float trt = width.top < radii.topRight ? radii.topRight - width.top : 0;
        float trs = width.right < radii.topRight ? radii.topRight - width.right : 0;

        path.moveTo(rect.x + radii.topLeft, rect.y);
        path.lineTo(rect.x + rect.width - radii.topRight, rect.y);

        path.append(new Float(rect.x + rect.width - radii.topRight * 2, rect.y, radii.topRight * 2, radii.topRight * 2, 90, -45, Arc2D.OPEN), true);
        path.append(new Float(rect.x + rect.width - Math.max(radii.topRight, width.right) - trs, rect.y + width.top, trs * 2, trt * 2, 45, 45, Arc2D.OPEN),
                true);

        path.lineTo(rect.x + Math.max(radii.topLeft, width.left), rect.y + width.top);

        path.append(new Float(rect.x + Math.max(radii.topLeft, width.left) - tls, rect.y + Math.max(radii.topRight, width.top) - tlt, tls * 2, tlt * 2, 90, 45,
                Arc2D.OPEN), true);
        path.append(new Float(rect.x, rect.y, radii.topLeft * 2, radii.topLeft * 2, 135, -45, Arc2D.OPEN), true);

        return path;
    }

    private static Path2D computeRightBorder(final TBounds rect, final TRadius radii, final TInsets width) {
        final Path2D path = new Path2D.Float();

        float brt = width.bottom < radii.bottomRight ? radii.bottomRight - width.bottom : 0;
        float brs = width.right < radii.bottomRight ? radii.bottomRight - width.right : 0;
        float trt = width.top < radii.topRight ? radii.topRight - width.top : 0;
        float trs = width.right < radii.topRight ? radii.topRight - width.right : 0;

        path.moveTo(rect.x + rect.width, rect.y + radii.topRight);
        path.lineTo(rect.x + rect.width, rect.y + rect.height - radii.bottomRight);

        path.append(new Float(rect.x + rect.width - radii.bottomRight * 2, rect.y + rect.height - radii.bottomRight * 2, radii.bottomRight * 2,
                radii.bottomRight * 2, 0, -45, Arc2D.OPEN), true);
        path.append(new Float(rect.x + rect.width - Math.max(radii.bottomRight, width.right) - brs,
                rect.y + rect.height - Math.max(radii.bottomRight, width.bottom) - brt, brs * 2, brt * 2, -45, 45, Arc2D.OPEN), true);

        path.lineTo(rect.x + rect.width - width.right, rect.y + Math.max(radii.topRight, width.bottom));

        path.append(new Float(rect.x + rect.width - Math.max(radii.topRight, width.right) - trs, rect.y + Math.max(radii.topRight, width.top) - trt, trs * 2,
                trt * 2, 0, 45, Arc2D.OPEN), true);
        path.append(new Float(rect.x + rect.width - radii.topRight * 2, rect.y, radii.topRight * 2, radii.topRight * 2, 45, -45, Arc2D.OPEN), true);

        return path;
    }

    private static Path2D computeBottomBorder(final TBounds rect, final TRadius radii, final TInsets width) {
        final Path2D path = new Path2D.Float();

        float blt = width.bottom < radii.bottomLeft ? radii.bottomLeft - width.bottom : 0;
        float bls = width.left < radii.bottomLeft ? radii.bottomLeft - width.left : 0;
        float brt = width.bottom < radii.bottomRight ? radii.bottomRight - width.bottom : 0;
        float brs = width.right < radii.bottomRight ? radii.bottomRight - width.right : 0;

        path.moveTo(rect.x + radii.topLeft, rect.y + rect.height);
        path.lineTo(rect.x + rect.width - radii.bottomRight, rect.y + rect.height);

        path.append(new Float(rect.x + rect.width - radii.bottomRight * 2, rect.y + rect.height - radii.bottomRight * 2, radii.bottomRight * 2,
                radii.bottomRight * 2, -90, 45, Arc2D.OPEN), true);
        path.append(new Float(rect.x + rect.width - Math.max(radii.bottomRight, width.right) - brs,
                rect.y + rect.height - Math.max(radii.bottomRight, width.bottom) - brt, brs * 2, brt * 2, -45, -45, Arc2D.OPEN), true);

        path.lineTo(rect.x + Math.max(radii.topLeft, width.left), rect.y + rect.height - width.bottom);

        path.append(
                new Float(rect.x + Math.max(radii.bottomLeft, width.left) - bls, rect.y + rect.height - Math.max(radii.bottomLeft, width.bottom) - blt, bls * 2,
                        blt * 2, -90, -45, Arc2D.OPEN), true);
        path.append(new Float(rect.x, rect.y + rect.height - radii.bottomLeft * 2, radii.bottomLeft * 2, radii.bottomLeft * 2, -135, 45, Arc2D.OPEN), true);

        return path;
    }

    private static Path2D computeLeftBorder(final TBounds rect, final TRadius radii, final TInsets width) {
        final Path2D path = new Path2D.Float();

        float blt = width.bottom < radii.bottomLeft ? radii.bottomLeft - width.bottom : 0;
        float bls = width.left < radii.bottomLeft ? radii.bottomLeft - width.left : 0;
        float tlt = width.top < radii.topLeft ? radii.topLeft - width.top : 0;
        float tls = width.left < radii.topLeft ? radii.topLeft - width.left : 0;

        path.moveTo(rect.x, rect.y + radii.topLeft);
        path.lineTo(rect.x, rect.y + rect.height - radii.bottomLeft);

        path.append(new Float(rect.x, rect.y + rect.height - radii.bottomLeft * 2, radii.bottomLeft * 2, radii.bottomLeft * 2, 180, 45, Arc2D.OPEN), true);
        path.append(
                new Float(rect.x + Math.max(radii.bottomLeft, width.left) - bls, rect.y + rect.height - Math.max(radii.bottomLeft, width.bottom) - blt, bls * 2,
                        blt * 2, 225, -45, Arc2D.OPEN), true);

        path.lineTo(rect.x + width.left, rect.y + Math.max(radii.topLeft, width.top));

        path.append(new Float(rect.x + Math.max(radii.topLeft, width.left) - tls, rect.y + Math.max(radii.topLeft, width.top) - tlt, tls * 2, tlt * 2, 180, -45,
                Arc2D.OPEN), true);
        path.append(new Float(rect.x, rect.y, radii.topLeft * 2, radii.topLeft * 2, 135, 45, Arc2D.OPEN), true);

        return path;
    }

    public TInsets getMargin() {
        return computedValues.getMargin().getInsets();
    }

    public TInsets getPadding() {
        return computedValues.getPadding().getInsets();
    }

    public TInsets getBorderWidth() {
        return computedValues.getBorder().getWidth();
    }
}
