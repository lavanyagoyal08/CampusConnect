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

public class LoginPanel extends JPanel {

    private final MainFrame   mainFrame;
    private final AuthService authService = new AuthService();

    private JTextField     emailField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);
        build();
    }

    private void build() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(440);
        split.setDividerSize(0);
        split.setEnabled(false);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.NAVY);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        JLabel logo = new JLabel("🎓");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel brand = new JLabel("CampusConnect");
        brand.setFont(UIConstants.FONT_LARGE_BOLD);
        brand.setForeground(UIConstants.WHITE);
        brand.setAlignmentX(CENTER_ALIGNMENT);

        JPanel accent = new JPanel();
        accent.setBackground(UIConstants.YELLOW);
        accent.setPreferredSize(new Dimension(60, 4));
        accent.setMaximumSize(new Dimension(60, 4));
        accent.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><div style='text-align:center;color:#A9D9FF;'>Your campus.<br>Your people.<br>Your possibilities.</div></html>");
        tagline.setFont(UIConstants.FONT_BODY);
        tagline.setAlignmentX(CENTER_ALIGNMENT);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        inner.add(logo);
        inner.add(Box.createVerticalStrut(16));
        inner.add(brand);
        inner.add(Box.createVerticalStrut(10));
        inner.add(accent);
        inner.add(Box.createVerticalStrut(14));
        inner.add(tagline);

        panel.add(inner);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UIConstants.WHITE);

        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(380, 460));

        JLabel title = new JLabel("Welcome back 👋");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.NAVY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your student account");
        sub.setFont(UIConstants.FONT_BODY);
        sub.setForeground(UIConstants.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JLabel emailLbl = lbl("Email Address");
        emailField = tf();

        JLabel passLbl = lbl("Password");
        passwordField = new JPasswordField();
        styleField(passwordField);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIConstants.FONT_SMALL);
        errorLabel.setForeground(UIConstants.RED);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);

        StyledButton loginBtn = new StyledButton("Sign In", StyledButton.Style.PRIMARY);
        loginBtn.setAlignmentX(LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.addActionListener(e -> performLogin());

        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        registerRow.setOpaque(false);
        registerRow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel noAcc = new JLabel("Don't have an account? ");
        noAcc.setFont(UIConstants.FONT_SMALL);
        noAcc.setForeground(UIConstants.TEXT_MUTED);
        JLabel regLink = new JLabel("Register");
        regLink.setFont(UIConstants.FONT_SMALL_BOLD);
        regLink.setForeground(UIConstants.NAVY);
        regLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { mainFrame.showPanel(MainFrame.REGISTER_PANEL); }
        });
        registerRow.add(noAcc); registerRow.add(regLink);

        JLabel adminLink = new JLabel("Admin Sign In →");
        adminLink.setFont(UIConstants.FONT_SMALL);
        adminLink.setForeground(UIConstants.TEXT_MUTED);
        adminLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        adminLink.setAlignmentX(LEFT_ALIGNMENT);
        adminLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { mainFrame.showPanel(MainFrame.ADMIN_LOGIN_PANEL); }
        });

        emailField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> performLogin());

        card.add(title); card.add(Box.createVerticalStrut(4));
        card.add(sub);   card.add(Box.createVerticalStrut(28));
        card.add(emailLbl); card.add(Box.createVerticalStrut(4));
        addFull(card, emailField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLbl); card.add(Box.createVerticalStrut(4));
        addFull(card, passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel); card.add(Box.createVerticalStrut(4));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(18));
        card.add(registerRow);
        card.add(Box.createVerticalStrut(10));
        card.add(adminLink);

        outer.add(card);
        return outer;
    }

    private void performLogin() {
        errorLabel.setText(" ");
        String email = emailField.getText().trim();
        String pass  = new String(passwordField.getPassword());
        try {
            var student = authService.loginStudent(email, pass);
            SessionManager.setCurrentStudent(student);
            mainFrame.openStudentDashboard(student);
        } catch (AuthException ex) {
            errorLabel.setText(ex.getMessage());
        } catch (SQLException ex) {
            errorLabel.setText("Database error. Please check your connection.");
        }
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIConstants.FONT_SMALL_BOLD);
        l.setForeground(UIConstants.TEXT_DARK);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField tf() {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setFont(UIConstants.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.setAlignmentX(LEFT_ALIGNMENT);
    }

    private void addFull(JPanel p, JComponent c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(c);
    }
}
