package ui.components;

import util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * RoundedPanel — JPanel with painted rounded-rectangle background, optional border + shadow.
 */
public class RoundedPanel extends JPanel {

    private int   radius      = UIConstants.CARD_RADIUS;
    private Color bgColor     = UIConstants.WHITE;
    private Color borderColor = UIConstants.CARD_BORDER;

    public RoundedPanel()                              { super(); setOpaque(false); }
    public RoundedPanel(Color bgColor)                 { this(); this.bgColor = bgColor; }
    public RoundedPanel(int r, Color bg, Color border) { this(); radius=r; bgColor=bg; borderColor=border; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // subtle shadow
        g2.setColor(new Color(0,0,0,10));
        g2.fillRoundRect(2, 3, getWidth()-2, getHeight()-2, radius*2, radius*2);
        // background
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius*2, radius*2);
        // border
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius*2, radius*2);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    public void setBgColor(Color c)     { bgColor     = c; repaint(); }
    public void setBorderColor(Color c) { borderColor = c; repaint(); }
    public void setRadius(int r)        { radius      = r; repaint(); }
}
