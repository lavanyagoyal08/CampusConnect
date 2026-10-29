package ui.components;

import model.Club;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClubCard extends RoundedPanel {

    private final Club club;
    private Runnable onClick;

    public ClubCard(Club club) {
        this.club = club;
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        build();
    }

    private void build() {
        JLabel catBadge = new JLabel(club.getCategory() != null ? club.getCategory() : "");
        catBadge.setFont(UIConstants.FONT_SMALL_BOLD);
        catBadge.setForeground(UIConstants.NAVY);
        catBadge.setBackground(UIConstants.SKY_BLUE);
        catBadge.setOpaque(true);
        catBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        JLabel nameLabel = new JLabel("<html><b>" + htmlSafe(club.getName()) + "</b></html>");
        nameLabel.setFont(UIConstants.FONT_SUBHEADING);
        nameLabel.setForeground(UIConstants.NAVY);

        String desc = club.getDescription() == null ? "" : club.getDescription();
        if (desc.length() > 110) desc = desc.substring(0, 110) + "…";
        JLabel descLabel = new JLabel("<html><div style='width:220px'>" + htmlSafe(desc) + "</div></html>");
        descLabel.setFont(UIConstants.FONT_SMALL);
        descLabel.setForeground(UIConstants.TEXT_MUTED);

        JLabel contactLabel = new JLabel();
        if (club.getContactEmail() != null && !club.getContactEmail().isEmpty()) {
            contactLabel.setText("✉ " + club.getContactEmail());
            contactLabel.setFont(UIConstants.FONT_SMALL);
            contactLabel.setForeground(UIConstants.TEXT_MUTED);
        }

        JLabel viewDetails = new JLabel("View Details →");
        viewDetails.setFont(UIConstants.FONT_SMALL_BOLD);
        viewDetails.setForeground(UIConstants.NAVY);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        catBadge.setAlignmentX(LEFT_ALIGNMENT);
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);
        descLabel.setAlignmentX(LEFT_ALIGNMENT);
        contactLabel.setAlignmentX(LEFT_ALIGNMENT);
        viewDetails.setAlignmentX(LEFT_ALIGNMENT);
        content.add(catBadge);
        content.add(Box.createVerticalStrut(8));
        content.add(nameLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(descLabel);
        content.add(Box.createVerticalStrut(8));
        content.add(contactLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(viewDetails);

        add(content, BorderLayout.CENTER);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { setBorderColor(UIConstants.SKY_BLUE); repaint(); }
            @Override public void mouseExited(MouseEvent e)  { setBorderColor(UIConstants.CARD_BORDER); repaint(); }
            @Override public void mouseClicked(MouseEvent e) { if (onClick != null) onClick.run(); }
        });
    }

    public void setOnClick(Runnable r) { onClick = r; }
    public Club getClub()               { return club; }

    private String htmlSafe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;");
    }
}
