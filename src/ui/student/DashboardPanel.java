package ui.student;

import dao.EventDAO;
import dao.RegistrationDAO;
import dao.SavedEventDAO;
import model.Event;
import model.Student;
import service.PassportService;
import service.RecommendationService;
import ui.components.EventCard;
import ui.components.RoundedPanel;
import ui.components.StyledButton;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * DashboardPanel — Student landing screen.
 * Displays personalized greeting, quick participation stats, Feature 1: "Picked for Your Interests"
 * (Opportunity Match), upcoming campus events, and quick navigation shortcuts.
 */
public class DashboardPanel extends JPanel {

    private final StudentFrame studentFrame;
    private final Student student;
    private final RecommendationService recService = new RecommendationService();
    private final PassportService passportService = new PassportService();

    private JPanel upcomingGrid;
    private JPanel recommendedGrid;
    private JLabel statsReg, statsAttend, statsSaved, passportBadgeCount;

    public DashboardPanel(StudentFrame sf) {
        this.studentFrame = sf;
        this.student = sf.getStudent();
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(UIConstants.PALE_BG);
        inner.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // ── Personalized Welcome Banner ──────────────────────────────────────────
        RoundedPanel banner = new RoundedPanel(UIConstants.NAVY);
        banner.setLayout(new BorderLayout(16, 0));
        banner.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        banner.setAlignmentX(LEFT_ALIGNMENT);

        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.setOpaque(false);

        String firstName = student.getName() != null ? student.getName().split("\\s+")[0] : "Student";
        JLabel g = new JLabel("Welcome back, " + firstName + "! 👋");
        g.setFont(UIConstants.FONT_LARGE_BOLD);
        g.setForeground(UIConstants.WHITE);

        JLabel s = new JLabel(student.getYearDeptLabel() + "  ·  “Your campus. Your people. Your possibilities.”");
        s.setFont(UIConstants.FONT_BODY);
        s.setForeground(UIConstants.SKY_BLUE);

        txt.add(g);
        txt.add(Box.createVerticalStrut(4));
        txt.add(s);

        JPanel bannerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bannerBtns.setOpaque(false);

        StyledButton exploreBtn = new StyledButton("Beyond the Classroom 🚀", StyledButton.Style.YELLOW);
        exploreBtn.addActionListener(e -> studentFrame.navigateTo(StudentFrame.BEYOND_CLASSROOM));

        StyledButton clubsBtn = new StyledButton("Discover Clubs 🏛️", StyledButton.Style.SECONDARY);
        clubsBtn.addActionListener(e -> studentFrame.navigateTo(StudentFrame.CLUBS));

        bannerBtns.add(clubsBtn);
        bannerBtns.add(exploreBtn);

        banner.add(txt, BorderLayout.CENTER);
        banner.add(bannerBtns, BorderLayout.EAST);

        // ── Stats Summary Row ────────────────────────────────────────────────────
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        statsRow.setAlignmentX(LEFT_ALIGNMENT);

        statsReg = new JLabel("0");
        statsAttend = new JLabel("0");
        statsSaved = new JLabel("0");
        passportBadgeCount = new JLabel("0");

        statsRow.add(statCard("📋", "Registered Events", statsReg, StudentFrame.MY_CAMPUS));
        statsRow.add(statCard("✅", "Verified Attended", statsAttend, StudentFrame.PASSPORT));
        statsRow.add(statCard("⭐", "Saved Events", statsSaved, StudentFrame.MY_CAMPUS));
        statsRow.add(statCard("🎒", "Campus Badges", passportBadgeCount, StudentFrame.PASSPORT));

        // ── Feature 1: "Picked for Your Interests" (Opportunity Match) ───────────
        JPanel matchedSectionHdr = sectionHdr("Picked for Your Interests ✨", "Update Interests", () -> studentFrame.navigateTo(StudentFrame.PROFILE));
        matchedSectionHdr.setAlignmentX(LEFT_ALIGNMENT);

        recommendedGrid = new JPanel(new GridLayout(0, 2, 14, 14));
        recommendedGrid.setOpaque(false);
        recommendedGrid.setAlignmentX(LEFT_ALIGNMENT);

        // ── Upcoming Events Section ──────────────────────────────────────────────
        JPanel upcomingSectionHdr = sectionHdr("Upcoming Campus Events 📅", "View All Events →", () -> studentFrame.navigateTo(StudentFrame.EVENTS));
        upcomingSectionHdr.setAlignmentX(LEFT_ALIGNMENT);

        upcomingGrid = new JPanel(new GridLayout(0, 2, 14, 14));
        upcomingGrid.setOpaque(false);
        upcomingGrid.setAlignmentX(LEFT_ALIGNMENT);

        inner.add(banner);
        inner.add(Box.createVerticalStrut(18));
        inner.add(statsRow);
        inner.add(Box.createVerticalStrut(22));
        inner.add(matchedSectionHdr);
        inner.add(Box.createVerticalStrut(10));
        inner.add(recommendedGrid);
        inner.add(Box.createVerticalStrut(24));
        inner.add(upcomingSectionHdr);
        inner.add(Box.createVerticalStrut(10));
        inner.add(upcomingGrid);

        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setBackground(UIConstants.PALE_BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel statCard(String emoji, String label, JLabel val, String navTarget) {
        RoundedPanel c = new RoundedPanel();
        c.setLayout(new BorderLayout(10, 0));
        c.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(emoji);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        val.setFont(UIConstants.FONT_LARGE_BOLD);
        val.setForeground(UIConstants.NAVY);

        JLabel l = new JLabel(label);
        l.setFont(UIConstants.FONT_SMALL);
        l.setForeground(UIConstants.TEXT_MUTED);

        text.add(val);
        text.add(l);

        c.add(icon, BorderLayout.WEST);
        c.add(text, BorderLayout.CENTER);

        c.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                studentFrame.navigateTo(navTarget);
            }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                c.setBorderColor(UIConstants.SKY_BLUE);
                c.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                c.setBorderColor(UIConstants.CARD_BORDER);
                c.repaint();
            }
        });

        return c;
    }

    private JPanel sectionHdr(String title, String actionLabel, Runnable onAction) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel h = new JLabel(title);
        h.setFont(UIConstants.FONT_SUBHEADING);
        h.setForeground(UIConstants.NAVY);

        JLabel va = new JLabel(actionLabel);
        va.setFont(UIConstants.FONT_SMALL_BOLD);
        va.setForeground(UIConstants.NAVY);
        va.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        va.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (onAction != null) onAction.run();
            }
        });

        row.add(h, BorderLayout.WEST);
        row.add(va, BorderLayout.EAST);
        return row;
    }

    public void refresh() {
        int sid = student.getId();
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            int regCount, attendedCount, savedCount, badgesCount;
            List<Event> upcoming;
            List<RecommendationService.RecommendedEvent> recommendations;

            @Override
            protected Void doInBackground() throws Exception {
                RegistrationDAO regDAO = new RegistrationDAO();
                regCount = (int) regDAO.findByStudent(sid).stream()
                    .filter(x -> "REGISTERED".equalsIgnoreCase(x.getStatus())).count();
                attendedCount = regDAO.findAttendedByStudent(sid).size();
                savedCount = new SavedEventDAO().findSavedEventsByStudent(sid).size();

                var stamps = passportService.getStampsWithCategory(sid);
                badgesCount = passportService.computeBadgesFromStamps(stamps).size();

                upcoming = new EventDAO().findUpcomingLimit(4);
                recommendations = recService.getRecommendedEvents(student);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    statsReg.setText(String.valueOf(regCount));
                    statsAttend.setText(String.valueOf(attendedCount));
                    statsSaved.setText(String.valueOf(savedCount));
                    passportBadgeCount.setText(String.valueOf(badgesCount));

                    // Render "Picked for Your Interests"
                    recommendedGrid.removeAll();
                    if (student.getInterests() == null || student.getInterests().isEmpty()) {
                        recommendedGrid.add(emptyState("You haven't selected your interests yet. Click 'Update Interests' to personalize your feed!"));
                    } else if (recommendations.isEmpty()) {
                        recommendedGrid.add(emptyState("No upcoming events currently match your selected interests (" +
                            String.join(", ", student.getInterests()) + "). Explore Beyond the Classroom!"));
                    } else {
                        int limit = Math.min(4, recommendations.size());
                        for (int i = 0; i < limit; i++) {
                            var rec = recommendations.get(i);
                            JPanel wrapper = new JPanel();
                            wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
                            wrapper.setOpaque(false);

                            EventCard c = new EventCard(rec.getEvent());
                            c.setOnClick(() -> studentFrame.openEventDetail(rec.getEvent()));

                            JLabel reasonLabel = new JLabel("  💡 " + rec.getReason());
                            reasonLabel.setFont(UIConstants.FONT_SMALL_BOLD);
                            reasonLabel.setForeground(UIConstants.NAVY_HOVER);

                            wrapper.add(c);
                            wrapper.add(Box.createVerticalStrut(3));
                            wrapper.add(reasonLabel);
                            recommendedGrid.add(wrapper);
                        }
                    }

                    // Render "Upcoming Campus Events"
                    upcomingGrid.removeAll();
                    if (upcoming.isEmpty()) {
                        upcomingGrid.add(emptyState("No upcoming events scheduled right now. Check back soon!"));
                    } else {
                        for (Event ev : upcoming) {
                            EventCard c = new EventCard(ev);
                            c.setOnClick(() -> studentFrame.openEventDetail(ev));
                            upcomingGrid.add(c);
                        }
                    }

                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    System.err.println("[DashboardPanel] Refresh error: " + ex.getMessage());
                }
            }
        };
        w.execute();
    }

    private JLabel emptyState(String m) {
        JLabel l = new JLabel(m);
        l.setFont(UIConstants.FONT_BODY);
        l.setForeground(UIConstants.TEXT_MUTED);
        return l;
    }
}
