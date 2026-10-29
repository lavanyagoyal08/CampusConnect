package dao;

import model.Event;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SavedEventDAO — Manages saved (bookmarked) events per student.
 */
public class SavedEventDAO {

    public boolean saveEvent(int studentId, int eventId) throws SQLException {
        // Use INSERT IGNORE to avoid duplicate
        String sql = "INSERT IGNORE INTO saved_events (student_id, event_id) VALUES (?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, eventId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean unsaveEvent(int studentId, int eventId) throws SQLException {
        String sql = "DELETE FROM saved_events WHERE student_id=? AND event_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, eventId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean isEventSaved(int studentId, int eventId) throws SQLException {
        String sql = "SELECT 1 FROM saved_events WHERE student_id=? AND event_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, eventId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /** Returns full Event objects for all saved events of a student. */
    public List<Event> findSavedEventsByStudent(int studentId) throws SQLException {
        String sql = "SELECT e.* FROM events e " +
                     "JOIN saved_events se ON e.id = se.event_id " +
                     "WHERE se.student_id = ? ORDER BY e.event_date";
        List<Event> events = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) events.add(mapEvent(rs));
            }
        }
        return events;
    }

    private Event mapEvent(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setId(rs.getInt("id"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setCategory(rs.getString("category"));
        e.setOpportunityType(rs.getString("opportunity_type"));
        java.sql.Date d = rs.getDate("event_date");
        if (d != null) e.setEventDate(d.toLocalDate());
        e.setVenue(rs.getString("venue"));
        e.setOrganizer(rs.getString("organizer"));
        e.setCapacity(rs.getInt("capacity"));
        e.setAvailableSeats(rs.getInt("available_seats"));
        e.setRegistrationOpen(rs.getBoolean("registration_open"));
        e.setActive(rs.getBoolean("active"));
        return e;
    }
}
