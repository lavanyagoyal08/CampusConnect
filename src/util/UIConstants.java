package util;

import java.awt.*;

/**
 * UIConstants — Central visual theme for CampusConnect.
 * All colours, fonts, and layout constants are defined here.
 */
public class UIConstants {

    // ── Colour Palette ─────────────────────────────────────────────────────────
    public static final Color NAVY         = new Color(0x10, 0x1F, 0x3C);
    public static final Color NAVY_HOVER   = new Color(0x1A, 0x33, 0x60);
    public static final Color SKY_BLUE     = new Color(0xA9, 0xD9, 0xFF);
    public static final Color YELLOW       = new Color(0xF8, 0xCC, 0x4D);
    public static final Color WHITE        = Color.WHITE;
    public static final Color PALE_BG      = new Color(0xF3, 0xF6, 0xFA);

    // Text
    public static final Color TEXT_DARK    = new Color(0x1A, 0x1A, 0x2E);
    public static final Color TEXT_MUTED   = new Color(0x6B, 0x7A, 0x99);

    // Semantic
    public static final Color GREEN        = new Color(0x1A, 0x8A, 0x45);
    public static final Color RED          = new Color(0xD9, 0x3A, 0x3A);

    // Card
    public static final Color CARD_BORDER  = new Color(0xE0, 0xE7, 0xF0);

    // Category tag backgrounds / foregrounds
    public static final Color TAG_TECH        = new Color(0xDB, 0xEA, 0xFF);
    public static final Color TAG_TECH_FG     = new Color(0x1A, 0x4F, 0xB5);
    public static final Color TAG_CULTURAL    = new Color(0xFD, 0xE8, 0xD8);
    public static final Color TAG_CULTURAL_FG = new Color(0xB0, 0x55, 0x10);
    public static final Color TAG_SOCIAL      = new Color(0xD6, 0xF5, 0xE3);
    public static final Color TAG_SOCIAL_FG   = new Color(0x12, 0x7A, 0x44);
    public static final Color TAG_DESIGN      = new Color(0xF3, 0xDA, 0xFF);
    public static final Color TAG_DESIGN_FG   = new Color(0x76, 0x1E, 0xB5);

    // ── Fonts ──────────────────────────────────────────────────────────────────
    public static final Font FONT_LARGE_BOLD   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_BRAND        = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_HEADING      = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_SUBHEADING   = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_BODY_BOLD    = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BODY         = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_NAV          = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL_BOLD   = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FONT_SMALL        = new Font("Segoe UI", Font.PLAIN, 12);

    // ── Layout ────────────────────────────────────────────────────────────────
    public static final int WINDOW_WIDTH   = 1280;
    public static final int WINDOW_HEIGHT  = 800;
    public static final int SIDEBAR_WIDTH  = 210;
    public static final int TOPBAR_HEIGHT  = 60;
    public static final int CARD_RADIUS    = 12;
    public static final int BTN_RADIUS     = 8;
    public static final int PADDING_LG     = 24;
    public static final int PADDING_MD     = 16;

    private UIConstants() {}   // prevent instantiation
}
