package test.components;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.event.KeyEvent;

public class TabbedPaneDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JTabbedPane Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(420, 250);

            JTabbedPane tabbedPane = new JTabbedPane();

            // Tab 1: Profile panel
            JPanel profilePanel = new JPanel();
            profilePanel.add(new JLabel("Name:"));
            profilePanel.add(new JTextField(15));
            tabbedPane.addTab("Profile", null, profilePanel, "Edit user profile");
            tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

            // Tab 2: Actions panel
            JPanel actionsPanel = new JPanel();
            actionsPanel.add(new JButton("Save Changes"));
            tabbedPane.addTab("Actions", null, actionsPanel, "Perform actions");
            tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

            // Tab 3: Info panel
            JPanel infoPanel = new JPanel();
            infoPanel.add(new JLabel("Application version 1.0"));
            tabbedPane.addTab("Info", null, infoPanel, "View app information");
            tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

            frame.add(tabbedPane);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
