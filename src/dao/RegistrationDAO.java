package dao;

import model.Registration;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RegistrationDAO — Data access operations for event registrations and attendance.
 * Uses PreparedStatement and transactions with FOR UPDATE locking to enforce capacity limits
 * and handle repeated registration attempts safely.
 */
public class RegistrationDAO {

    /**
     * Safely registers a student for an event using a JDBC transaction.
     * Enforces deadline check, registration open check, capacity limits, and duplicate check.
     */
    public String safeRegister(int studentId, int eventId) throws SQLException {
        Connection con = DBConnection.getConnection();
        boolean originalAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            // 1. Lock and inspect event
            String eventSql = "SELECT capacity, available_seats, registration_open, reg_deadline FROM events WHERE id = ? FOR UPDATE";
            int availableSeats = 0;
            boolean regOpen = false;
            Timestamp deadline = null;

            try (PreparedStatement ps = con.prepareStatement(eventSql)) {
                ps.setInt(1, eventId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return "Event not found.";
                    }
                    availableSeats = rs.getInt("available_seats");
                    regOpen = rs.getBoolean("registration_open");
                    deadline = rs.getTimestamp("reg_deadline");
                }
            }

            if (!regOpen) {
                con.rollback();
                return "Registration is currently closed for this event.";
            }

            if (deadline != null && deadline.before(new Timestamp(System.currentTimeMillis()))) {
                con.rollback();
                return "Registration deadline has passed.";
            }

            if (availableSeats <= 0) {
                con.rollback();
                return "Event is full. No seats remaining.";
            }

            // 2. Check existing registration
            String regSql = "SELECT id, status FROM registrations WHERE student_id = ? AND event_id = ?";
            Integer existingId = null;
            String existingStatus = null;

            try (PreparedStatement ps = con.prepareStatement(regSql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, eventId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        existingId = rs.getInt("id");
                        existingStatus = rs.getString("status");
                    }
                }
            }

            if (existingId != null) {
                if ("REGISTERED".equalsIgnoreCase(existingStatus)) {
                    con.rollback();
                    return "You are already registered for this event.";
                } else {
                    // Re-activate previously cancelled registration
                    String reactivateSql = "UPDATE registrations SET status='REGISTERED', reg_date=CURRENT_TIMESTAMP WHERE id=?";
                    try (PreparedStatement ps = con.prepareStatement(reactivateSql)) {
                        ps.setInt(1, existingId);
                        ps.executeUpdate();
                    }
                    String seatSql = "UPDATE events SET available_seats = available_seats - 1 WHERE id=?";
                    try (PreparedStatement ps = con.prepareStatement(seatSql)) {
                        ps.setInt(1, eventId);
                        ps.executeUpdate();
                    }
                    con.commit();
                    return "Registration successful!";
                }
            }

            // 3. Insert new registration
            String insertSql = "INSERT INTO registrations (student_id, event_id, status, attendance_status) VALUES (?, ?, 'REGISTERED', 'PENDING')";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, eventId);
                ps.executeUpdate();
            }

            // 4. Decrement available seats
            String seatSql = "UPDATE events SET available_seats = available_seats - 1 WHERE id=?";
            try (PreparedStatement ps = con.prepareStatement(seatSql)) {
                ps.setInt(1, eventId);
                ps.executeUpdate();
            }

            con.commit();
            return "Registration successful!";
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(originalAutoCommit);
        }
    }

    /**
     * Cancels an active registration and restores one available seat.
     */
    public boolean cancelRegistration(int studentId, int eventId) throws SQLException {
        Connection con = DBConnection.getConnection();
        boolean originalAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            String check = "SELECT id FROM registrations WHERE student_id=? AND event_id=? AND status='REGISTERED'";
            int regId = -1;
            try (PreparedStatement ps = con.prepareStatement(check)) {
                ps.setInt(1, studentId);
                ps.setInt(2, eventId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.commit();
                        return false;
                    }
                    regId = rs.getInt("id");
                }
            }

            String cancel = "UPDATE registrations SET status='CANCELLED' WHERE id=?";
            try (PreparedStatement ps = con.prepareStatement(cancel)) {
                ps.setInt(1, regId);
                ps.executeUpdate();
            }

            String restore = "UPDATE events SET available_seats = available_seats + 1 WHERE id=? AND available_seats < capacity";
            try (PreparedStatement ps = con.prepareStatement(restore)) {
                ps.setInt(1, eventId);
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(originalAutoCommit);
        }
    }

    public Registration findByStudentAndEvent(int studentId, int eventId) throws SQLException {
        String sql = "SELECT r.*, e.title AS event_title, e.event_date " +
                     "FROM registrations r JOIN events e ON r.event_id = e.id " +
                     "WHERE r.student_id=? AND r.event_id=? " +
                     "ORDER BY r.reg_date DESC LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Registration> findByStudent(int studentId) throws SQLException {
        String sql = "SELECT r.*, e.title AS event_title, e.event_date " +
                     "FROM registrations r JOIN events e ON r.event_id = e.id " +
                     "WHERE r.student_id = ? ORDER BY r.reg_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return mapRows(ps.executeQuery());
        }
    }

    public List<Registration> findAttendedByStudent(int studentId) throws SQLException {
        String sql = "SELECT r.*, e.title AS event_title, e.event_date " +
                     "FROM registrations r JOIN events e ON r.event_id = e.id " +
                     "WHERE r.student_id=? AND r.attendance_status='ATTENDED' " +
                     "ORDER BY e.event_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return mapRows(ps.executeQuery());
        }
    }

    public List<Registration> findByEvent(int eventId) throws SQLException {
        String sql = "SELECT r.*, s.name AS student_name, e.title AS event_title, e.event_date " +
                     "FROM registrations r " +
                     "JOIN students s ON r.student_id = s.id " +
                     "JOIN events   e ON r.event_id   = e.id " +
                     "WHERE r.event_id = ? ORDER BY r.reg_date";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            return mapRows(ps.executeQuery());
        }
    }

    public boolean updateAttendance(int registrationId, String attendanceStatus, String verifiedBy) throws SQLException {
        String sql = "UPDATE registrations SET attendance_status=?, verified_at=CURRENT_TIMESTAMP, verified_by=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, attendanceStatus);
            ps.setString(2, verifiedBy != null ? verifiedBy : "Admin");
            ps.setInt(3, registrationId);
            return ps.executeUpdate() > 0;
        }
    }

    public int countAllRegistrations() throws SQLException {
        String sql = "SELECT COUNT(*) FROM registrations WHERE status='REGISTERED'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int countVerifiedAttendances() throws SQLException {
        String sql = "SELECT COUNT(*) FROM registrations WHERE attendance_status='ATTENDED'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private List<Registration> mapRows(ResultSet rs) throws SQLException {
        List<Registration> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    private Registration mapRow(ResultSet rs) throws SQLException {
        Registration r = new Registration();
        r.setId(rs.getInt("id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setEventId(rs.getInt("event_id"));
        r.setStatus(rs.getString("status"));
        r.setAttendanceStatus(rs.getString("attendance_status"));
        Timestamp rat = rs.getTimestamp("reg_date");
        if (rat != null) r.setRegisteredAt(rat.toLocalDateTime());
        Timestamp vat = rs.getTimestamp("verified_at");
        if (vat != null) r.setVerifiedAt(vat.toLocalDateTime());
        try { r.setStudentName(rs.getString("student_name")); } catch (SQLException ignored) {}
        try { r.setEventTitle(rs.getString("event_title")); } catch (SQLException ignored) {}
        try {
            java.sql.Date ed = rs.getDate("event_date");
            if (ed != null) r.setEventDate(ed.toLocalDate());
        } catch (SQLException ignored) {}
        return r;
    }
}
