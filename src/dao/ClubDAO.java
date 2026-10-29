package dao;

import model.Club;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ClubDAO — Data access for the clubs table.
 */
public class ClubDAO {

    public List<Club> findAll() throws SQLException {
        String sql = "SELECT * FROM clubs WHERE active = 1 ORDER BY name";
        return query(sql);
    }

    public List<Club> findByCategory(String category) throws SQLException {
        String sql = "SELECT * FROM clubs WHERE active = 1 AND category = ? ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            return mapRows(ps.executeQuery());
        }
    }

    public List<Club> searchByName(String keyword) throws SQLException {
        String sql = "SELECT * FROM clubs WHERE active = 1 AND name LIKE ? ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            return mapRows(ps.executeQuery());
        }
    }

    public Club findById(int id) throws SQLException {
        String sql = "SELECT * FROM clubs WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public int insert(Club club) throws SQLException {
        String sql = "INSERT INTO clubs (name, category, description, contact_email, contact_info, active) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, club.getName());
            ps.setString(2, club.getCategory());
            ps.setString(3, club.getDescription());
            ps.setString(4, club.getContactEmail());
            ps.setString(5, club.getContactInfo());
            ps.setBoolean(6, club.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Club insert failed.");
    }

    public boolean update(Club club) throws SQLException {
        String sql = "UPDATE clubs SET name=?, category=?, description=?, contact_email=?, contact_info=?, active=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, club.getName());
            ps.setString(2, club.getCategory());
            ps.setString(3, club.getDescription());
            ps.setString(4, club.getContactEmail());
            ps.setString(5, club.getContactInfo());
            ps.setBoolean(6, club.isActive());
            ps.setInt(7, club.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "UPDATE clubs SET active = 0 WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM clubs WHERE active = 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<String> findAllCategories() throws SQLException {
        String sql = "SELECT DISTINCT category FROM clubs WHERE active = 1 ORDER BY category";
        List<String> cats = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cats.add(rs.getString(1));
        }
        return cats;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private List<Club> query(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }

    private List<Club> mapRows(ResultSet rs) throws SQLException {
        List<Club> clubs = new ArrayList<>();
        while (rs.next()) clubs.add(mapRow(rs));
        return clubs;
    }

    private Club mapRow(ResultSet rs) throws SQLException {
        return new Club(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getString("description"),
            rs.getString("contact_email"),
            rs.getString("contact_info"),
            rs.getBoolean("active")
        );
    }
}
