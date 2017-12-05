package de.reflare.lnf;

public class RegionPainter {

    /*
    public void paintBackground(final Graphics graphics, final Component component, final ComponentUI ui) {
        final Graphics2D g2 = (Graphics2D) graphics;

        final Rectangle bounds = component.getBounds();

        final CssProperties properties = getCssProperties(ui);

        final Background background = properties.getBackground();

        final Border border = properties.getBorder();
        final Insets borderWidth = border.getWidth();

        switch (background.getBackgroundClip()) {
            case CONTENT_BOX:
                reduce(bounds, properties.getPadding());

            case PADDING_BOX:<
                reduce(bounds, borderWidth);

            case BORDER_BOX:
                reduce(bounds, properties.getMargin());
        }

        final Radii radii = border.getRadii();

        g2.setPaint(background.getColor());

        switch (background.getBackgroundClip()) {
            case CONTENT_BOX:
                g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                break;
            case PADDING_BOX:
                g2.fill(paddingBox(bounds, radii, borderWidth));
                break;
            case BORDER_BOX:
                g2.fill(borderBox(bounds, radii));
                break;
        }
    }

    public void paintBorder(final Graphics graphics, final Component component, final ComponentUI ui) {
        final Graphics2D g2 = (Graphics2D) graphics;

        final CssProperties properties = getCssProperties(ui);

        final Border border = properties.getBorder();
        final Rectangle bounds = component.getBounds();

        final Radii radii = border.getRadii();

        reduce(bounds, properties.getMargin());

        g2.setPaint(border.getColor());
        g2.fill(borderBox(bounds, radii));

        final Insets width = border.getWidth();

        reduce(bounds, width);

        g2.setComposite(AlphaComposite.Clear);

        g2.fill(paddingBox(bounds, radii, width));

        g2.setComposite(AlphaComposite.SrcOver);
    }

    private static void reduce(final Rectangle rectangle, final Insets insets) {
        if (insets != null) {
            rectangle.x += insets.left;
            rectangle.y += insets.top;
            rectangle.width -= insets.left + insets.right;
            rectangle.height -= insets.top + insets.bottom;
        }
    }

    private static Path2D paddingBox(final Rectangle rect, final Radii radii, final Insets width) {
        final Path2D path = new Path2D.Float();

        path.moveTo(rect.x + (radii.topLeft - width.left), rect.y);
        path.lineTo(rect.x + rect.width - (radii.topRight - width.right), rect.y);
        path.quadTo(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + (radii.topRight - width.top));
        path.lineTo(rect.x + rect.width, rect.height - (radii.bottomRight - width.bottom));
        path.quadTo(rect.x + rect.width, rect.y + rect.height, rect.x + rect.width - (radii.bottomRight - width.right), rect.y + rect.height);
        path.lineTo(rect.x + (radii.bottomLeft - width.left), rect.y + rect.height);
        path.quadTo(rect.x, rect.y + rect.height, rect.x, rect.y + rect.height - (radii.bottomLeft - width.bottom));
        path.lineTo(rect.x, rect.y + (radii.topLeft - width.top));
        path.quadTo(rect.x, rect.y, rect.x + (radii.topLeft - width.left), rect.y);

        return path;
    }

    private static Path2D borderBox(final Rectangle rect, final Radii radii) {
        final Path2D path = new Path2D.Float();

        path.moveTo(rect.x + radii.topLeft, rect.y);
        path.lineTo(rect.x + rect.width - radii.topRight, rect.y);
        path.quadTo(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + radii.topRight);
        path.lineTo(rect.x + rect.width, rect.height - radii.bottomRight);
        path.quadTo(rect.x + rect.width, rect.y + rect.height, rect.x + rect.width - radii.bottomRight, rect.y + rect.height);
        path.lineTo(rect.x + radii.bottomLeft, rect.y + rect.height);
        path.quadTo(rect.x, rect.y + rect.height, rect.x, rect.y + rect.height - radii.bottomLeft);
        path.lineTo(rect.x, rect.y + radii.topLeft);
        path.quadTo(rect.x, rect.y, rect.x + radii.topLeft, rect.y);

        return path;
    }

    private static CssProperties getCssProperties(final ComponentUI ui) {
        if (ui instanceof FlareUI) {
            return ((FlareUI) ui).getCssProperties();
        }

        throw new IllegalArgumentException();
    }
    */
}
