package ui;

import model.Admin;
import model.Student;
import ui.auth.AdminLoginPanel;
import ui.auth.LoginPanel;
import ui.auth.RegisterPanel;
import ui.admin.AdminFrame;
import ui.student.StudentFrame;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame — Root JFrame. Uses CardLayout for auth screens;
 * swaps content pane when user logs in.
 */
public class MainFrame extends JFrame {

    public static final String LOGIN_PANEL       = "LOGIN";
    public static final String REGISTER_PANEL    = "REGISTER";
    public static final String ADMIN_LOGIN_PANEL = "ADMIN_LOGIN";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     cardPanel  = new JPanel(cardLayout);

    public MainFrame() {
        setTitle("CampusConnect — Your Campus Companion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        buildAuthPanels();
        setContentPane(cardPanel);
        showPanel(LOGIN_PANEL);
    }

    public void showPanel(String name) { cardLayout.show(cardPanel, name); }

    public void openStudentDashboard(Student student) {
        StudentFrame sf = new StudentFrame(student, this);
        setContentPane(sf);
        revalidate(); repaint();
    }

    public void openAdminDashboard(Admin admin) {
        AdminFrame af = new AdminFrame(admin, this);
        setContentPane(af);
        revalidate(); repaint();
    }

    public void returnToLogin() {
        util.SessionManager.clear();
        buildAuthPanels();
        setContentPane(cardPanel);
        revalidate(); repaint();
        showPanel(LOGIN_PANEL);
    }

    private void buildAuthPanels() {
        cardPanel.removeAll();
        cardPanel.add(new LoginPanel(this),      LOGIN_PANEL);
        cardPanel.add(new RegisterPanel(this),   REGISTER_PANEL);
        cardPanel.add(new AdminLoginPanel(this), ADMIN_LOGIN_PANEL);
    }
}
