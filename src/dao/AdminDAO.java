package dao;

import model.Admin;
import util.DBConnection;

import java.sql.*;

/**
 * AdminDAO — Data access operations for the admins table.
 */
public class AdminDAO {

    /** Inserts a new admin user and returns generated ID. */
    public int insert(Admin admin) throws SQLException {
        String sql = "INSERT INTO admins (name, email, password_hash, salt, hash_iterations, admin_level) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, admin.getName());
            ps.setString(2, admin.getEmail());
            ps.setString(3, admin.getPasswordHash());
            ps.setString(4, admin.getSalt() != null ? admin.getSalt() : "");
            ps.setInt(5, admin.getHashIterations() > 0 ? admin.getHashIterations() : 100_000);
            ps.setString(6, admin.getAdminLevel() != null ? admin.getAdminLevel() : "SUPER");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Admin insert failed — no generated ID returned.");
    }

    /** Finds an administrator by email. */
    public Admin findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM admins WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Finds an administrator by ID. */
    public Admin findById(int id) throws SQLException {
        String sql = "SELECT * FROM admins WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Returns total count of admins. */
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM admins";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setId(rs.getInt("id"));
        a.setName(rs.getString("name"));
        a.setEmail(rs.getString("email"));
        a.setPasswordHash(rs.getString("password_hash"));
        a.setSalt(rs.getString("salt"));
        try {
            a.setHashIterations(rs.getInt("hash_iterations"));
        } catch (SQLException ignored) {
            a.setHashIterations(100_000);
        }
        try {
            a.setAdminLevel(rs.getString("admin_level"));
        } catch (SQLException ignored) {
            a.setAdminLevel("SUPER");
        }
        return a;
    }
}
