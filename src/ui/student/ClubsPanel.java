package ui.student;

import dao.ClubDAO;
import model.Club;
import ui.components.ClubCard;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ClubsPanel extends JPanel {

    private final StudentFrame studentFrame;
    private final ClubDAO clubDAO = new ClubDAO();
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JPanel cardsPanel;

    public ClubsPanel(StudentFrame sf) {
        this.studentFrame = sf;
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);

        JPanel header = new JPanel(new BorderLayout(12,0));
        header.setBackground(UIConstants.PALE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(20,20,10,20));
        JLabel title = new JLabel("Discover Clubs"); title.setFont(UIConstants.FONT_HEADING); title.setForeground(UIConstants.NAVY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); controls.setOpaque(false);
        searchField = new JTextField(16); searchField.setFont(UIConstants.FONT_BODY);
        searchField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UIConstants.CARD_BORDER,1,true),BorderFactory.createEmptyBorder(6,10,6,10)));
        searchField.addActionListener(e -> applyFilters());
        categoryFilter = new JComboBox<>(new String[]{"All"}); categoryFilter.setFont(UIConstants.FONT_BODY);
        categoryFilter.addActionListener(e -> applyFilters());
        JButton sb = new JButton("Search"); sb.setFont(UIConstants.FONT_SMALL_BOLD); sb.addActionListener(e -> applyFilters());
        controls.add(new JLabel("Category:")); controls.add(categoryFilter); controls.add(searchField); controls.add(sb);
        header.add(title, BorderLayout.WEST); header.add(controls, BorderLayout.EAST);

        cardsPanel = new JPanel(new GridLayout(0,2,16,16)); cardsPanel.setOpaque(false);
        JPanel wrap = new JPanel(new BorderLayout()); wrap.setBackground(UIConstants.PALE_BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(0,20,20,20)); wrap.add(cardsPanel, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(wrap); scroll.setBorder(null); scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(header, BorderLayout.NORTH); add(scroll, BorderLayout.CENTER);
        loadClubs("","All");
    }

    private void applyFilters() { loadClubs(searchField.getText().trim(), (String)categoryFilter.getSelectedItem()); }

    private void loadClubs(String kw, String cat) {
        SwingWorker<List<Club>,Void> w = new SwingWorker<>() {
            List<String> cats;
            @Override protected List<Club> doInBackground() throws Exception {
                cats = clubDAO.findAllCategories();
                if (kw!=null&&!kw.isEmpty()) return clubDAO.searchByName(kw);
                if (!"All".equals(cat)) return clubDAO.findByCategory(cat);
                return clubDAO.findAll();
            }
            @Override protected void done() {
                try {
                    List<Club> clubs = get();
                    String sel = (String) categoryFilter.getSelectedItem();
                    categoryFilter.removeAllItems(); categoryFilter.addItem("All"); cats.forEach(categoryFilter::addItem);
                    if (sel!=null) categoryFilter.setSelectedItem(sel);
                    cardsPanel.removeAll();
                    if (clubs.isEmpty()) { JLabel e = new JLabel("No clubs found."); e.setFont(UIConstants.FONT_BODY); e.setForeground(UIConstants.TEXT_MUTED); cardsPanel.add(e); }
                    else for (Club c : clubs) { ClubCard card = new ClubCard(c); card.setOnClick(()->studentFrame.openClubDetail(c)); cardsPanel.add(card); }
                    cardsPanel.revalidate(); cardsPanel.repaint();
                } catch (Exception ex) { System.err.println("[Clubs] " + ex.getMessage()); }
            }
        };
        w.execute();
    }
}
