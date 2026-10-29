package ui.admin;

import dao.EventDAO;
import dao.RegistrationDAO;
import model.Event;
import model.Registration;
import util.DateUtil;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * AttendancePanel — Admin marks student attendance for events.
 */
public class AttendancePanel extends JPanel {

    private final AdminFrame      adminFrame;
    private final EventDAO        eventDAO = new EventDAO();
    private final RegistrationDAO regDAO   = new RegistrationDAO();

    private JComboBox<EventItem>  eventCombo;
    private JTable                table;
    private DefaultTableModel     tableModel;
    private List<Registration>    currentRegs;

    private static final String[] COLUMNS = {
        "Reg ID", "Student Name", "Student ID", "Registered At", "Attendance"
    };

    public AttendancePanel(AdminFrame adminFrame) {
        this.adminFrame = adminFrame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);
        build();
        loadEvents();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.PALE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel title = new JLabel("Attendance Verification ✅");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.NAVY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        eventCombo = new JComboBox<>();
        eventCombo.setFont(UIConstants.FONT_BODY);
        eventCombo.setPreferredSize(new Dimension(280, 32));
        eventCombo.addActionListener(e -> loadRegistrations());

        JButton markAttended    = btn("✅ Mark Attended");
        JButton markNotAttended = btn("❌ Mark Not Attended");
        JButton refreshBtn      = btn("↻ Refresh");

        markAttended.addActionListener(e    -> markAttendance("ATTENDED"));
        markNotAttended.addActionListener(e -> markAttendance("NOT_ATTENDED"));
        refreshBtn.addActionListener(e      -> loadRegistrations());

        controls.add(new JLabel("Event:"));
        controls.add(eventCombo);
        controls.add(markAttended);
        controls.add(markNotAttended);
        controls.add(refreshBtn);

        header.add(title,    BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(UIConstants.FONT_BODY);
        table.getTableHeader().setFont(UIConstants.FONT_SMALL_BOLD);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void loadEvents() {
        SwingWorker<List<Event>, Void> worker = new SwingWorker<>() {
            @Override protected List<Event> doInBackground() throws Exception { return eventDAO.findAll(); }
            @Override protected void done() {
                try {
                    eventCombo.removeAllItems();
                    for (Event ev : get()) eventCombo.addItem(new EventItem(ev));
                    loadRegistrations();
                } catch (Exception ex) {
                    System.err.println("[AttendancePanel] Load events error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void loadRegistrations() {
        EventItem selected = (EventItem) eventCombo.getSelectedItem();
        if (selected == null) return;
        int eventId = selected.event.getId();

        SwingWorker<List<Registration>, Void> worker = new SwingWorker<>() {
            @Override protected List<Registration> doInBackground() throws Exception {
                return regDAO.findByEvent(eventId);
            }
            @Override protected void done() {
                try {
                    currentRegs = get();
                    tableModel.setRowCount(0);
                    for (Registration r : currentRegs) {
                        tableModel.addRow(new Object[]{
                            r.getId(),
                            r.getStudentName() != null ? r.getStudentName() : ("Student #" + r.getStudentId()),
                            r.getStudentId(),
                            r.getRegisteredAt() != null ? DateUtil.formatDateTime(r.getRegisteredAt()) : "—",
                            r.getAttendanceStatus()
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("[AttendancePanel] Load regs error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void markAttendance(String status) {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) { JOptionPane.showMessageDialog(this, "Select one or more students first."); return; }
        try {
            for (int row : rows) {
                int regId = (int) tableModel.getValueAt(row, 0);
                String adminName = adminFrame.getAdmin() != null ? adminFrame.getAdmin().getName() : "Admin";
                regDAO.updateAttendance(regId, status, adminName);
            }
            loadRegistrations();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.setFont(UIConstants.FONT_SMALL_BOLD);
        return b;
    }

    private static class EventItem {
        final Event event;
        EventItem(Event e) { this.event = e; }
        @Override public String toString() {
            return event.getTitle() + " (" + (event.getEventDate() != null ? event.getEventDate() : "no date") + ")";
        }
    }
}
