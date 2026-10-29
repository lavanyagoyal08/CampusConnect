package ui.auth;

import dao.StudentDAO;
import service.AuthService;
import service.RecommendationService;
import ui.MainFrame;
import ui.components.RoundedPanel;
import ui.components.StyledButton;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class RegisterPanel extends JPanel {

    private final MainFrame   mainFrame;
    private final AuthService authService = new AuthService();

    private JTextField     nameField, emailField, deptField;
    private JPasswordField passwordField, confirmField;
    private JComboBox<Integer> yearCombo;
    private JLabel         errorLabel;

    private final List<String>         selectedInterests = new ArrayList<>();
    private final List<JToggleButton>  interestButtons   = new ArrayList<>();

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);
        build();
    }

    private void build() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UIConstants.PALE_BG);

        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));
        card.setPreferredSize(new Dimension(500, 680));

        JLabel title = new JLabel("Create your account");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.NAVY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Join CampusConnect — never miss an opportunity.");
        sub.setFont(UIConstants.FONT_BODY);
        sub.setForeground(UIConstants.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        nameField     = tf();
        emailField    = tf();
        passwordField = new JPasswordField(); styleF(passwordField);
        confirmField  = new JPasswordField(); styleF(confirmField);
        deptField     = tf();
        yearCombo     = new JComboBox<>(new Integer[]{1,2,3,4,5});
        yearCombo.setFont(UIConstants.FONT_BODY);
        yearCombo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel interestLbl = new JLabel("Select Your Interests");
        interestLbl.setFont(UIConstants.FONT_SMALL_BOLD);
        interestLbl.setForeground(UIConstants.TEXT_DARK);
        interestLbl.setAlignmentX(LEFT_ALIGNMENT);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        chips.setOpaque(false);
        chips.setAlignmentX(LEFT_ALIGNMENT);

        for (String interest : RecommendationService.getAllInterestLabels()) {
            JToggleButton chip = new JToggleButton(interest);
            chip.setFont(UIConstants.FONT_SMALL);
            chip.setFocusPainted(false);
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            chip.setBackground(UIConstants.WHITE);
            chip.setForeground(UIConstants.NAVY);
            chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.CARD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
            chip.addItemListener(e -> {
                if (chip.isSelected()) { selectedInterests.add(interest); chip.setBackground(UIConstants.NAVY); chip.setForeground(UIConstants.WHITE); }
                else                   { selectedInterests.remove(interest); chip.setBackground(UIConstants.WHITE); chip.setForeground(UIConstants.NAVY); }
            });
            interestButtons.add(chip);
            chips.add(chip);
        }

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIConstants.FONT_SMALL);
        errorLabel.setForeground(UIConstants.RED);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);

        StyledButton registerBtn = new StyledButton("Create Account", StyledButton.Style.YELLOW);
        registerBtn.setAlignmentX(LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        registerBtn.addActionListener(e -> performRegister());

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loginRow.setOpaque(false);
        loginRow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel already = new JLabel("Already have an account? ");
        already.setFont(UIConstants.FONT_SMALL);
        already.setForeground(UIConstants.TEXT_MUTED);
        JLabel signIn = new JLabel("Sign In");
        signIn.setFont(UIConstants.FONT_SMALL_BOLD);
        signIn.setForeground(UIConstants.NAVY);
        signIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signIn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { mainFrame.showPanel(MainFrame.LOGIN_PANEL); }
        });
        loginRow.add(already); loginRow.add(signIn);

        card.add(title); card.add(Box.createVerticalStrut(4));
        card.add(sub);   card.add(Box.createVerticalStrut(18));
        addField(card, "Full Name",          nameField);
        addField(card, "Email Address",      emailField);
        addField(card, "Password (min 6 chars)", passwordField);
        addField(card, "Confirm Password",   confirmField);
        addField(card, "Department/Branch",  deptField);
        addField(card, "Year of Study",      yearCombo);
        card.add(Box.createVerticalStrut(8));
        card.add(interestLbl); card.add(Box.createVerticalStrut(6));
        card.add(chips);
        card.add(Box.createVerticalStrut(10));
        card.add(errorLabel); card.add(Box.createVerticalStrut(4));
        card.add(registerBtn); card.add(Box.createVerticalStrut(14));
        card.add(loginRow);

        center.add(card);
        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);
    }

    private void performRegister() {
        errorLabel.setText(" ");
        try {
            var student = authService.registerStudent(
                nameField.getText().trim(),
                emailField.getText().trim(),
                new String(passwordField.getPassword()),
                new String(confirmField.getPassword()),
                deptField.getText().trim(),
                (int) yearCombo.getSelectedItem()
            );
            if (!selectedInterests.isEmpty()) {
                student.setInterests(new ArrayList<>(selectedInterests));
                new StudentDAO().updateProfile(student);
            }
            JOptionPane.showMessageDialog(this,
                "Account created! Please sign in.", "Welcome", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            mainFrame.showPanel(MainFrame.LOGIN_PANEL);
        } catch (AuthService.AuthException ex) {
            errorLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("Registration failed: " + ex.getMessage());
        }
    }

    private void clearForm() {
        nameField.setText(""); emailField.setText("");
        passwordField.setText(""); confirmField.setText(""); deptField.setText("");
        yearCombo.setSelectedIndex(0);
        selectedInterests.clear();
        for (JToggleButton btn : interestButtons) {
            btn.setSelected(false);
            btn.setBackground(UIConstants.WHITE); btn.setForeground(UIConstants.NAVY);
        }
    }

    private JTextField tf() {
        JTextField f = new JTextField(); f.setFont(UIConstants.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.CARD_BORDER,1,true),
            BorderFactory.createEmptyBorder(8,12,8,12)));
        return f;
    }
    private void styleF(JTextField f) {
        f.setFont(UIConstants.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.CARD_BORDER,1,true),
            BorderFactory.createEmptyBorder(8,12,8,12)));
    }
    private void addField(JPanel p, String label, JComponent c) {
        JLabel l = new JLabel(label); l.setFont(UIConstants.FONT_SMALL_BOLD);
        l.setForeground(UIConstants.TEXT_DARK); l.setAlignmentX(LEFT_ALIGNMENT);
        c.setAlignmentX(LEFT_ALIGNMENT); c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(l); p.add(Box.createVerticalStrut(4)); p.add(c); p.add(Box.createVerticalStrut(12));
    }
}
