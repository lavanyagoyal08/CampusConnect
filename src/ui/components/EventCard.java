package ui.components;

import model.Event;
import util.DateUtil;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EventCard extends RoundedPanel {

    private final Event event;
    private Runnable onClick;

    public EventCard(Event event) {
        this.event = event;
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel typeTag = makeTag(event.getOpportunityType() != null ? event.getOpportunityType() : "Event");
        header.add(typeTag, BorderLayout.WEST);
        if (!event.isRegistrationOpen()) {
            JLabel closed = new JLabel("• Closed");
            closed.setFont(UIConstants.FONT_SMALL_BOLD);
            closed.setForeground(UIConstants.RED);
            header.add(closed, BorderLayout.EAST);
        }

        JLabel title = new JLabel("<html><b>" + htmlSafe(event.getTitle()) + "</b></html>");
        title.setFont(UIConstants.FONT_BODY_BOLD);
        title.setForeground(UIConstants.TEXT_DARK);

        JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        meta.setOpaque(false);
        meta.add(makeMetaLabel("📅 " + DateUtil.formatDate(event.getEventDate())));
        meta.add(makeMetaLabel("  📍 " + truncate(event.getVenue(), 28)));

        String seatText = event.isRegistrationOpen()
                ? event.getAvailableSeats() + " seats left" : "Registration closed";
        Color seatColor = (event.getAvailableSeats() <= 10 && event.isRegistrationOpen())
                ? UIConstants.RED : UIConstants.TEXT_MUTED;
        JLabel seats = new JLabel(seatText);
        seats.setFont(UIConstants.FONT_SMALL);
        seats.setForeground(seatColor);

        JLabel catTag = makeCategoryTag(event.getCategory() != null ? event.getCategory() : "");

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(catTag, BorderLayout.WEST);
        footer.add(seats,  BorderLayout.EAST);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(header);
        content.add(Box.createVerticalStrut(6));
        content.add(title);
        content.add(Box.createVerticalStrut(4));
        content.add(meta);
        content.add(Box.createVerticalStrut(6));
        content.add(footer);

        add(content, BorderLayout.CENTER);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { setBorderColor(UIConstants.SKY_BLUE); repaint(); }
            @Override public void mouseExited(MouseEvent e)  { setBorderColor(UIConstants.CARD_BORDER); repaint(); }
            @Override public void mouseClicked(MouseEvent e) { if (onClick != null) onClick.run(); }
        });
    }

    public void setOnClick(Runnable r) { onClick = r; }
    public Event getEvent()             { return event; }

    private JLabel makeTag(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIConstants.FONT_SMALL_BOLD);
        l.setForeground(UIConstants.NAVY);
        l.setBackground(UIConstants.SKY_BLUE);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        return l;
    }

    private JLabel makeCategoryTag(String category) {
        JLabel l = new JLabel(category);
        l.setFont(UIConstants.FONT_SMALL_BOLD);
        Color[] c = categoryColors(category);
        l.setForeground(c[1]);
        l.setBackground(c[0]);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        return l;
    }

    private Color[] categoryColors(String cat) {
        return switch (cat) {
            case "Technology"  -> new Color[]{UIConstants.TAG_TECH,     UIConstants.TAG_TECH_FG};
            case "Cultural"    -> new Color[]{UIConstants.TAG_CULTURAL, UIConstants.TAG_CULTURAL_FG};
            case "Social Impact" -> new Color[]{UIConstants.TAG_SOCIAL, UIConstants.TAG_SOCIAL_FG};
            case "Design"      -> new Color[]{UIConstants.TAG_DESIGN,   UIConstants.TAG_DESIGN_FG};
            default            -> new Color[]{UIConstants.PALE_BG,      UIConstants.TEXT_MUTED};
        };
    }

    private JLabel makeMetaLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIConstants.FONT_SMALL);
        l.setForeground(UIConstants.TEXT_MUTED);
        return l;
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

    private String htmlSafe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;");
    }
}
