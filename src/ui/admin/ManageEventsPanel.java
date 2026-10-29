package ui.admin;

import dao.EventDAO;
import model.Event;
import util.DateUtil;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * ManageEventsPanel — Admin CRUD for events table.
 */
public class ManageEventsPanel extends JPanel {

    private final AdminFrame adminFrame;
    private final EventDAO   eventDAO = new EventDAO();

    private JTable              table;
    private DefaultTableModel   tableModel;
    private List<Event>         currentEvents;

    private static final String[] COLUMNS = {
        "ID", "Title", "Category", "Type", "Date", "Venue", "Seats", "Open", "Active"
    };

    public ManageEventsPanel(AdminFrame adminFrame) {
        this.adminFrame = adminFrame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);
        build();
        loadData();
    }

    private void build() {
        // ── Header ─────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.PALE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel title = new JLabel("Manage Events 📅");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.NAVY);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        JButton addBtn    = btn("+ Add Event");
        JButton editBtn   = btn("Edit");
        JButton deleteBtn = btn("Deactivate");
        JButton refreshBtn = btn("↻ Refresh");

        addBtn.addActionListener(e    -> showEventDialog(null));
        editBtn.addActionListener(e   -> editSelected());
        deleteBtn.addActionListener(e -> deactivateSelected());
        refreshBtn.addActionListener(e -> loadData());

        btnRow.add(addBtn); btnRow.add(editBtn); btnRow.add(deleteBtn); btnRow.add(refreshBtn);
        header.add(title,  BorderLayout.WEST);
        header.add(btnRow, BorderLayout.EAST);

        // ── Table ──────────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(UIConstants.FONT_BODY);
        table.getTableHeader().setFont(UIConstants.FONT_SMALL_BOLD);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        SwingWorker<List<Event>, Void> worker = new SwingWorker<>() {
            @Override protected List<Event> doInBackground() throws Exception {
                return eventDAO.findAll();
            }
            @Override protected void done() {
                try {
                    currentEvents = get();
                    tableModel.setRowCount(0);
                    for (Event ev : currentEvents) {
                        tableModel.addRow(new Object[]{
                            ev.getId(), ev.getTitle(), ev.getCategory(),
                            ev.getOpportunityType(), DateUtil.formatDate(ev.getEventDate()),
                            ev.getVenue(), ev.getAvailableSeats() + "/" + ev.getCapacity(),
                            ev.isRegistrationOpen() ? "Yes" : "No",
                            ev.isActive() ? "Yes" : "No"
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ManageEventsPanel.this,
                        "Load error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an event first."); return; }
        showEventDialog(currentEvents.get(row));
    }

    private void deactivateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an event first."); return; }
        Event ev = currentEvents.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deactivate \"" + ev.getTitle() + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            eventDAO.delete(ev.getId());
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showEventDialog(Event existing) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
            existing == null ? "Add Event" : "Edit Event",
            Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setContentPane(new EventFormPanel(existing, saved -> {
            try {
                if (existing == null) eventDAO.insert(saved);
                else { saved.setId(existing.getId()); eventDAO.update(saved); }
                loadData();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Save error: " + ex.getMessage());
            }
        }));
        dialog.setVisible(true);
    }

    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.setFont(UIConstants.FONT_SMALL_BOLD);
        return b;
    }

    // ── Inline event form ──────────────────────────────────────────────────────

    private static class EventFormPanel extends JPanel {
        private final JTextField      titleField, venueField, organizerField, eligibilityField, capacityField;
        private final JComboBox<String> categoryCombo, typeCombo;
        private final JTextField      dateField;
        private final JCheckBox       openCheck;
        private final JTextArea       descArea;
        private final java.util.function.Consumer<Event> onSave;

        EventFormPanel(Event ev, java.util.function.Consumer<Event> onSave) {
            this.onSave = onSave;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

            String[] categories = {"Technology","Cultural","Sports","Social Impact","Entrepreneurship","Design"};
            String[] types      = {"Workshop","Hackathon","Competition","Volunteering","Cultural","Skill-Building","Seminar","Other"};

            titleField       = field(ev != null ? ev.getTitle() : "");
            categoryCombo    = new JComboBox<>(categories);
            typeCombo        = new JComboBox<>(types);
            dateField        = field(ev != null && ev.getEventDate() != null ? ev.getEventDate().toString() : LocalDate.now().toString());
            venueField       = field(ev != null ? ev.getVenue() : "");
            organizerField   = field(ev != null ? ev.getOrganizer() : "");
            eligibilityField = field(ev != null ? ev.getEligibility() : "");
            capacityField    = field(ev != null ? String.valueOf(ev.getCapacity()) : "50");
            openCheck        = new JCheckBox("Registration Open", ev == null || ev.isRegistrationOpen());
            descArea         = new JTextArea(ev != null ? ev.getDescription() : "", 4, 30);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);

            if (ev != null) {
                categoryCombo.setSelectedItem(ev.getCategory());
                typeCombo.setSelectedItem(ev.getOpportunityType());
            }

            addRow("Title", titleField);
            addRow("Category", categoryCombo);
            addRow("Type", typeCombo);
            addRow("Date (YYYY-MM-DD)", dateField);
            addRow("Venue", venueField);
            addRow("Organizer", organizerField);
            addRow("Eligibility", eligibilityField);
            addRow("Capacity", capacityField);
            add(openCheck);
            add(Box.createVerticalStrut(6));
            addRow("Description", new JScrollPane(descArea));
            add(Box.createVerticalStrut(12));

            JButton saveBtn = new JButton("Save Event");
            saveBtn.setFont(UIConstants.FONT_SMALL_BOLD);
            saveBtn.addActionListener(e -> save());
            add(saveBtn);
        }

        private void save() {
            try {
                Event ev = new Event();
                ev.setTitle(titleField.getText().trim());
                ev.setCategory((String) categoryCombo.getSelectedItem());
                ev.setOpportunityType((String) typeCombo.getSelectedItem());
                ev.setEventDate(LocalDate.parse(dateField.getText().trim()));
                ev.setVenue(venueField.getText().trim());
                ev.setOrganizer(organizerField.getText().trim());
                ev.setEligibility(eligibilityField.getText().trim());
                ev.setCapacity(Integer.parseInt(capacityField.getText().trim()));
                ev.setAvailableSeats(ev.getCapacity());
                ev.setRegistrationOpen(openCheck.isSelected());
                ev.setActive(true);
                ev.setDescription(descArea.getText().trim());
                onSave.accept(ev);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        }

        private JTextField field(String text) {
            JTextField f = new JTextField(text);
            f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            return f;
        }

        private void addRow(String label, JComponent comp) {
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setAlignmentX(LEFT_ALIGNMENT);
            comp.setAlignmentX(LEFT_ALIGNMENT);
            comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            add(lbl);
            add(Box.createVerticalStrut(3));
            add(comp);
            add(Box.createVerticalStrut(10));
        }
    }
}
