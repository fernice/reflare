package de.krall.reflare;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class Main {

    public static void main(final String... args) {
        FlareLookAndFeel.init();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Modern Flare");
        frame.setSize(600, 400);

        final JPanel panel = new JPanel(new BorderLayout());

        final JTextField textField = new JTextField();
        textField.setColumns(15);

        panel.add(textField, BorderLayout.NORTH);

        frame.setContentPane(panel);

        frame.setVisible(true);
    }

}
