package ui.components;

import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * StyledButton — Custom-painted rounded button with 5 style variants.
 */
public class StyledButton extends JButton {

    public enum Style { PRIMARY, SECONDARY, DANGER, GHOST, YELLOW }

    private final Style style;
    private Color bgColor, hoverColor, fgColor;
    private boolean hovered = false;

    public StyledButton(String text) { this(text, Style.PRIMARY); }

    public StyledButton(String text, Style style) {
        super(text);
        this.style = style;
        initColors();
        setFont(UIConstants.FONT_BODY_BOLD);
        setForeground(fgColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
        });
    }

    private void initColors() {
        switch (style) {
            case PRIMARY   -> { bgColor = UIConstants.NAVY;     hoverColor = UIConstants.NAVY_HOVER;                fgColor = UIConstants.WHITE; }
            case SECONDARY -> { bgColor = UIConstants.SKY_BLUE; hoverColor = new Color(0x85,0xC8,0xFF);            fgColor = UIConstants.NAVY;  }
            case DANGER    -> { bgColor = UIConstants.RED;      hoverColor = new Color(0xC5,0x2E,0x2E);            fgColor = UIConstants.WHITE; }
            case GHOST     -> { bgColor = UIConstants.WHITE;    hoverColor = UIConstants.PALE_BG;                  fgColor = UIConstants.NAVY;  }
            case YELLOW    -> { bgColor = UIConstants.YELLOW;   hoverColor = new Color(0xE8,0xBC,0x3D);            fgColor = UIConstants.NAVY;  }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg = (!isEnabled()) ? UIConstants.CARD_BORDER : (hovered ? hoverColor : bgColor);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.BTN_RADIUS * 2, UIConstants.BTN_RADIUS * 2);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setForeground(enabled ? fgColor : UIConstants.TEXT_MUTED);
        repaint();
    }
}
