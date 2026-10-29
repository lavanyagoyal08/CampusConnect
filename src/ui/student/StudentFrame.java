package ui.student;

import dao.RegistrationDAO;
import model.Club;
import model.Event;
import model.Registration;
import model.Student;
import ui.MainFrame;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * StudentFrame — Main dashboard and portal shell for authenticated students.
 * Features a top navigation bar (Logo, global search, notification indicator, profile access)
 * and a left sidebar navigating to all core modules and unique features.
 */
public class StudentFrame extends JPanel {

    public static final String DASHBOARD        = "DASHBOARD";
    public static final String CLUBS            = "CLUBS";
    public static final String EVENTS           = "EVENTS";
    public static final String BEYOND_CLASSROOM = "BEYOND_CLASSROOM";
    public static final String PASSPORT         = "PASSPORT";
    public static final String MY_CAMPUS        = "MY_CAMPUS";
    public static final String PROFILE          = "PROFILE";

    private final Student    student;
    private final MainFrame  mainFrame;
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel     contentPanel  = new JPanel(contentLayout);
    private String           activeNav     = DASHBOARD;

    private DashboardPanel          dashboardPanel;
    private ClubsPanel              clubsPanel;
    private EventsPanel             eventsPanel;
    private BeyondTheClassroomPanel beyondClassroomPanel;
    private PassportPanel           passportPanel;
    private MyCampusPanel           myCampusPanel;
    private ProfilePanel            profilePanel;

    private JLabel notificationBadge;

    private final String[][] navItems = {
        {"🏠", "Dashboard",              DASHBOARD},
        {"🏛️", "Discover Clubs",         CLUBS},
        {"📅", "Events & Opportunities", EVENTS},
        {"🚀", "Beyond the Classroom",   BEYOND_CLASSROOM},
        {"🎒", "Campus Passport",        PASSPORT},
        {"🗂️", "My Campus",              MY_CAMPUS},
        {"👤", "My Profile",             PROFILE},
    };
    private final JLabel[] navLabels = new JLabel[navItems.length];

    public StudentFrame(Student student, MainFrame mainFrame) {
        this.student   = student;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        navigateTo(DASHBOARD);
        updateNotificationCount();
    }

    public void navigateTo(String panel) {
        activeNav = panel;
        contentLayout.show(contentPanel, panel);

        for (int i = 0; i < navItems.length; i++) {
            boolean active = navItems[i][2].equals(activeNav);
            navLabels[i].setForeground(active ? UIConstants.YELLOW : UIConstants.WHITE);
        }

        switch (panel) {
            case DASHBOARD        -> { if (dashboardPanel != null) dashboardPanel.refresh(); updateNotificationCount(); }
            case CLUBS            -> { /* Clubs loads on demand */ }
            case EVENTS           -> { /* Events loads on demand */ }
            case BEYOND_CLASSROOM -> { if (beyondClassroomPanel != null) beyondClassroomPanel.refresh(); }
            case PASSPORT         -> { if (passportPanel != null) passportPanel.refresh(); }
            case MY_CAMPUS        -> { if (myCampusPanel != null) myCampusPanel.refresh(); }
            case PROFILE          -> { if (profilePanel != null) profilePanel.refresh(); }
        }
    }

    public Student   getStudent()   { return student; }
    public MainFrame getMainFrame() { return mainFrame; }

    public void updateNotificationCount() {
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                List<Registration> regs = new RegistrationDAO().findByStudent(student.getId());
                int count = 0;
                for (Registration r : regs) {
                    if ("REGISTERED".equalsIgnoreCase(r.getStatus())) {
                        count++;
                    }
                }
                return count;
            }

            @Override
            protected void done() {
                try {
                    int c = get();
                    if (notificationBadge != null) {
                        notificationBadge.setText("🔔 " + c + " Active");
                        notificationBadge.setToolTipText(c + " active registered event(s)");
                    }
                } catch (Exception ignored) {}
            }
        };
        worker.execute();
    }

    // ── Top Navigation Bar ─────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIConstants.WHITE);
        bar.setPreferredSize(new Dimension(0, UIConstants.TOPBAR_HEIGHT));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.CARD_BORDER),
            BorderFactory.createEmptyBorder(0, UIConstants.PADDING_LG, 0, UIConstants.PADDING_LG)
        ));

        // Brand & Tagline
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel brand = new JLabel("🎓 CampusConnect");
        brand.setFont(UIConstants.FONT_BRAND);
        brand.setForeground(UIConstants.NAVY);

        JLabel tagline = new JLabel("“Your campus. Your people. Your possibilities.”");
        tagline.setFont(UIConstants.FONT_SMALL);
        tagline.setForeground(UIConstants.TEXT_MUTED);

        left.add(brand);
        left.add(tagline);

        // Global Search Field
        JTextField search = new JTextField();
        search.setFont(UIConstants.FONT_BODY);
        search.setToolTipText("Global search: type keyword and press Enter");
        search.setPreferredSize(new Dimension(280, 34));
        search.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        search.addActionListener(e -> {
            String kw = search.getText().trim();
            if (eventsPanel != null) {
                eventsPanel.search(kw);
                navigateTo(EVENTS);
            }
        });

        // Right items: Notifications, Profile access, Logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        notificationBadge = new JLabel("🔔 0 Active");
        notificationBadge.setFont(UIConstants.FONT_SMALL_BOLD);
        notificationBadge.setForeground(UIConstants.NAVY);
        notificationBadge.setBackground(UIConstants.SKY_BLUE);
        notificationBadge.setOpaque(true);
        notificationBadge.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        notificationBadge.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        notificationBadge.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { navigateTo(MY_CAMPUS); }
        });

        String firstName = student.getName() != null ? student.getName().split("\\s+")[0] : "Student";
        JLabel profileBtn = new JLabel("👤 " + firstName);
        profileBtn.setFont(UIConstants.FONT_BODY_BOLD);
        profileBtn.setForeground(UIConstants.NAVY);
        profileBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileBtn.setToolTipText("Open My Profile");
        profileBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { navigateTo(PROFILE); }
        });

        JLabel logout = new JLabel("Log Out");
        logout.setFont(UIConstants.FONT_SMALL_BOLD);
        logout.setForeground(UIConstants.RED);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    StudentFrame.this,
                    "Do you want to log out of CampusConnect?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    mainFrame.returnToLogin();
                }
            }
        });

        right.add(notificationBadge);
        right.add(profileBtn);
        right.add(new JSeparator(SwingConstants.VERTICAL));
        right.add(logout);

        bar.add(left,   BorderLayout.WEST);
        bar.add(search, BorderLayout.CENTER);
        bar.add(right,  BorderLayout.EAST);
        return bar;
    }

    // ── Left Sidebar Navigation ────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(UIConstants.NAVY);
        sb.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sb.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        for (int i = 0; i < navItems.length; i++) {
            String emoji = navItems[i][0];
            String label = navItems[i][1];
            String panel = navItems[i][2];

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 11));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 46));

            JLabel lbl = new JLabel(emoji + "  " + label);
            lbl.setFont(UIConstants.FONT_NAV);
            lbl.setForeground(UIConstants.WHITE);
            navLabels[i] = lbl;

            row.add(lbl);
            row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            row.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { navigateTo(panel); }
                @Override public void mouseEntered(MouseEvent e) { if (!activeNav.equals(panel)) row.setBackground(UIConstants.NAVY_HOVER); }
                @Override public void mouseExited(MouseEvent e)  { row.setOpaque(false); row.repaint(); }
            });
            sb.add(row);
        }
        sb.add(Box.createVerticalGlue());
        return sb;
    }

    // ── Content Area with CardLayout ───────────────────────────────────────────

    private JPanel buildContent() {
        contentPanel.setBackground(UIConstants.PALE_BG);

        dashboardPanel       = new DashboardPanel(this);
        clubsPanel           = new ClubsPanel(this);
        eventsPanel          = new EventsPanel(this);
        beyondClassroomPanel = new BeyondTheClassroomPanel(this);
        passportPanel        = new PassportPanel(this);
        myCampusPanel        = new MyCampusPanel(this);
        profilePanel         = new ProfilePanel(this);

        contentPanel.add(dashboardPanel,       DASHBOARD);
        contentPanel.add(clubsPanel,           CLUBS);
        contentPanel.add(eventsPanel,          EVENTS);
        contentPanel.add(beyondClassroomPanel, BEYOND_CLASSROOM);
        contentPanel.add(passportPanel,        PASSPORT);
        contentPanel.add(myCampusPanel,        MY_CAMPUS);
        contentPanel.add(profilePanel,         PROFILE);

        return contentPanel;
    }

    // ── Dialog Modals ──────────────────────────────────────────────────────────

    public void openEventDetail(Event event) {
        EventDetailPanel detail = new EventDetailPanel(event, this);
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), event.getTitle(), Dialog.ModalityType.APPLICATION_MODAL);
        d.setContentPane(detail);
        d.setSize(680, 580);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
        // Refresh active views after modal closes
        navigateTo(activeNav);
    }

    public void openClubDetail(Club club) {
        ClubDetailPanel detail = new ClubDetailPanel(club);
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), club.getName(), Dialog.ModalityType.APPLICATION_MODAL);
        d.setContentPane(detail);
        d.setSize(560, 440);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    public EventsPanel getEventsPanel() {
        return eventsPanel;
    }
}
