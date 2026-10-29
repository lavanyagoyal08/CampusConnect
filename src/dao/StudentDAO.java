package dao;

import model.Student;
import util.DBConnection;

import java.sql.*;

/**
 * StudentDAO — Data access operations for the students table.
 * Interests are stored as a delimited field directly in the students table.
 */
public class StudentDAO {

    /** Inserts a new student and returns the generated ID. */
    public int insert(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, email, password_hash, salt, hash_iterations, department, year_of_study, interests) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getPasswordHash());
            ps.setString(4, student.getSalt() != null ? student.getSalt() : "");
            ps.setInt(5, student.getHashIterations() > 0 ? student.getHashIterations() : 100_000);
            ps.setString(6, student.getDepartment());
            ps.setInt(7, student.getYearOfStudy());
            ps.setString(8, student.getInterestsDelimited());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Student insert failed — no generated ID returned.");
    }

    /** Finds a student by email (used during authentication). Returns null if not found. */
    public Student findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM students WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Finds a student by primary key ID. */
    public Student findById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Checks whether an email is already registered in the system. */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Updates student profile fields including name, department, year of study, and delimited interests. */
    public boolean updateProfile(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, department = ?, year_of_study = ?, interests = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getDepartment());
            ps.setInt(3, student.getYearOfStudy());
            ps.setString(4, student.getInterestsDelimited());
            ps.setInt(5, student.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Returns the total count of registered students. */
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── Row mapper ─────────────────────────────────────────────────────────────
    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setPasswordHash(rs.getString("password_hash"));
        s.setSalt(rs.getString("salt"));
        try {
            s.setHashIterations(rs.getInt("hash_iterations"));
        } catch (SQLException ignored) {
            s.setHashIterations(100_000);
        }
        s.setDepartment(rs.getString("department"));
        s.setYearOfStudy(rs.getInt("year_of_study"));
        s.setInterestsFromDelimited(rs.getString("interests"));
        return s;
    }
}
