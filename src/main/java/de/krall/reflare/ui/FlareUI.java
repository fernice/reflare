package de.krall.reflare.ui;

import de.krall.reflare.Styleable;
import java.awt.Component;
import java.awt.Graphics;

public interface FlareUI {

    Styleable getStyleable();

    void paintBorder(Component c, Graphics g, int x, int y, int width, int height);
}
