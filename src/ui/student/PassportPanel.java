package ui.student;

import model.PassportStamp;
import service.PassportService;
import ui.components.RoundedPanel;
import util.DateUtil;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * PassportPanel — Feature 3: Campus Passport.
 * Shows digital event stamps, earned badges, and participation milestones.
 */
public class PassportPanel extends JPanel {

    private final StudentFrame  studentFrame;
    private final PassportService passportService = new PassportService();
    private JPanel stampGrid;
    private JPanel badgeRow;
    private JLabel totalLabel;

    public PassportPanel(StudentFrame studentFrame) {
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

        // ── Header ─────────────────────────────────────────────────────────────
        JLabel title = new JLabel("Campus Passport 🎒");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.NAVY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Your verified participation record — every event leaves a stamp.");
        sub.setFont(UIConstants.FONT_BODY);
        sub.setForeground(UIConstants.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // ── Total count banner ─────────────────────────────────────────────────
        RoundedPanel countBanner = new RoundedPanel(UIConstants.NAVY);
        countBanner.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 12));
        countBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        countBanner.setAlignmentX(LEFT_ALIGNMENT);

        totalLabel = new JLabel("0 events verified");
        totalLabel.setFont(UIConstants.FONT_LARGE_BOLD);
        totalLabel.setForeground(UIConstants.YELLOW);

        JLabel countSub = new JLabel("Verified Attendances");
        countSub.setFont(UIConstants.FONT_BODY);
        countSub.setForeground(UIConstants.SKY_BLUE);

        countBanner.add(new JLabel("🎟️"));
        countBanner.add(totalLabel);
        countBanner.add(new JLabel(" · "));
        countBanner.add(countSub);

        // ── Badge row ──────────────────────────────────────────────────────────
        JLabel badgeTitle = new JLabel("Achievement Badges");
        badgeTitle.setFont(UIConstants.FONT_SUBHEADING);
        badgeTitle.setForeground(UIConstants.NAVY);
        badgeTitle.setAlignmentX(LEFT_ALIGNMENT);

        badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(LEFT_ALIGNMENT);

        // ── Stamps grid ────────────────────────────────────────────────────────
        JLabel stampTitle = new JLabel("Event Stamps");
        stampTitle.setFont(UIConstants.FONT_SUBHEADING);
        stampTitle.setForeground(UIConstants.NAVY);
        stampTitle.setAlignmentX(LEFT_ALIGNMENT);

        stampGrid = new JPanel(new GridLayout(0, 3, 14, 14));
        stampGrid.setOpaque(false);
        stampGrid.setAlignmentX(LEFT_ALIGNMENT);

        inner.add(title);
        inner.add(Box.createVerticalStrut(4));
        inner.add(sub);
        inner.add(Box.createVerticalStrut(16));
        inner.add(countBanner);
        inner.add(Box.createVerticalStrut(20));
        inner.add(badgeTitle);
        inner.add(Box.createVerticalStrut(8));
        inner.add(badgeRow);
        inner.add(Box.createVerticalStrut(20));
        inner.add(stampTitle);
        inner.add(Box.createVerticalStrut(10));
        inner.add(stampGrid);

        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setBackground(UIConstants.PALE_BG);
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh() {
        int studentId = studentFrame.getStudent().getId();
        SwingWorker<List<PassportStamp>, Void> worker = new SwingWorker<>() {
            List<String> badges;

            @Override
            protected List<PassportStamp> doInBackground() throws Exception {
                List<PassportStamp> stamps = passportService.getStampsWithCategory(studentId);
                badges = passportService.computeBadgesFromStamps(stamps);
                return stamps;
            }

            @Override
            protected void done() {
                try {
                    List<PassportStamp> stamps = get();

                    totalLabel.setText(stamps.size() + " event" + (stamps.size() != 1 ? "s" : "") + " verified");

                    // Badges
                    badgeRow.removeAll();
                    if (badges.isEmpty()) {
                        JLabel noBadge = new JLabel("Attend verified events to earn badges! 🏆");
                        noBadge.setFont(UIConstants.FONT_BODY);
                        noBadge.setForeground(UIConstants.TEXT_MUTED);
                        badgeRow.add(noBadge);
                    } else {
                        for (String badge : badges) {
                            JLabel chip = new JLabel(badge);
                            chip.setFont(UIConstants.FONT_BODY_BOLD);
                            chip.setForeground(UIConstants.NAVY);
                            chip.setBackground(UIConstants.YELLOW);
                            chip.setOpaque(true);
                            chip.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
                            badgeRow.add(chip);
                        }
                    }

                    // Stamps
                    stampGrid.removeAll();
                    if (stamps.isEmpty()) {
                        JLabel empty = new JLabel("No stamps yet — register and attend events to collect them!");
                        empty.setFont(UIConstants.FONT_BODY);
                        empty.setForeground(UIConstants.TEXT_MUTED);
                        stampGrid.setLayout(new FlowLayout(FlowLayout.LEFT));
                        stampGrid.add(empty);
                    } else {
                        stampGrid.setLayout(new GridLayout(0, 3, 14, 14));
                        for (PassportStamp stamp : stamps) {
                            stampGrid.add(buildStampCard(stamp));
                        }
                    }

                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    System.err.println("[PassportPanel] Refresh error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private RoundedPanel buildStampCard(PassportStamp stamp) {
        RoundedPanel card = new RoundedPanel(UIConstants.NAVY);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel stampIcon = new JLabel(categoryEmoji(stamp.getEventCategory()));
        stampIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        stampIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel("<html><center>" + stamp.getEventTitle() + "</center></html>");
        titleLbl.setFont(UIConstants.FONT_SMALL_BOLD);
        titleLbl.setForeground(UIConstants.WHITE);
        titleLbl.setAlignmentX(CENTER_ALIGNMENT);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel dateLbl = new JLabel(DateUtil.formatDate(stamp.getEventDate()));
        dateLbl.setFont(UIConstants.FONT_SMALL);
        dateLbl.setForeground(UIConstants.SKY_BLUE);
        dateLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel catLbl = new JLabel(stamp.getEventCategory() == null ? "" : stamp.getEventCategory());
        catLbl.setFont(UIConstants.FONT_SMALL);
        catLbl.setForeground(UIConstants.YELLOW);
        catLbl.setAlignmentX(CENTER_ALIGNMENT);

        card.add(stampIcon);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(dateLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(catLbl);
        return card;
    }

    private String categoryEmoji(String category) {
        if (category == null) return "🎟️";
        return switch (category) {
            case "Technology"      -> "💻";
            case "Cultural"        -> "🎭";
            case "Sports"          -> "🏅";
            case "Social Impact"   -> "🤝";
            case "Entrepreneurship"-> "🚀";
            case "Design"          -> "🎨";
            default                -> "🎟️";
        };
    }
}
