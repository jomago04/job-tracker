package jobtracker.dao;

import jobtracker.db.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDaoJdbc {

    public static class ApplicationRow {
        public String auid;
        public String userName;
        public String userEmail;
        public String companyName;
        public String jobTitle;
        public String status;
        public Timestamp appliedAt;
    }

    public static class ActivityRow {
        public String actuid;
        public String eventType;
        public String oldStatus;
        public String newStatus;
        public Timestamp eventTime;
        public String details;
    }

    /** Joined view: application + user + job + company */
    public List<ApplicationRow> listApplicationsDetailed(int limit) {
        String sql = """
            SELECT
              a.auid,
              u.name AS user_name,
              u.email AS user_email,
              c.name AS company_name,
              j.title AS job_title,
              a.status,
              a.applied_at
            FROM application a
            JOIN `user` u ON a.uuid = u.uuid
            JOIN job j ON a.juid = j.juid
            JOIN company c ON j.cuid = c.cuid
            ORDER BY a.applied_at DESC
            LIMIT ?
            """;

        List<ApplicationRow> out = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ApplicationRow r = new ApplicationRow();
                    r.auid = rs.getString("auid");
                    r.userName = rs.getString("user_name");
                    r.userEmail = rs.getString("user_email");
                    r.companyName = rs.getString("company_name");
                    r.jobTitle = rs.getString("job_title");
                    r.status = rs.getString("status");
                    r.appliedAt = rs.getTimestamp("applied_at");
                    out.add(r);
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("listApplicationsDetailed failed", e);
        }
    }

    /** Activity timeline for one application */
    public List<ActivityRow> listActivityForApplication(String auid) {
        String sql = """
            SELECT actuid, event_type, old_status, new_status, event_time, details
            FROM activity
            WHERE auid = ?
            ORDER BY event_time ASC
            """;

        List<ActivityRow> out = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, auid);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivityRow r = new ActivityRow();
                    r.actuid = rs.getString("actuid");
                    r.eventType = rs.getString("event_type");
                    r.oldStatus = rs.getString("old_status");
                    r.newStatus = rs.getString("new_status");
                    r.eventTime = rs.getTimestamp("event_time");
                    r.details = rs.getString("details");
                    out.add(r);
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("listActivityForApplication failed", e);
        }
    }

    /** Simple row counts (good for proving data is there) */
    public List<String> getRowCounts() {
        String sql = """
            SELECT 'user' AS table_name, COUNT(*) AS row_count FROM `user`
            UNION ALL SELECT 'company', COUNT(*) FROM company
            UNION ALL SELECT 'job', COUNT(*) FROM job
            UNION ALL SELECT 'application', COUNT(*) FROM application
            UNION ALL SELECT 'activity', COUNT(*) FROM activity
            """;

        List<String> out = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(rs.getString("table_name") + ": " + rs.getInt("row_count"));
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("getRowCounts failed", e);
        }
    }
}