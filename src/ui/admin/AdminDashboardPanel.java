package ui.admin;

import dao.ClubDAO;
import dao.EventDAO;
import dao.RegistrationDAO;
import dao.StudentDAO;
import ui.components.RoundedPanel;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * AdminDashboardPanel — Overview statistics calculated from actual database records.
 * Displays: Total Students, Total Clubs, Total Events, Upcoming Events, Total Registrations, Verified Attendance.
 */
public class AdminDashboardPanel extends JPanel {

    private final AdminFrame adminFrame;
    private JLabel studentsCount, eventsCount, clubsCount, upcomingCount, regsCount, verifiedCount;

    public AdminDashboardPanel(AdminFrame adminFrame) {
        this.adminFrame = adminFrame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);
        build();
        loadStats();
    }

    private void build() {
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(UIConstants.PALE_BG);
        inner.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Welcome
        JLabel title = new JLabel("Admin Dashboard 📊");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.NAVY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Live campus activity and participation metrics calculated directly from database records.");
        sub.setFont(UIConstants.FONT_BODY);
        sub.setForeground(UIConstants.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // Stats grid: 2 rows of 3 columns
        JPanel statsGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        statsGrid.setAlignmentX(LEFT_ALIGNMENT);

        studentsCount = new JLabel("…");
        clubsCount    = new JLabel("…");
        eventsCount   = new JLabel("…");
        upcomingCount = new JLabel("…");
        regsCount     = new JLabel("…");
        verifiedCount = new JLabel("…");

        statsGrid.add(statCard("👤", "Total Students",      studentsCount));
        statsGrid.add(statCard("🏛️", "Total Clubs",         clubsCount));
        statsGrid.add(statCard("📅", "Total Events",        eventsCount));
        statsGrid.add(statCard("🚀", "Upcoming Events",     upcomingCount));
        statsGrid.add(statCard("📋", "Total Registrations", regsCount));
        statsGrid.add(statCard("✅", "Verified Attendance", verifiedCount));

        // Quick action cards
        JPanel navGrid = new JPanel(new GridLayout(1, 3, 16, 16));
        navGrid.setOpaque(false);
        navGrid.setAlignmentX(LEFT_ALIGNMENT);
        navGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        navGrid.add(quickLink("📅 Manage Events",  "Add, edit, open/close, and cancel events.", AdminFrame.MANAGE_EVENTS));
        navGrid.add(quickLink("🏛️ Manage Clubs",   "Add, edit, and organize student clubs.",    AdminFrame.MANAGE_CLUBS));
        navGrid.add(quickLink("✅ Attendance",      "Verify attendance for Campus Passport.",    AdminFrame.ATTENDANCE));

        inner.add(title);
        inner.add(Box.createVerticalStrut(4));
        inner.add(sub);
        inner.add(Box.createVerticalStrut(20));
        inner.add(statsGrid);
        inner.add(Box.createVerticalStrut(24));

        JLabel navTitle = new JLabel("Quick Actions");
        navTitle.setFont(UIConstants.FONT_SUBHEADING);
        navTitle.setForeground(UIConstants.NAVY);
        navTitle.setAlignmentX(LEFT_ALIGNMENT);
        inner.add(navTitle);
        inner.add(Box.createVerticalStrut(10));
        inner.add(navGrid);

        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);
    }

    public void loadStats() {
        SwingWorker<int[], Void> worker = new SwingWorker<>() {
            @Override
            protected int[] doInBackground() throws Exception {
                EventDAO eventDAO = new EventDAO();
                RegistrationDAO regDAO = new RegistrationDAO();
                return new int[]{
                    new StudentDAO().countAll(),
                    new ClubDAO().countAll(),
                    eventDAO.countAll(),
                    eventDAO.findUpcomingLimit(100).size(),
                    regDAO.countAllRegistrations(),
                    regDAO.countVerifiedAttendances()
                };
            }
            @Override
            protected void done() {
                try {
                    int[] counts = get();
                    studentsCount.setText(String.valueOf(counts[0]));
                    clubsCount.setText(String.valueOf(counts[1]));
                    eventsCount.setText(String.valueOf(counts[2]));
                    upcomingCount.setText(String.valueOf(counts[3]));
                    regsCount.setText(String.valueOf(counts[4]));
                    verifiedCount.setText(String.valueOf(counts[5]));
                } catch (Exception ex) {
                    System.err.println("[AdminDashboard] Stats load error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private JPanel statCard(String emoji, String label, JLabel valueLabel) {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel icon = new JLabel(emoji);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        valueLabel.setFont(UIConstants.FONT_LARGE_BOLD);
        valueLabel.setForeground(UIConstants.NAVY);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(UIConstants.TEXT_MUTED);

        text.add(valueLabel);
        text.add(lbl);
        card.add(icon, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private RoundedPanel quickLink(String title, String desc, String navTarget) {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UIConstants.FONT_BODY_BOLD);
        titleLbl.setForeground(UIConstants.NAVY);

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(UIConstants.FONT_SMALL);
        descLbl.setForeground(UIConstants.TEXT_MUTED);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(descLbl);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                adminFrame.navigateTo(navTarget);
            }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorderColor(UIConstants.SKY_BLUE);
                card.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorderColor(UIConstants.CARD_BORDER);
                card.repaint();
            }
        });
        return card;
    }
}
