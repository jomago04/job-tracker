package jobtracker.dao;

import jobtracker.db.Db;
import jobtracker.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class UserDaoJdbc implements UserDao {

    @Override
    public void create(User u) {
        String sql = "INSERT INTO `user` (uuid, email, password_hash, name) VALUES (?, ?, ?, ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.uuid);
            ps.setString(2, u.email);
            ps.setString(3, u.passwordHash);
            ps.setString(4, u.name);
            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("User create failed (duplicate email or uuid): " + u.email, e);
        } catch (SQLException e) {
            throw new RuntimeException("User create failed", e);
        }
    }

    @Override
    public Optional<User> getById(String uuid) {
        String sql = "SELECT uuid, email, password_hash, name, created_at FROM `user` WHERE uuid = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("User getById failed", e);
        }
    }

    @Override
    public Optional<User> getByEmail(String email) {
        String sql = "SELECT uuid, email, password_hash, name, created_at FROM `user` WHERE email = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("User getByEmail failed", e);
        }
    }

    @Override
    public List<User> listAll() {
        String sql = "SELECT uuid, email, password_hash, name, created_at FROM `user` ORDER BY created_at DESC";
        List<User> out = new ArrayList<>();
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(map(rs));
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("User listAll failed", e);
        }
    }

    @Override
    public void update(User u) {
        String sql = "UPDATE `user` SET email = ?, password_hash = ?, name = ? WHERE uuid = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.email);
            ps.setString(2, u.passwordHash);
            ps.setString(3, u.name);
            ps.setString(4, u.uuid);

            int updated = ps.executeUpdate();
            if (updated == 0) throw new RuntimeException("User update failed: uuid not found " + u.uuid);

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("User update failed (duplicate email): " + u.email, e);
        } catch (SQLException e) {
            throw new RuntimeException("User update failed", e);
        }
    }

    @Override
    public void delete(String uuid) {
        String sql = "DELETE FROM `user` WHERE uuid = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, uuid);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("User delete failed (may be blocked by FK constraints)", e);
        }
    }

    private static User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.uuid = rs.getString("uuid");
        u.email = rs.getString("email");
        u.passwordHash = rs.getString("password_hash");
        u.name = rs.getString("name");
        Timestamp ts = rs.getTimestamp("created_at");
        u.createdAt = (ts == null) ? null : ts.toLocalDateTime();
        return u;
    }
}