package app;

import service.AuthService;
import ui.MainFrame;
import util.DBConnection;

import javax.swing.*;
import java.awt.*;

/**
 * CampusConnectApp — Application entry point.
 * CampusConnect: "Your campus. Your people. Your possibilities."
 */
public class CampusConnectApp {

    public static void main(String[] args) {
        // Enable anti-aliasing and modern Swing text rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            // Check database connectivity in background and ensure default admin exists
            new Thread(() -> {
                try {
                    if (DBConnection.testConnection()) {
                        System.out.println("[CampusConnect] Connected to MySQL successfully.");
                        new AuthService().initializeDefaultAdminIfEmpty();
                    } else {
                        System.err.println("[CampusConnect] Notice: MySQL not reachable yet. Check db.properties or start MySQL service.");
                    }
                } catch (Exception e) {
                    System.err.println("[CampusConnect] DB init note: " + e.getMessage());
                }
            }).start();

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
