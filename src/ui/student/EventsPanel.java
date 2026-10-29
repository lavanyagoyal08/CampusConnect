package ui.student;

import dao.EventDAO;
import model.Event;
import ui.components.EventCard;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EventsPanel extends JPanel {

    private final StudentFrame studentFrame;
    private final EventDAO eventDAO = new EventDAO();
    private JTextField searchField;
    private JComboBox<String> categoryFilter, typeFilter;
    private JPanel cardsPanel;

    public EventsPanel(StudentFrame sf) {
        this.studentFrame = sf;
        setLayout(new BorderLayout());
        setBackground(UIConstants.PALE_BG);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UIConstants.PALE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(20,20,10,20));

        JLabel title = new JLabel("Events & Opportunities");
        title.setFont(UIConstants.FONT_HEADING); title.setForeground(UIConstants.NAVY); title.setAlignmentX(LEFT_ALIGNMENT);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filters.setOpaque(false); filters.setAlignmentX(LEFT_ALIGNMENT);

        searchField = new JTextField(18); searchField.setFont(UIConstants.FONT_BODY);
        searchField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UIConstants.CARD_BORDER,1,true),BorderFactory.createEmptyBorder(6,10,6,10)));
        searchField.addActionListener(e -> applyFilters());

        categoryFilter = new JComboBox<>(new String[]{"All"}); categoryFilter.setFont(UIConstants.FONT_BODY); categoryFilter.addActionListener(e->applyFilters());
        typeFilter = new JComboBox<>(new String[]{"All"}); typeFilter.setFont(UIConstants.FONT_BODY); typeFilter.addActionListener(e->applyFilters());
        JButton sb = new JButton("Search"); sb.setFont(UIConstants.FONT_SMALL_BOLD); sb.addActionListener(e->applyFilters());
        filters.add(searchField); filters.add(new JLabel("Category:")); filters.add(categoryFilter);
        filters.add(new JLabel("Type:")); filters.add(typeFilter); filters.add(sb);
        header.add(title); header.add(Box.createVerticalStrut(8)); header.add(filters);

        cardsPanel = new JPanel(new GridLayout(0,2,14,14)); cardsPanel.setOpaque(false);
        JPanel wrap = new JPanel(new BorderLayout()); wrap.setBackground(UIConstants.PALE_BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(0,20,20,20)); wrap.add(cardsPanel, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(wrap); scroll.setBorder(null); scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(header, BorderLayout.NORTH); add(scroll, BorderLayout.CENTER);
        loadEvents("","All","All");
    }

    public void search(String kw) { searchField.setText(kw); applyFilters(); }
    private void applyFilters() { loadEvents(searchField.getText().trim(),(String)categoryFilter.getSelectedItem(),(String)typeFilter.getSelectedItem()); }

    private void loadEvents(String kw, String cat, String type) {
        SwingWorker<List<Event>,Void> w = new SwingWorker<>() {
            List<String> cats, types;
            @Override protected List<Event> doInBackground() throws Exception {
                cats = eventDAO.findAllCategories(); types = eventDAO.findAllOpportunityTypes();
                return eventDAO.search(kw, cat, type);
            }
            @Override protected void done() {
                try {
                    List<Event> events = get();
                    String sc = (String) categoryFilter.getSelectedItem(), st = (String) typeFilter.getSelectedItem();
                    categoryFilter.removeAllItems(); categoryFilter.addItem("All"); cats.forEach(categoryFilter::addItem);
                    typeFilter.removeAllItems(); typeFilter.addItem("All"); types.forEach(typeFilter::addItem);
                    if (sc!=null) categoryFilter.setSelectedItem(sc); if (st!=null) typeFilter.setSelectedItem(st);
                    cardsPanel.removeAll();
                    if (events.isEmpty()) { JLabel e = new JLabel("No events found."); e.setFont(UIConstants.FONT_BODY); e.setForeground(UIConstants.TEXT_MUTED); cardsPanel.add(e); }
                    else for (Event ev : events) { EventCard c = new EventCard(ev); c.setOnClick(()->studentFrame.openEventDetail(ev)); cardsPanel.add(c); }
                    cardsPanel.revalidate(); cardsPanel.repaint();
                } catch (Exception ex) { System.err.println("[Events] " + ex.getMessage()); }
            }
        };
        w.execute();
    }
}
