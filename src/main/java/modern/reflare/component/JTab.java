package modern.reflare.component;

import modern.reflare.plaf.TabUI;
import javax.swing.JComponent;
import javax.swing.UIManager;

public class JTab extends JComponent {

    public JTab() {
        setDoubleBuffered(true);
        updateUI();
    }

    @Override
    public String getUIClassID() {
        return "TabUI";
    }

    public void updateUI() {
        setUI((TabUI) UIManager.getUI(this));
    }

    public void setUI(TabUI ui) {
        this.ui = ui;
    }

    public TabUI getUI() {
        return (TabUI) ui;
    }
}
