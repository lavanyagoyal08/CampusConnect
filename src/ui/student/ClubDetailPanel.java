package ui.student;

import model.Club;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

public class ClubDetailPanel extends JPanel {

    public ClubDetailPanel(Club club) {
        setLayout(new BorderLayout());
        setBackground(UIConstants.WHITE);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(UIConstants.WHITE);
        inner.setBorder(BorderFactory.createEmptyBorder(28,32,28,32));

        JLabel cat = new JLabel(club.getCategory()!=null?club.getCategory():"");
        cat.setFont(UIConstants.FONT_SMALL_BOLD); cat.setForeground(UIConstants.NAVY);
        cat.setBackground(UIConstants.SKY_BLUE); cat.setOpaque(true);
        cat.setBorder(BorderFactory.createEmptyBorder(3,10,3,10)); cat.setAlignmentX(LEFT_ALIGNMENT);

        JLabel name = new JLabel("<html><b>" + safe(club.getName()) + "</b></html>");
        name.setFont(new Font("Segoe UI",Font.BOLD,22)); name.setForeground(UIConstants.NAVY); name.setAlignmentX(LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator(); sep.setForeground(UIConstants.CARD_BORDER); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));

        JTextArea desc = new JTextArea(club.getDescription()!=null?club.getDescription():"No description.");
        desc.setFont(UIConstants.FONT_BODY); desc.setForeground(UIConstants.TEXT_DARK); desc.setBackground(UIConstants.WHITE);
        desc.setWrapStyleWord(true); desc.setLineWrap(true); desc.setEditable(false); desc.setFocusable(false);
        desc.setBorder(null); desc.setAlignmentX(LEFT_ALIGNMENT);

        JLabel email = row("Email: ", club.getContactEmail());
        JLabel info  = row("Meeting: ", club.getContactInfo());
        JLabel status = new JLabel(club.isActive()?"Active":"Inactive");
        status.setFont(UIConstants.FONT_SMALL_BOLD);
        status.setForeground(club.isActive()?UIConstants.GREEN:UIConstants.RED);
        status.setAlignmentX(LEFT_ALIGNMENT);

        inner.add(cat); inner.add(Box.createVerticalStrut(10));
        inner.add(name); inner.add(Box.createVerticalStrut(10));
        inner.add(sep); inner.add(Box.createVerticalStrut(14));
        inner.add(desc); inner.add(Box.createVerticalStrut(18));
        inner.add(email); inner.add(Box.createVerticalStrut(4));
        inner.add(info); inner.add(Box.createVerticalStrut(14));
        inner.add(status);

        add(new JScrollPane(inner), BorderLayout.CENTER);
    }

    private JLabel row(String label, String value) {
        JLabel l = new JLabel(label + (value==null||value.isEmpty()?"Not specified":value));
        l.setFont(UIConstants.FONT_BODY); l.setForeground(UIConstants.TEXT_DARK); l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
    private String safe(String s) { return s==null?"":s.replace("&","&amp;").replace("<","&lt;"); }
}
