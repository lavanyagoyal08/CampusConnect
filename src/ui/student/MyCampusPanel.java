package ui.student;

import dao.EventDAO;
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
import java.util.List;

public class MyCampusPanel extends JPanel {

    private final StudentFrame studentFrame;
    private final Student student;
    private final RegistrationService regService = new RegistrationService();
    private RoundedPanel registeredPanel, savedPanel, historyPanel;

    public MyCampusPanel(StudentFrame sf) {
        this.studentFrame = sf; this.student = sf.getStudent();
        setLayout(new BorderLayout()); setBackground(UIConstants.PALE_BG);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(UIConstants.PALE_BG);
        inner.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel title = new JLabel("My Campus"); title.setFont(UIConstants.FONT_HEADING); title.setForeground(UIConstants.NAVY); title.setAlignmentX(LEFT_ALIGNMENT);
        registeredPanel = section("My Registrations");
        savedPanel      = section("Saved Events");
        historyPanel    = section("Participation History");
        inner.add(title); inner.add(Box.createVerticalStrut(16));
        inner.add(registeredPanel); inner.add(Box.createVerticalStrut(16));
        inner.add(savedPanel); inner.add(Box.createVerticalStrut(16));
        inner.add(historyPanel);

        JScrollPane scroll = new JScrollPane(inner); scroll.setBorder(null); scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);
    }

    private RoundedPanel section(String heading) {
        RoundedPanel p = new RoundedPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        p.setAlignmentX(LEFT_ALIGNMENT); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JLabel h = new JLabel(heading); h.setFont(UIConstants.FONT_SUBHEADING); h.setForeground(UIConstants.NAVY); h.setAlignmentX(LEFT_ALIGNMENT);
        JSeparator sep = new JSeparator(); sep.setForeground(UIConstants.CARD_BORDER); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        p.add(h); p.add(Box.createVerticalStrut(8)); p.add(sep); p.add(Box.createVerticalStrut(8));
        return p;
    }

    public void refresh() {
        SwingWorker<Void,Void> w = new SwingWorker<>() {
            List<Registration> regs; List<Event> saved;
            @Override protected Void doInBackground() throws Exception {
                regs = regService.getMyRegistrations(student.getId());
                saved = regService.getSavedEvents(student.getId());
                return null;
            }
            @Override protected void done() {
                try { get();
                    refill(registeredPanel, regs.stream().filter(r->"REGISTERED".equals(r.getStatus())).toList(), true);
                    refillSaved(savedPanel, saved);
                    refill(historyPanel, regs.stream().filter(r->!"PENDING".equals(r.getAttendanceStatus())).toList(), false);
                    revalidate(); repaint();
                } catch (Exception ex) { System.err.println("[MyCampus] " + ex.getMessage()); }
            }
        };
        w.execute();
    }

    private void refill(RoundedPanel panel, List<Registration> list, boolean showView) {
        while (panel.getComponentCount()>4) panel.remove(panel.getComponentCount()-1);
        if (list.isEmpty()) { panel.add(empty(showView?"No active registrations.":"No history yet.")); return; }
        for (Registration r : list) {
            JPanel row = new JPanel(new BorderLayout(10,0)); row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE,48));
            JPanel left = new JPanel(); left.setLayout(new BoxLayout(left,BoxLayout.Y_AXIS)); left.setOpaque(false);
            JLabel t = new JLabel(r.getEventTitle()!=null?r.getEventTitle():"Event #"+r.getEventId());
            t.setFont(UIConstants.FONT_BODY_BOLD); t.setForeground(UIConstants.TEXT_DARK);
            JLabel d = new JLabel(DateUtil.formatDate(r.getEventDate()));
            d.setFont(UIConstants.FONT_SMALL); d.setForeground(UIConstants.TEXT_MUTED);
            left.add(t); left.add(d);
            JLabel badge = badge(r.getAttendanceStatus());
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,6,0)); right.setOpaque(false); right.add(badge);
            if (showView) {
                StyledButton vb = new StyledButton("View", StyledButton.Style.GHOST); vb.setPreferredSize(new Dimension(60,28));
                vb.addActionListener(e -> { try { Event ev = new EventDAO().findById(r.getEventId()); if (ev!=null) studentFrame.openEventDetail(ev); } catch(Exception ex){} });
                right.add(vb);
            }
            row.add(left, BorderLayout.CENTER); row.add(right, BorderLayout.EAST);
            panel.add(row); panel.add(Box.createVerticalStrut(6));
        }
    }

    private void refillSaved(RoundedPanel panel, List<Event> events) {
        while (panel.getComponentCount()>4) panel.remove(panel.getComponentCount()-1);
        if (events.isEmpty()) { panel.add(empty("No saved events.")); return; }
        for (Event ev : events) {
            JPanel row = new JPanel(new BorderLayout(10,0)); row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE,48));
            JLabel t = new JLabel(ev.getTitle()); t.setFont(UIConstants.FONT_BODY_BOLD); t.setForeground(UIConstants.TEXT_DARK);
            StyledButton vb = new StyledButton("View", StyledButton.Style.GHOST); vb.setPreferredSize(new Dimension(60,28));
            vb.addActionListener(e -> studentFrame.openEventDetail(ev));
            row.add(t, BorderLayout.CENTER); row.add(vb, BorderLayout.EAST);
            panel.add(row); panel.add(Box.createVerticalStrut(6));
        }
    }

    private JLabel badge(String status) {
        String text = switch(status) { case "ATTENDED"->"Attended"; case "NOT_ATTENDED"->"Not Attended"; default->"Pending"; };
        Color fg = switch(status) { case "ATTENDED"->UIConstants.GREEN; case "NOT_ATTENDED"->UIConstants.RED; default->UIConstants.TEXT_MUTED; };
        JLabel l = new JLabel(text); l.setFont(UIConstants.FONT_SMALL_BOLD); l.setForeground(fg); return l;
    }

    private JPanel empty(String msg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT)); p.setOpaque(false);
        JLabel l = new JLabel(msg); l.setFont(UIConstants.FONT_BODY); l.setForeground(UIConstants.TEXT_MUTED); p.add(l); return p;
    }
}
