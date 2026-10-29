package ui.admin;

import dao.ClubDAO;
import model.Club;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ManageClubsPanel — Admin CRUD for clubs table.
 */
public class ManageClubsPanel extends JPanel {

    private final AdminFrame adminFrame;
    private final ClubDAO    clubDAO = new ClubDAO();

    private JTable            table;
    private DefaultTableModel tableModel;
    private List<Club>        currentClubs;

    private static final String[] COLUMNS = {"ID", "Name", "Category", "Contact Email", "Active"};

    public ManageClubsPanel(AdminFrame adminFrame) {
        this.adminFrame = adminFrame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);
        build();
        loadData();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.PALE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel title = new JLabel("Manage Clubs 🏛️");
        title.setFont(UIConstants.FONT_HEADING);
        title.setForeground(UIConstants.NAVY);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        JButton addBtn     = btn("+ Add Club");
        JButton editBtn    = btn("Edit");
        JButton deleteBtn  = btn("Deactivate");
        JButton refreshBtn = btn("↻ Refresh");

        addBtn.addActionListener(e    -> showClubDialog(null));
        editBtn.addActionListener(e   -> editSelected());
        deleteBtn.addActionListener(e -> deactivateSelected());
        refreshBtn.addActionListener(e -> loadData());

        btnRow.add(addBtn); btnRow.add(editBtn); btnRow.add(deleteBtn); btnRow.add(refreshBtn);
        header.add(title, BorderLayout.WEST);
        header.add(btnRow, BorderLayout.EAST);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(UIConstants.FONT_BODY);
        table.getTableHeader().setFont(UIConstants.FONT_SMALL_BOLD);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        SwingWorker<List<Club>, Void> worker = new SwingWorker<>() {
            @Override protected List<Club> doInBackground() throws Exception { return clubDAO.findAll(); }
            @Override protected void done() {
                try {
                    currentClubs = get();
                    tableModel.setRowCount(0);
                    for (Club c : currentClubs) {
                        tableModel.addRow(new Object[]{
                            c.getId(), c.getName(), c.getCategory(),
                            c.getContactEmail(), c.isActive() ? "Yes" : "No"
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ManageClubsPanel.this,
                        "Load error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a club first."); return; }
        showClubDialog(currentClubs.get(row));
    }

    private void deactivateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a club first."); return; }
        Club c = currentClubs.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deactivate \"" + c.getName() + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try { clubDAO.delete(c.getId()); loadData(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void showClubDialog(Club existing) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
            existing == null ? "Add Club" : "Edit Club", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(460, 420);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        String[] categories = {"Technology","Cultural","Sports","Social Impact","Entrepreneurship","Design"};

        JTextField nameField    = tf(existing != null ? existing.getName() : "");
        JComboBox<String> catBox = new JComboBox<>(categories);
        if (existing != null) catBox.setSelectedItem(existing.getCategory());
        JTextField emailField   = tf(existing != null ? existing.getContactEmail() : "");
        JTextField infoField    = tf(existing != null ? existing.getContactInfo() : "");
        JTextArea  descArea     = new JTextArea(existing != null ? existing.getDescription() : "", 3, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JCheckBox activeCheck   = new JCheckBox("Active", existing == null || existing.isActive());

        addRow(form, "Club Name",    nameField);
        addRow(form, "Category",     catBox);
        addRow(form, "Contact Email",emailField);
        addRow(form, "Meeting Info", infoField);
        addRow(form, "Description",  new JScrollPane(descArea));
        form.add(activeCheck);
        form.add(Box.createVerticalStrut(12));

        JButton saveBtn = new JButton("Save Club");
        saveBtn.setFont(UIConstants.FONT_SMALL_BOLD);
        saveBtn.addActionListener(e -> {
            try {
                Club c = existing != null ? existing : new Club();
                c.setName(nameField.getText().trim());
                c.setCategory((String) catBox.getSelectedItem());
                c.setContactEmail(emailField.getText().trim());
                c.setContactInfo(infoField.getText().trim());
                c.setDescription(descArea.getText().trim());
                c.setActive(activeCheck.isSelected());
                if (existing == null) clubDAO.insert(c);
                else clubDAO.update(c);
                loadData();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Save error: " + ex.getMessage());
            }
        });
        form.add(saveBtn);
        dialog.setContentPane(new JScrollPane(form));
        dialog.setVisible(true);
    }

    private JTextField tf(String text) {
        JTextField f = new JTextField(text);
        f.setFont(UIConstants.FONT_BODY);
        return f;
    }

    private void addRow(JPanel panel, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL_BOLD);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        comp.setAlignmentX(LEFT_ALIGNMENT);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(3));
        panel.add(comp);
        panel.add(Box.createVerticalStrut(10));
    }

    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.setFont(UIConstants.FONT_SMALL_BOLD);
        return b;
    }
}
