package ui.student;

import model.Event;
import model.Registration;
import model.Student;
import service.RegistrationService;
import ui.components.RoundedPanel;
import ui.components.StyledButton;
import util.DateUtil;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * EventDetailPanel — Event info + register/cancel/save.
 * (Campus Pulse feedback removed for simplicity.)
 */
public class EventDetailPanel extends JPanel {

    private final Event event;
    private final StudentFrame studentFrame;
    private final Student student;
    private final RegistrationService regService = new RegistrationService();

    private JLabel       regStatusLabel;
    private StyledButton registerBtn, cancelBtn, saveBtn;

    public EventDetailPanel(Event event, StudentFrame sf) {
        this.event = event; this.studentFrame = sf; this.student = sf.getStudent();
        setLayout(new BorderLayout()); setBackground(UIConstants.WHITE);
        build(); refreshState();
    }

    private void build() {
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(UIConstants.WHITE);
        inner.setBorder(BorderFactory.createEmptyBorder(24,30,24,30));

        // Tags
        JPanel tags = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0)); tags.setOpaque(false); tags.setAlignmentX(LEFT_ALIGNMENT);
        tags.add(tag(event.getOpportunityType()!=null?event.getOpportunityType():"Event", UIConstants.SKY_BLUE, UIConstants.NAVY));
        tags.add(tag(event.getCategory()!=null?event.getCategory():"", UIConstants.PALE_BG, UIConstants.TEXT_MUTED));

        JLabel titleLbl = new JLabel("<html><b>"+safe(event.getTitle())+"</b></html>");
        titleLbl.setFont(new Font("Segoe UI",Font.BOLD,20)); titleLbl.setForeground(UIConstants.NAVY); titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        // Meta grid
        RoundedPanel meta = new RoundedPanel(UIConstants.PALE_BG);
        meta.setLayout(new GridLayout(3,2,10,8)); meta.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));
        meta.setAlignmentX(LEFT_ALIGNMENT); meta.setMaximumSize(new Dimension(Integer.MAX_VALUE,110));
        meta.add(metaItem("Date", DateUtil.formatDate(event.getEventDate())));
        meta.add(metaItem("Venue", event.getVenue()));
        meta.add(metaItem("Organizer", event.getOrganizer()));
        meta.add(metaItem("Eligibility", event.getEligibility()));
        meta.add(metaItem("Deadline", event.getRegDeadline()!=null?DateUtil.formatDateTime(event.getRegDeadline()):"Not set"));
        meta.add(metaItem("Seats", event.getAvailableSeats()+" / "+event.getCapacity()));

        // Description
        JLabel descTitle = sectionLbl("About this Event");
        JTextArea descArea = new JTextArea(event.getDescription()!=null?event.getDescription():"No description.");
        descArea.setFont(UIConstants.FONT_BODY); descArea.setForeground(UIConstants.TEXT_DARK);
        descArea.setBackground(UIConstants.WHITE); descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true); descArea.setEditable(false); descArea.setFocusable(false);
        descArea.setBorder(null); descArea.setAlignmentX(LEFT_ALIGNMENT);

        // Actions
        JLabel actTitle = sectionLbl("Registration");
        regStatusLabel = new JLabel(" "); regStatusLabel.setFont(UIConstants.FONT_SMALL_BOLD); regStatusLabel.setAlignmentX(LEFT_ALIGNMENT);
        registerBtn = new StyledButton("Register Now", StyledButton.Style.YELLOW);
        cancelBtn   = new StyledButton("Cancel Registration", StyledButton.Style.DANGER);
        saveBtn     = new StyledButton("Save", StyledButton.Style.GHOST);
        registerBtn.addActionListener(e -> doRegister());
        cancelBtn.addActionListener(e -> doCancel());
        saveBtn.addActionListener(e -> doSaveToggle());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); btnRow.setOpaque(false); btnRow.setAlignmentX(LEFT_ALIGNMENT);
        btnRow.add(registerBtn); btnRow.add(cancelBtn); btnRow.add(saveBtn);

        inner.add(tags); inner.add(Box.createVerticalStrut(10));
        inner.add(titleLbl); inner.add(Box.createVerticalStrut(12));
        inner.add(meta); inner.add(Box.createVerticalStrut(14));
        inner.add(descTitle); inner.add(Box.createVerticalStrut(6));
        inner.add(descArea); inner.add(Box.createVerticalStrut(14));
        inner.add(actTitle); inner.add(Box.createVerticalStrut(6));
        inner.add(regStatusLabel); inner.add(Box.createVerticalStrut(6));
        inner.add(btnRow);

        JScrollPane scroll = new JScrollPane(inner); scroll.setBorder(null); scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);
    }

    private void doRegister() {
        try {
            String res = regService.registerForEvent(student.getId(), event.getId());
            regStatusLabel.setText(res);
            regStatusLabel.setForeground(res.contains("success")||res.contains("Success")?UIConstants.GREEN:UIConstants.RED);
            refreshState();
        } catch (Exception ex) { regStatusLabel.setText("Error: "+ex.getMessage()); regStatusLabel.setForeground(UIConstants.RED); }
    }

    private void doCancel() {
        if (JOptionPane.showConfirmDialog(this,"Cancel registration?","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        try {
            boolean ok = regService.cancelRegistration(student.getId(), event.getId());
            regStatusLabel.setText(ok?"Registration cancelled.":"No active registration.");
            refreshState();
        } catch (Exception ex) { regStatusLabel.setText("Error: "+ex.getMessage()); }
    }

    private void doSaveToggle() {
        try {
            if (regService.isEventSaved(student.getId(), event.getId())) {
                regService.unsaveEvent(student.getId(), event.getId()); saveBtn.setText("Save");
            } else {
                regService.saveEvent(student.getId(), event.getId()); saveBtn.setText("Saved");
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void refreshState() {
        SwingWorker<Registration,Void> w = new SwingWorker<>() {
            boolean saved;
            @Override protected Registration doInBackground() throws Exception {
                saved = regService.isEventSaved(student.getId(), event.getId());
                return regService.getRegistration(student.getId(), event.getId());
            }
            @Override protected void done() {
                try {
                    Registration reg = get();
                    saveBtn.setText(saved?"Saved":"Save");
                    if (reg==null || Registration.STATUS_CANCELLED.equals(reg.getStatus())) {
                        registerBtn.setEnabled(event.isRegistrationOpen()); cancelBtn.setEnabled(false);
                    } else {
                        registerBtn.setEnabled(false); cancelBtn.setEnabled(true);
                        regStatusLabel.setText("Registered  |  Attendance: " + reg.getAttendanceStatus());
                        regStatusLabel.setForeground(UIConstants.GREEN);
                    }
                } catch (Exception ex) { System.err.println("[EventDetail] " + ex.getMessage()); }
            }
        };
        w.execute();
    }

    private JLabel tag(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text); l.setFont(UIConstants.FONT_SMALL_BOLD);
        l.setForeground(fg); l.setBackground(bg); l.setOpaque(true);
        l.setBorder(BorderFactory.createEmptyBorder(3,10,3,10)); return l;
    }
    private JPanel metaItem(String label, String value) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); p.setOpaque(false);
        JLabel lbl = new JLabel(label); lbl.setFont(UIConstants.FONT_SMALL_BOLD); lbl.setForeground(UIConstants.TEXT_MUTED);
        JLabel val = new JLabel(value==null||value.isEmpty()?"—":value); val.setFont(UIConstants.FONT_SMALL); val.setForeground(UIConstants.TEXT_DARK);
        p.add(lbl); p.add(val); return p;
    }
    private JLabel sectionLbl(String t) { JLabel l = new JLabel(t); l.setFont(UIConstants.FONT_BODY_BOLD); l.setForeground(UIConstants.NAVY); l.setAlignmentX(LEFT_ALIGNMENT); return l; }
    private String safe(String s) { return s==null?"":s.replace("&","&amp;").replace("<","&lt;"); }
}
