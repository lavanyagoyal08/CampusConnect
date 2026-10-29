package dao;

import model.Event;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * EventDAO — Data access for the events table.
 */
public class EventDAO {

    // ── Read ──────────────────────────────────────────────────────────────────

    public List<Event> findAll() throws SQLException {
        String sql = "SELECT * FROM events WHERE active = 1 ORDER BY event_date";
        return query(sql);
    }

    public Event findById(int id) throws SQLException {
        String sql = "SELECT * FROM events WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Upcoming events for the dashboard widget (limit rows). */
    public List<Event> findUpcomingLimit(int limit) throws SQLException {
        String sql = "SELECT * FROM events WHERE active=1 AND event_date >= CURDATE() " +
                     "ORDER BY event_date LIMIT ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            return mapRows(ps.executeQuery());
        }
    }

    /** Keyword + category + type search used by EventsPanel. */
    public List<Event> search(String keyword, String category, String type) throws SQLException {
        StringBuilder sb = new StringBuilder(
            "SELECT * FROM events WHERE active = 1");
        if (keyword != null && !keyword.isBlank())
            sb.append(" AND (title LIKE ? OR description LIKE ? OR organizer LIKE ?)");
        if (category != null && !"All".equals(category))
            sb.append(" AND category = ?");
        if (type != null && !"All".equals(type))
            sb.append(" AND opportunity_type = ?");
        sb.append(" ORDER BY event_date");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sb.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            if (category != null && !"All".equals(category)) ps.setString(idx++, category);
            if (type     != null && !"All".equals(type))     ps.setString(idx,   type);
            return mapRows(ps.executeQuery());
        }
    }

    /** Returns events whose category is in the given list (for recommendations). */
    public List<Event> findByCategories(List<String> categories) throws SQLException {
        if (categories.isEmpty()) return new ArrayList<>();
        StringBuilder sb = new StringBuilder(
            "SELECT * FROM events WHERE active=1 AND registration_open=1 " +
            "AND event_date >= CURDATE() AND category IN (");
        for (int i = 0; i < categories.size(); i++) {
            sb.append(i > 0 ? ",?" : "?");
        }
        sb.append(") ORDER BY event_date");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sb.toString())) {
            for (int i = 0; i < categories.size(); i++) ps.setString(i + 1, categories.get(i));
            return mapRows(ps.executeQuery());
        }
    }

    public List<String> findAllCategories() throws SQLException {
        String sql = "SELECT DISTINCT category FROM events WHERE active=1 ORDER BY category";
        List<String> cats = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cats.add(rs.getString(1));
        }
        return cats;
    }

    public List<String> findAllOpportunityTypes() throws SQLException {
        String sql = "SELECT DISTINCT opportunity_type FROM events WHERE active=1 ORDER BY opportunity_type";
        List<String> types = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) types.add(rs.getString(1));
        }
        return types;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM events WHERE active=1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    public int insert(Event event) throws SQLException {
        String sql = "INSERT INTO events (title, description, category, opportunity_type, " +
                     "event_date, venue, organizer, reg_deadline, eligibility, capacity, " +
                     "available_seats, registration_open, active) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getCategory());
            ps.setString(4, event.getOpportunityType());
            ps.setDate(5, Date.valueOf(event.getEventDate()));
            ps.setString(6, event.getVenue());
            ps.setString(7, event.getOrganizer());
            if (event.getRegDeadline() != null)
                ps.setTimestamp(8, Timestamp.valueOf(event.getRegDeadline()));
            else ps.setNull(8, Types.TIMESTAMP);
            ps.setString(9, event.getEligibility());
            ps.setInt(10, event.getCapacity());
            ps.setInt(11, event.getAvailableSeats());
            ps.setBoolean(12, event.isRegistrationOpen());
            ps.setBoolean(13, event.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Event insert failed.");
    }

    public boolean update(Event event) throws SQLException {
        String sql = "UPDATE events SET title=?, description=?, category=?, opportunity_type=?, " +
                     "event_date=?, venue=?, organizer=?, reg_deadline=?, eligibility=?, " +
                     "capacity=?, registration_open=?, active=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getCategory());
            ps.setString(4, event.getOpportunityType());
            ps.setDate(5, Date.valueOf(event.getEventDate()));
            ps.setString(6, event.getVenue());
            ps.setString(7, event.getOrganizer());
            if (event.getRegDeadline() != null)
                ps.setTimestamp(8, Timestamp.valueOf(event.getRegDeadline()));
            else ps.setNull(8, Types.TIMESTAMP);
            ps.setString(9, event.getEligibility());
            ps.setInt(10, event.getCapacity());
            ps.setBoolean(11, event.isRegistrationOpen());
            ps.setBoolean(12, event.isActive());
            ps.setInt(13, event.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "UPDATE events SET active = 0 WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private List<Event> query(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }

    private List<Event> mapRows(ResultSet rs) throws SQLException {
        List<Event> events = new ArrayList<>();
        while (rs.next()) events.add(mapRow(rs));
        return events;
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setId(rs.getInt("id"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setCategory(rs.getString("category"));
        e.setOpportunityType(rs.getString("opportunity_type"));
        Date d = rs.getDate("event_date");
        if (d != null) e.setEventDate(d.toLocalDate());
        e.setVenue(rs.getString("venue"));
        e.setOrganizer(rs.getString("organizer"));
        Timestamp dl = rs.getTimestamp("reg_deadline");
        if (dl != null) e.setRegDeadline(dl.toLocalDateTime());
        e.setEligibility(rs.getString("eligibility"));
        e.setCapacity(rs.getInt("capacity"));
        e.setAvailableSeats(rs.getInt("available_seats"));
        e.setRegistrationOpen(rs.getBoolean("registration_open"));
        e.setActive(rs.getBoolean("active"));
        return e;
    }
}
