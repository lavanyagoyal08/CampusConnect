package ui.student;

import model.Student;
import service.RecommendationService;
import ui.components.EventCard;
import ui.components.RoundedPanel;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * OpportunityMatchPanel — Feature 1: Opportunity Match full view.
 * Shows recommended events and clubs ranked by interest match score.
 */
public class OpportunityMatchPanel extends JPanel {

    private final StudentFrame          studentFrame;
    private final RecommendationService recService = new RecommendationService();

    private JPanel eventGrid;
    private JPanel clubGrid;

    public OpportunityMatchPanel(StudentFrame studentFrame) {
        this.studentFrame = studentFrame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);
        build();
    }

    private void build() {
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(UIConstants.PALE_BG);
        inner.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Opportunity Match ✨");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.NAVY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Events and clubs ranked by match to your selected interests. Update interests in My Profile.");
        sub.setFont(UIConstants.FONT_BODY);
        sub.setForeground(UIConstants.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        JLabel eventsTitle = sectionTitle("Matched Events 📅");
        eventGrid = new JPanel(new GridLayout(0, 2, 14, 14));
        eventGrid.setOpaque(false);
        eventGrid.setAlignmentX(LEFT_ALIGNMENT);

        JLabel clubsTitle = sectionTitle("Matched Clubs 🏛️");
        clubGrid = new JPanel(new GridLayout(0, 2, 14, 14));
        clubGrid.setOpaque(false);
        clubGrid.setAlignmentX(LEFT_ALIGNMENT);

        inner.add(title);
        inner.add(Box.createVerticalStrut(4));
        inner.add(sub);
        inner.add(Box.createVerticalStrut(20));
        inner.add(eventsTitle);
        inner.add(Box.createVerticalStrut(10));
        inner.add(eventGrid);
        inner.add(Box.createVerticalStrut(24));
        inner.add(clubsTitle);
        inner.add(Box.createVerticalStrut(10));
        inner.add(clubGrid);

        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setBackground(UIConstants.PALE_BG);
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh() {
        Student student = studentFrame.getStudent();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            List<RecommendationService.RecommendedEvent> events;
            List<RecommendationService.RecommendedClub>  clubs;

            @Override
            protected Void doInBackground() throws Exception {
                events = recService.getRecommendedEvents(student);
                clubs  = recService.getRecommendedClubs(student);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();

                    // Events
                    eventGrid.removeAll();
                    if (events == null || events.isEmpty()) {
                        eventGrid.add(emptyState("No matched events found. Select your interests in My Profile to see recommendations!"));
                    } else {
                        for (var rec : events) {
                            JPanel wrapper = new JPanel();
                            wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
                            wrapper.setOpaque(false);

                            EventCard card = new EventCard(rec.getEvent());
                            card.setOnClick(() -> studentFrame.openEventDetail(rec.getEvent()));

                            JLabel reasonLbl = new JLabel("  💡 " + rec.getReason());
                            reasonLbl.setFont(UIConstants.FONT_SMALL_BOLD);
                            reasonLbl.setForeground(UIConstants.NAVY_HOVER);

                            wrapper.add(card);
                            wrapper.add(reasonLbl);
                            eventGrid.add(wrapper);
                        }
                    }

                    // Clubs
                    clubGrid.removeAll();
                    if (clubs == null || clubs.isEmpty()) {
                        clubGrid.add(emptyState("No matched clubs found for your current interests."));
                    } else {
                        for (var rec : clubs) {
                            clubGrid.add(buildClubMatchCard(rec));
                        }
                    }

                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    System.err.println("[OpportunityMatchPanel] Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private RoundedPanel buildClubMatchCard(RecommendationService.RecommendedClub rec) {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel catBadge = new JLabel(rec.getClub().getCategory());
        catBadge.setFont(UIConstants.FONT_SMALL_BOLD);
        catBadge.setForeground(UIConstants.NAVY);
        catBadge.setBackground(UIConstants.SKY_BLUE);
        catBadge.setOpaque(true);
        catBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        JLabel name = new JLabel(rec.getClub().getName());
        name.setFont(UIConstants.FONT_BODY_BOLD);
        name.setForeground(UIConstants.NAVY);

        JLabel reason = new JLabel("💡 " + rec.getReason());
        reason.setFont(UIConstants.FONT_SMALL);
        reason.setForeground(UIConstants.TEXT_MUTED);

        JLabel viewBtn = new JLabel("View Club Details →");
        viewBtn.setFont(UIConstants.FONT_SMALL_BOLD);
        viewBtn.setForeground(UIConstants.NAVY);
        viewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                studentFrame.openClubDetail(rec.getClub());
            }
        });

        card.add(catBadge);
        card.add(Box.createVerticalStrut(6));
        card.add(name);
        card.add(Box.createVerticalStrut(4));
        card.add(reason);
        card.add(Box.createVerticalStrut(8));
        card.add(viewBtn);
        return card;
    }

    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIConstants.FONT_SUBHEADING);
        l.setForeground(UIConstants.NAVY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JPanel emptyState(String msg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        JLabel l = new JLabel(msg);
        l.setFont(UIConstants.FONT_BODY);
        l.setForeground(UIConstants.TEXT_MUTED);
        p.add(l);
        return p;
    }
}
