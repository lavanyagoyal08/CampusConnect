package ui.student;

import dao.StudentDAO;
import model.Student;
import service.RecommendationService;
import ui.components.RoundedPanel;
import ui.components.StyledButton;
import util.SessionManager;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProfilePanel — View and edit student profile and select multiple interests.
 * Interests are persisted delimited in the students table and trigger immediate
 * refresh of Feature 1: Opportunity Match recommendations.
 */
public class ProfilePanel extends JPanel {

    private final StudentFrame studentFrame;
    private final Student student;
    private final StudentDAO studentDAO = new StudentDAO();

    private JTextField nameField, deptField;
    private JComboBox<Integer> yearCombo;
    private JLabel emailDisplay, statusLabel;
    private final List<String> selectedInterests = new ArrayList<>();
    private final List<JToggleButton> interestButtons = new ArrayList<>();

    public ProfilePanel(StudentFrame sf) {
        this.studentFrame = sf;
        this.student = sf.getStudent();
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);
        buildUI();
    }

    private void buildUI() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UIConstants.PALE_BG);

        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));
        card.setPreferredSize(new Dimension(580, 720));

        JLabel title = new JLabel("My Profile 👤");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.NAVY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage your academic details and interests for personalized recommendations.");
        subtitle.setFont(UIConstants.FONT_SMALL);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        emailDisplay = new JLabel(student.getEmail());
        emailDisplay.setFont(UIConstants.FONT_BODY_BOLD);
        emailDisplay.setForeground(UIConstants.NAVY);
        emailDisplay.setAlignmentX(LEFT_ALIGNMENT);

        nameField = tf(student.getName());
        deptField = tf(student.getDepartment() != null ? student.getDepartment() : "");
        yearCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        yearCombo.setFont(UIConstants.FONT_BODY);
        yearCombo.setSelectedItem(student.getYearOfStudy());
        yearCombo.setAlignmentX(LEFT_ALIGNMENT);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIConstants.FONT_SMALL_BOLD);
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);

        StyledButton saveProfileBtn = new StyledButton("Save Profile Details", StyledButton.Style.PRIMARY);
        saveProfileBtn.setAlignmentX(LEFT_ALIGNMENT);
        saveProfileBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveProfileBtn.addActionListener(e -> saveFullProfile());

        JSeparator sep = new JSeparator();
        sep.setForeground(UIConstants.CARD_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);

        JLabel interestTitle = new JLabel("My Interests (Used for Opportunity Match) ✨");
        interestTitle.setFont(UIConstants.FONT_SUBHEADING);
        interestTitle.setForeground(UIConstants.NAVY);
        interestTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel interestHelp = new JLabel("Select all topics you enjoy. We use these to match clubs and events for you.");
        interestHelp.setFont(UIConstants.FONT_SMALL);
        interestHelp.setForeground(UIConstants.TEXT_MUTED);
        interestHelp.setAlignmentX(LEFT_ALIGNMENT);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        chips.setOpaque(false);
        chips.setAlignmentX(LEFT_ALIGNMENT);

        selectedInterests.clear();
        if (student.getInterests() != null) {
            selectedInterests.addAll(student.getInterests());
        }

        for (String interest : RecommendationService.getAllInterestLabels()) {
            JToggleButton chip = new JToggleButton(interest);
            chip.setFont(UIConstants.FONT_SMALL);
            chip.setFocusPainted(false);
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            boolean isSelected = selectedInterests.contains(interest);
            chip.setSelected(isSelected);
            styleChip(chip, isSelected);

            chip.addItemListener(e -> {
                if (chip.isSelected()) {
                    if (!selectedInterests.contains(interest)) selectedInterests.add(interest);
                    styleChip(chip, true);
                } else {
                    selectedInterests.remove(interest);
                    styleChip(chip, false);
                }
            });

            interestButtons.add(chip);
            chips.add(chip);
        }

        StyledButton saveInterestsBtn = new StyledButton("Save Interests & Refresh Matches", StyledButton.Style.YELLOW);
        saveInterestsBtn.setAlignmentX(LEFT_ALIGNMENT);
        saveInterestsBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveInterestsBtn.addActionListener(e -> saveFullProfile());

        card.add(title);
        card.add(Box.createVerticalStrut(2));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(16));

        addField(card, "Email Address (Read-only login)", emailDisplay);
        addField(card, "Full Name", nameField);
        addField(card, "Department / Major", deptField);
        addField(card, "Year of Study", yearCombo);

        card.add(statusLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(saveProfileBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));
        card.add(interestTitle);
        card.add(Box.createVerticalStrut(2));
        card.add(interestHelp);
        card.add(Box.createVerticalStrut(8));
        card.add(chips);
        card.add(Box.createVerticalStrut(12));
        card.add(saveInterestsBtn);

        wrapper.add(card);
        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);
    }

    private void saveFullProfile() {
        String newName = nameField.getText().trim();
        String newDept = deptField.getText().trim();
        int newYear = (int) yearCombo.getSelectedItem();

        if (newName.isEmpty()) {
            showStatus("Full Name cannot be empty.", false);
            return;
        }

        student.setName(newName);
        student.setDepartment(newDept);
        student.setYearOfStudy(newYear);
        student.setInterests(new ArrayList<>(selectedInterests));

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return studentDAO.updateProfile(student);
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        SessionManager.setCurrentStudent(student);
                        showStatus("Profile and interests successfully saved! 🎉", true);
                    } else {
                        showStatus("Failed to update profile in database.", false);
                    }
                } catch (Exception ex) {
                    showStatus("Error: " + ex.getMessage(), false);
                }
            }
        };
        worker.execute();
    }

    public void refresh() {
        if (student != null) {
            nameField.setText(student.getName());
            deptField.setText(student.getDepartment() != null ? student.getDepartment() : "");
            yearCombo.setSelectedItem(student.getYearOfStudy());

            selectedInterests.clear();
            if (student.getInterests() != null) {
                selectedInterests.addAll(student.getInterests());
            }

            for (JToggleButton chip : interestButtons) {
                boolean sel = selectedInterests.contains(chip.getText());
                chip.setSelected(sel);
                styleChip(chip, sel);
            }
        }
    }

    private void showStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setForeground(success ? UIConstants.GREEN : UIConstants.RED);
    }

    private void addField(JPanel panel, String label, JComponent comp) {
        JLabel l = new JLabel(label);
        l.setFont(UIConstants.FONT_SMALL_BOLD);
        l.setForeground(UIConstants.NAVY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        comp.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(l);
        panel.add(Box.createVerticalStrut(4));
        panel.add(comp);
        panel.add(Box.createVerticalStrut(10));
    }

    private JTextField tf(String val) {
        JTextField f = new JTextField(val != null ? val : "");
        f.setFont(UIConstants.FONT_BODY);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private void styleChip(JToggleButton chip, boolean selected) {
        chip.setBackground(selected ? UIConstants.YELLOW : UIConstants.WHITE);
        chip.setForeground(selected ? UIConstants.NAVY : UIConstants.TEXT_DARK);
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(selected ? UIConstants.YELLOW : UIConstants.CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        chip.setContentAreaFilled(true);
        chip.setOpaque(true);
    }
}
