package ui.admin;

import model.Admin;
import ui.MainFrame;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * AdminFrame — Authenticated workspace shell for administrators.
 * Sidebar navigation + CardLayout content area.
 */
public class AdminFrame extends JPanel {

    public static final String DASHBOARD     = "DASHBOARD";
    public static final String MANAGE_EVENTS = "MANAGE_EVENTS";
    public static final String MANAGE_CLUBS  = "MANAGE_CLUBS";
    public static final String ATTENDANCE    = "ATTENDANCE";

    private final Admin      admin;
    private final MainFrame  mainFrame;
    private final CardLayout contentLayout = new CardLayout();
    private final JPanel     contentPanel  = new JPanel(contentLayout);
    private String           activeNav     = DASHBOARD;

    private AdminDashboardPanel dashboardPanel;
    private ManageEventsPanel   eventsPanel;
    private ManageClubsPanel    clubsPanel;
    private AttendancePanel     attendancePanel;

    private final String[][] navItems = {
        {"📊", "Dashboard",     DASHBOARD},
        {"📅", "Manage Events", MANAGE_EVENTS},
        {"🏛️", "Manage Clubs",  MANAGE_CLUBS},
        {"✅", "Attendance",    ATTENDANCE},
    };
    private final JLabel[] navLabels;

    public AdminFrame(Admin admin, MainFrame mainFrame) {
        this.admin     = admin;
        this.mainFrame = mainFrame;
        this.navLabels = new JLabel[navItems.length];

        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
        navigateTo(DASHBOARD);
    }

    public void navigateTo(String panel) {
        activeNav = panel;
        contentLayout.show(contentPanel, panel);
        refreshNavHighlight();
        if (DASHBOARD.equals(panel) && dashboardPanel != null) {
            dashboardPanel.loadStats();
        }
    }

    public Admin getAdmin() { return admin; }

    // ── Top bar ────────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIConstants.WHITE);
        bar.setPreferredSize(new Dimension(0, UIConstants.TOPBAR_HEIGHT));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.CARD_BORDER),
            BorderFactory.createEmptyBorder(0, UIConstants.PADDING_LG, 0, UIConstants.PADDING_LG)
        ));

        // Brand + Admin tag
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel brand = new JLabel("🎓 CampusConnect");
        brand.setFont(UIConstants.FONT_BRAND);
        brand.setForeground(UIConstants.NAVY);

        JLabel badge = new JLabel("ADMIN STUDIO");
        badge.setFont(UIConstants.FONT_SMALL_BOLD);
        badge.setForeground(UIConstants.NAVY);
        badge.setBackground(UIConstants.YELLOW);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        left.add(brand);
        left.add(badge);

        // Right side: admin name + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        JLabel adminName = new JLabel(admin != null ? admin.getName() : "Administrator");
        adminName.setFont(UIConstants.FONT_BODY_BOLD);
        adminName.setForeground(UIConstants.NAVY);

        JLabel roleLabel = new JLabel("[" + (admin != null ? admin.getAdminLevel() : "SUPER") + "]");
        roleLabel.setFont(UIConstants.FONT_SMALL);
        roleLabel.setForeground(UIConstants.TEXT_MUTED);

        JLabel logout = new JLabel("Log Out");
        logout.setFont(UIConstants.FONT_SMALL_BOLD);
        logout.setForeground(UIConstants.RED);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int res = JOptionPane.showConfirmDialog(
                    AdminFrame.this,
                    "Are you sure you want to log out of Admin Studio?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
                );
                if (res == JOptionPane.YES_OPTION) {
                    mainFrame.returnToLogin();
                }
            }
        });

        right.add(adminName);
        right.add(roleLabel);
        right.add(new JSeparator(SwingConstants.VERTICAL));
        right.add(logout);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar ────────────────────────────────────────────────────────────────

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

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 48));

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

    private void refreshNavHighlight() {
        for (int i = 0; i < navItems.length; i++) {
            boolean active = navItems[i][2].equals(activeNav);
            navLabels[i].setForeground(active ? UIConstants.YELLOW : UIConstants.WHITE);
        }
    }

    // ── Content ────────────────────────────────────────────────────────────────

    private JPanel buildContent() {
        contentPanel.setBackground(UIConstants.PALE_BG);
        dashboardPanel  = new AdminDashboardPanel(this);
        eventsPanel     = new ManageEventsPanel(this);
        clubsPanel      = new ManageClubsPanel(this);
        attendancePanel = new AttendancePanel(this);

        contentPanel.add(dashboardPanel,  DASHBOARD);
        contentPanel.add(eventsPanel,     MANAGE_EVENTS);
        contentPanel.add(clubsPanel,      MANAGE_CLUBS);
        contentPanel.add(attendancePanel, ATTENDANCE);
        return contentPanel;
    }
}
