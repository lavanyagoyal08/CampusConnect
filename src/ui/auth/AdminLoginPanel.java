package ui.auth;

import service.AuthService;
import service.AuthService.AuthException;
import ui.MainFrame;
import ui.components.RoundedPanel;
import ui.components.StyledButton;
import util.SessionManager;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class AdminLoginPanel extends JPanel {

    private final MainFrame   mainFrame;
    private final AuthService authService = new AuthService();
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;

    public AdminLoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.NAVY);
        build();
    }

    private void build() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UIConstants.NAVY);

        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 44, 40, 44));
        card.setPreferredSize(new Dimension(400, 420));

        JLabel icon = new JLabel("🔐");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Admin Sign In");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.NAVY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("CampusConnect Admin Studio");
        sub.setFont(UIConstants.FONT_BODY);
        sub.setForeground(UIConstants.TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JPanel accent = new JPanel();
        accent.setBackground(UIConstants.YELLOW);
        accent.setPreferredSize(new Dimension(48, 3));
        accent.setMaximumSize(new Dimension(48, 3));
        accent.setAlignmentX(CENTER_ALIGNMENT);

        emailField = tf();
        passwordField = new JPasswordField(); styleF(passwordField);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIConstants.FONT_SMALL);
        errorLabel.setForeground(UIConstants.RED);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);

        StyledButton loginBtn = new StyledButton("Sign In to Admin Studio", StyledButton.Style.PRIMARY);
        loginBtn.setAlignmentX(LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.addActionListener(e -> performLogin());

        JLabel backLink = new JLabel("← Back to Student Sign In");
        backLink.setFont(UIConstants.FONT_SMALL);
        backLink.setForeground(UIConstants.TEXT_MUTED);
        backLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLink.setAlignmentX(LEFT_ALIGNMENT);
        backLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { mainFrame.showPanel(MainFrame.LOGIN_PANEL); }
        });

        emailField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> performLogin());

        card.add(icon); card.add(Box.createVerticalStrut(8));
        card.add(title); card.add(Box.createVerticalStrut(4));
        card.add(sub); card.add(Box.createVerticalStrut(10));
        card.add(accent); card.add(Box.createVerticalStrut(24));
        card.add(lbl("Admin Email")); card.add(Box.createVerticalStrut(4));
        addFull(card, emailField);
        card.add(Box.createVerticalStrut(14));
        card.add(lbl("Password")); card.add(Box.createVerticalStrut(4));
        addFull(card, passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel); card.add(Box.createVerticalStrut(4));
        card.add(loginBtn); card.add(Box.createVerticalStrut(18));
        card.add(backLink);

        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void performLogin() {
        errorLabel.setText(" ");
        try {
            var admin = authService.loginAdmin(emailField.getText().trim(), new String(passwordField.getPassword()));
            SessionManager.setCurrentAdmin(admin);
            mainFrame.openAdminDashboard(admin);
        } catch (AuthException ex) {
            errorLabel.setText(ex.getMessage());
        } catch (SQLException ex) {
            errorLabel.setText("Database error. Check your connection.");
        }
    }

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t); l.setFont(UIConstants.FONT_SMALL_BOLD);
        l.setForeground(UIConstants.TEXT_DARK); l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
    private JTextField tf() { JTextField f = new JTextField(); styleF(f); return f; }
    private void styleF(JTextField f) {
        f.setFont(UIConstants.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(8,12,8,12)));
        f.setAlignmentX(LEFT_ALIGNMENT);
    }
    private void addFull(JPanel p, JComponent c) { c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); p.add(c); }
}
