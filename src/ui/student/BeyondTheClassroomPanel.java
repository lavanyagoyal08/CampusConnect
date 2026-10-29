package ui.student;

import dao.EventDAO;
import model.Event;
import service.RegistrationService;
import ui.components.EventCard;
import ui.components.RoundedPanel;
import ui.components.StyledButton;
import util.DateUtil;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BeyondTheClassroomPanel — Feature 2: Beyond the Classroom.
 * Dedicated experiential opportunity discovery directory for Hackathons, Workshops,
 * Competitions, Volunteering, Cultural activities, Entrepreneurship, and Skill Development.
 * Uses the existing events table without duplicate data storage.
 */
public class BeyondTheClassroomPanel extends JPanel {

    private final StudentFrame studentFrame;
    private final EventDAO eventDAO = new EventDAO();
    private final RegistrationService regService = new RegistrationService();

    private JTextField searchField;
    private JComboBox<String> typeFilter;
    private JComboBox<String> categoryFilter;
    private JPanel spotlightPanel;
    private JPanel opportunitiesGrid;

    public static final String[] OPPORTUNITY_TYPES = {
        "All Opportunities",
        "Hackathon",
        "Workshop",
        "Competition",
        "Volunteering",
        "Cultural",
        "Entrepreneurship",
        "Skill Development"
    };

    public BeyondTheClassroomPanel(StudentFrame sf) {
        this.studentFrame = sf;
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);
        buildUI();
        refresh();
    }

    private void buildUI() {
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(UIConstants.PALE_BG);
        inner.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // ── Hero Banner ──────────────────────────────────────────────────────────
        RoundedPanel hero = new RoundedPanel(UIConstants.NAVY);
        hero.setLayout(new BorderLayout(20, 0));
        hero.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        hero.setAlignmentX(LEFT_ALIGNMENT);

        JPanel heroText = new JPanel();
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));
        heroText.setOpaque(false);

        JLabel heroTitle = new JLabel("Beyond the Classroom 🚀");
        heroTitle.setFont(UIConstants.FONT_LARGE_BOLD);
        heroTitle.setForeground(UIConstants.WHITE);

        JLabel heroTagline = new JLabel("Explore hackathons, workshops, competitions, volunteering, and skill-building bootcamps.");
        heroTagline.setFont(UIConstants.FONT_BODY);
        heroTagline.setForeground(UIConstants.SKY_BLUE);

        JLabel heroNote = new JLabel("Gain hands-on experience and earn verified participation stamps for your Campus Passport.");
        heroNote.setFont(UIConstants.FONT_SMALL);
        heroNote.setForeground(new Color(0xD0, 0xDF, 0xF5));

        heroText.add(heroTitle);
        heroText.add(Box.createVerticalStrut(4));
        heroText.add(heroTagline);
        heroText.add(Box.createVerticalStrut(2));
        heroText.add(heroNote);

        hero.add(heroText, BorderLayout.CENTER);

        // ── Filter Bar ───────────────────────────────────────────────────────────
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterBar.setOpaque(false);
        filterBar.setAlignmentX(LEFT_ALIGNMENT);

        searchField = new JTextField(18);
        searchField.setFont(UIConstants.FONT_BODY);
        searchField.setToolTipText("Search title, description, or organizer");
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        searchField.addActionListener(e -> applyFilters());

        typeFilter = new JComboBox<>(OPPORTUNITY_TYPES);
        typeFilter.setFont(UIConstants.FONT_BODY);
        typeFilter.addActionListener(e -> applyFilters());

        categoryFilter = new JComboBox<>(new String[]{"All Categories"});
        categoryFilter.setFont(UIConstants.FONT_BODY);
        categoryFilter.addActionListener(e -> applyFilters());

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(UIConstants.FONT_SMALL_BOLD);
        searchBtn.setBackground(UIConstants.NAVY);
        searchBtn.setForeground(UIConstants.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> applyFilters());

        JButton resetBtn = new JButton("Reset");
        resetBtn.setFont(UIConstants.FONT_SMALL);
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            typeFilter.setSelectedIndex(0);
            categoryFilter.setSelectedIndex(0);
            applyFilters();
        });

        filterBar.add(new JLabel("Type:"));
        filterBar.add(typeFilter);
        filterBar.add(new JLabel("Category:"));
        filterBar.add(categoryFilter);
        filterBar.add(searchField);
        filterBar.add(searchBtn);
        filterBar.add(resetBtn);

        // ── Spotlight Section ────────────────────────────────────────────────────
        JLabel spotlightTitle = new JLabel("🔥 Spotlight Opportunities");
        spotlightTitle.setFont(UIConstants.FONT_SUBHEADING);
        spotlightTitle.setForeground(UIConstants.NAVY);
        spotlightTitle.setAlignmentX(LEFT_ALIGNMENT);

        spotlightPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        spotlightPanel.setOpaque(false);
        spotlightPanel.setAlignmentX(LEFT_ALIGNMENT);
        spotlightPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // ── Full Directory Section ───────────────────────────────────────────────
        JLabel allTitle = new JLabel("All Beyond-the-Classroom Opportunities 📂");
        allTitle.setFont(UIConstants.FONT_SUBHEADING);
        allTitle.setForeground(UIConstants.NAVY);
        allTitle.setAlignmentX(LEFT_ALIGNMENT);

        opportunitiesGrid = new JPanel(new GridLayout(0, 2, 14, 14));
        opportunitiesGrid.setOpaque(false);
        opportunitiesGrid.setAlignmentX(LEFT_ALIGNMENT);

        inner.add(hero);
        inner.add(Box.createVerticalStrut(18));
        inner.add(filterBar);
        inner.add(Box.createVerticalStrut(14));
        inner.add(spotlightTitle);
        inner.add(Box.createVerticalStrut(8));
        inner.add(spotlightPanel);
        inner.add(Box.createVerticalStrut(20));
        inner.add(allTitle);
        inner.add(Box.createVerticalStrut(10));
        inner.add(opportunitiesGrid);

        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setBackground(UIConstants.PALE_BG);
        add(scroll, BorderLayout.CENTER);
    }

    public void applyFilters() {
        String kw = searchField.getText().trim();
        String selectedType = (String) typeFilter.getSelectedItem();
        if ("All Opportunities".equals(selectedType)) selectedType = "All";
        String selectedCat = (String) categoryFilter.getSelectedItem();
        if ("All Categories".equals(selectedCat)) selectedCat = "All";

        final String fKw = kw;
        final String fType = selectedType;
        final String fCat = selectedCat;

        SwingWorker<List<Event>, Void> worker = new SwingWorker<>() {
            List<String> allCats;
            @Override
            protected List<Event> doInBackground() throws Exception {
                allCats = eventDAO.findAllCategories();
                return eventDAO.search(fKw, fCat, fType);
            }

            @Override
            protected void done() {
                try {
                    List<Event> results = get();

                    // Sync category dropdown items safely
                    String curCat = (String) categoryFilter.getSelectedItem();
                    categoryFilter.removeAllItems();
                    categoryFilter.addItem("All Categories");
                    for (String c : allCats) categoryFilter.addItem(c);
                    if (curCat != null) categoryFilter.setSelectedItem(curCat);

                    // Update spotlight (top 2 soonest)
                    spotlightPanel.removeAll();
                    if (results.isEmpty()) {
                        spotlightPanel.add(emptyState("No spotlight opportunities right now."));
                    } else {
                        int count = Math.min(2, results.size());
                        for (int i = 0; i < count; i++) {
                            Event e = results.get(i);
                            spotlightPanel.add(buildSpotlightCard(e));
                        }
                    }

                    // Update main grid
                    opportunitiesGrid.removeAll();
                    if (results.isEmpty()) {
                        opportunitiesGrid.add(emptyState("No opportunities match your filter criteria."));
                    } else {
                        for (Event e : results) {
                            opportunitiesGrid.add(buildDirectoryCard(e));
                        }
                    }

                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    System.err.println("[BeyondTheClassroom] Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void refresh() {
        applyFilters();
    }

    private RoundedPanel buildSpotlightCard(Event ev) {
        RoundedPanel card = new RoundedPanel(UIConstants.WHITE);
        card.setLayout(new BorderLayout(12, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.YELLOW, 2, true),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        top.setOpaque(false);
        top.add(makeBadge(ev.getOpportunityType(), UIConstants.YELLOW, UIConstants.NAVY));
        top.add(makeBadge(ev.getCategory(), UIConstants.PALE_BG, UIConstants.TEXT_DARK));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel title = new JLabel(ev.getTitle());
        title.setFont(UIConstants.FONT_BODY_BOLD);
        title.setForeground(UIConstants.NAVY);

        JLabel meta = new JLabel("📅 " + DateUtil.formatDate(ev.getEventDate()) + "  ·  📍 " + ev.getVenue());
        meta.setFont(UIConstants.FONT_SMALL);
        meta.setForeground(UIConstants.TEXT_MUTED);

        JLabel dl = new JLabel("⏰ Deadline: " + (ev.getRegDeadline() != null ? DateUtil.formatDateTime(ev.getRegDeadline()) : "Open"));
        dl.setFont(UIConstants.FONT_SMALL_BOLD);
        dl.setForeground(UIConstants.NAVY_HOVER);

        center.add(title);
        center.add(Box.createVerticalStrut(4));
        center.add(meta);
        center.add(Box.createVerticalStrut(2));
        center.add(dl);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);

        StyledButton detailsBtn = new StyledButton("Details", StyledButton.Style.SECONDARY);
        detailsBtn.addActionListener(e -> studentFrame.openEventDetail(ev));

        StyledButton regBtn = new StyledButton("Register", StyledButton.Style.YELLOW);
        regBtn.addActionListener(e -> {
            try {
                String res = regService.registerForEvent(studentFrame.getStudent().getId(), ev.getId());
                JOptionPane.showMessageDialog(this, res, "Registration Status", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        actions.add(detailsBtn);
        actions.add(regBtn);

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private RoundedPanel buildDirectoryCard(Event ev) {
        RoundedPanel card = new RoundedPanel(UIConstants.WHITE);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Top badges & capacity
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badges.setOpaque(false);
        badges.add(makeBadge(ev.getOpportunityType(), UIConstants.SKY_BLUE, UIConstants.NAVY));
        badges.add(makeBadge(ev.getCategory(), UIConstants.PALE_BG, UIConstants.TEXT_DARK));

        JLabel seats = new JLabel(ev.getAvailableSeats() + " seats left");
        seats.setFont(UIConstants.FONT_SMALL_BOLD);
        seats.setForeground(ev.getAvailableSeats() > 0 ? UIConstants.GREEN : UIConstants.RED);

        top.add(badges, BorderLayout.WEST);
        top.add(seats, BorderLayout.EAST);

        // Center content
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel title = new JLabel(ev.getTitle());
        title.setFont(UIConstants.FONT_BODY_BOLD);
        title.setForeground(UIConstants.NAVY);

        JLabel organizer = new JLabel("Organized by " + ev.getOrganizer());
        organizer.setFont(UIConstants.FONT_SMALL);
        organizer.setForeground(UIConstants.TEXT_MUTED);

        JLabel venue = new JLabel("📍 " + ev.getVenue() + "  |  📅 " + DateUtil.formatDate(ev.getEventDate()));
        venue.setFont(UIConstants.FONT_SMALL);
        venue.setForeground(UIConstants.TEXT_DARK);

        JLabel elig = new JLabel("🎓 Eligibility: " + (ev.getEligibility() != null ? ev.getEligibility() : "All Students"));
        elig.setFont(UIConstants.FONT_SMALL);
        elig.setForeground(UIConstants.TEXT_MUTED);

        center.add(title);
        center.add(Box.createVerticalStrut(3));
        center.add(organizer);
        center.add(Box.createVerticalStrut(4));
        center.add(venue);
        center.add(Box.createVerticalStrut(2));
        center.add(elig);

        // Bottom actions
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel dl = new JLabel("⏰ " + (ev.getRegDeadline() != null ? DateUtil.formatDate(ev.getRegDeadline().toLocalDate()) : "Open"));
        dl.setFont(UIConstants.FONT_SMALL);
        dl.setForeground(UIConstants.TEXT_MUTED);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setOpaque(false);

        StyledButton viewBtn = new StyledButton("View Details", StyledButton.Style.GHOST);
        viewBtn.addActionListener(e -> studentFrame.openEventDetail(ev));

        StyledButton regBtn = new StyledButton("Register", StyledButton.Style.YELLOW);
        regBtn.setEnabled(ev.isRegistrationOpen() && ev.getAvailableSeats() > 0);
        regBtn.addActionListener(e -> {
            try {
                String res = regService.registerForEvent(studentFrame.getStudent().getId(), ev.getId());
                JOptionPane.showMessageDialog(this, res, "Registration", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRow.add(viewBtn);
        btnRow.add(regBtn);

        bottom.add(dl, BorderLayout.WEST);
        bottom.add(btnRow, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private JLabel makeBadge(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text != null ? text : "General");
        l.setFont(UIConstants.FONT_SMALL_BOLD);
        l.setBackground(bg);
        l.setForeground(fg);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return l;
    }

    private JLabel emptyState(String msg) {
        JLabel l = new JLabel(msg);
        l.setFont(UIConstants.FONT_BODY);
        l.setForeground(UIConstants.TEXT_MUTED);
        return l;
    }
}
