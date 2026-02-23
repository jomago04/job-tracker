package jobtracker.dao;

import jobtracker.db.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportDaoJdbc {

    // -------------------------
    // ROW TYPES (DTOs)
    // -------------------------
    public static class UserRow {
        public String uuid;
        public String email;
        public String passwordHash;
        public String name;
        public Timestamp createdAt;
    }

    public static class CompanyRow {
        public String cuid;
        public String name;
        public Timestamp createdAt;
    }

    public static class JobRow {
        public String juid;
        public String cuid;
        public String title;
        public String url;
        public Timestamp createdAt;
    }

    public static class ApplicationRow {
        public String auid;

        // foreign keys
        public String uuid; // user uuid
        public String juid; // job id

        // joined fields (display)
        public String userName;
        public String userEmail;
        public String companyName;
        public String jobTitle;

        // application fields
        public String status;
        public Timestamp appliedAt;
        public String source;
        public String notes;
        public Timestamp lastUpdatedAt;
    }

    public static class ActivityRow {
        public String actuid;
        public String auid;
        public String eventType;
        public String oldStatus;
        public String newStatus;
        public Timestamp eventTime;
        public String details;
    }

    // -------------------------
    // EXISTING METHODS (kept)
    // -------------------------

    /** Joined view: application + user + job + company (non-paged) */
    public List<ApplicationRow> listApplicationsDetailed(int limit) {
        String sql = "SELECT a.auid, a.uuid, a.juid, u.name AS user_name, u.email AS user_email, c.name AS company_name, j.title AS job_title, a.status, a.applied_at, a.source, a.notes, a.last_updated_at FROM application a JOIN `user` u ON a.uuid = u.uuid JOIN job j ON a.juid = j.juid JOIN company c ON j.cuid = c.cuid ORDER BY a.applied_at DESC LIMIT ?";

        List<ApplicationRow> out = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapApplicationJoined(rs));
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("listApplicationsDetailed failed", e);
        }
    }

    /** Activity timeline for one application (non-paged) */
    public List<ActivityRow> listActivityForApplication(String auid) {
        String sql = "SELECT actuid, auid, event_type, old_status, new_status, event_time, details FROM activity WHERE auid = ? ORDER BY event_time ASC";

        List<ActivityRow> out = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, auid);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapActivity(rs));
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("listActivityForApplication failed", e);
        }
    }

    /** Simple row counts */
    public List<String> getRowCounts() {
        String sql = "SELECT 'user' AS table_name, COUNT(*) AS row_count FROM `user` UNION ALL SELECT 'company', COUNT(*) FROM company UNION ALL SELECT 'job', COUNT(*) FROM job UNION ALL SELECT 'application', COUNT(*) FROM application UNION ALL SELECT 'activity', COUNT(*) FROM activity";

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

    // -------------------------
    // NEW: BROWSE METHODS (paged)
    // -------------------------

    public List<UserRow> listUsers(int limit, int offset) {
        String sql = "SELECT uuid, email, password_hash, name, created_at FROM `user` ORDER BY created_at DESC LIMIT ? OFFSET ?";

        List<UserRow> out = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserRow r = new UserRow();
                    r.uuid = rs.getString("uuid");
                    r.email = rs.getString("email");
                    r.passwordHash = rs.getString("password_hash");
                    r.name = rs.getString("name");
                    r.createdAt = rs.getTimestamp("created_at");
                    out.add(r);
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("listUsers failed", e);
        }
    }

    public List<CompanyRow> listCompanies(int limit, int offset) {
        String sql = "SELECT cuid, name, created_at FROM company ORDER BY created_at DESC LIMIT ? OFFSET ?";

        List<CompanyRow> out = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CompanyRow r = new CompanyRow();
                    r.cuid = rs.getString("cuid");
                    r.name = rs.getString("name");
                    r.createdAt = rs.getTimestamp("created_at");
                    out.add(r);
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("listCompanies failed", e);
        }
    }

    public List<JobRow> listJobs(int limit, int offset) {
        String sql = "SELECT juid, cuid, title, job_url AS url, created_at FROM job ORDER BY created_at DESC LIMIT ? OFFSET ?";

        List<JobRow> out = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JobRow r = new JobRow();
                    r.juid = rs.getString("juid");
                    r.cuid = rs.getString("cuid");
                    r.title = rs.getString("title");
                    r.url = rs.getString("url");
                    r.createdAt = rs.getTimestamp("created_at");
                    out.add(r);
                }
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("listJobs failed", e);
        }
    }

    /** Joined view, but paged */
    public List<ApplicationRow> listApplicationsDetailedPaged(int limit, int offset) {
        String sql = "SELECT a.auid, a.uuid, a.juid, u.name AS user_name, u.email AS user_email, c.name AS company_name, j.title AS job_title, a.status, a.applied_at, a.source, a.notes, a.last_updated_at FROM application a JOIN `user` u ON a.uuid = u.uuid JOIN job j ON a.juid = j.juid JOIN company c ON j.cuid = c.cuid ORDER BY a.applied_at DESC LIMIT ? OFFSET ?";

        List<ApplicationRow> out = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapApplicationJoined(rs));
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("listApplicationsDetailedPaged failed", e);
        }
    }

    /** Paged list of activity, optionally filtered by auid */
    public List<ActivityRow> listActivities(int limit, int offset, String auidFilter) {
        boolean filtered = (auidFilter != null && !auidFilter.trim().isEmpty());

        String sql = filtered
                ? "SELECT actuid, auid, event_type, old_status, new_status, event_time, details FROM activity WHERE auid = ? ORDER BY event_time DESC LIMIT ? OFFSET ?"
                : "SELECT actuid, auid, event_type, old_status, new_status, event_time, details FROM activity ORDER BY event_time DESC LIMIT ? OFFSET ?";

        List<ActivityRow> out = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            if (filtered) ps.setString(idx++, auidFilter);
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapActivity(rs));
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("listActivities failed", e);
        }
    }

    // -------------------------
    // MAPPERS
    // -------------------------

    private static ApplicationRow mapApplicationJoined(ResultSet rs) throws SQLException {
        ApplicationRow r = new ApplicationRow();
        r.auid = rs.getString("auid");
        r.uuid = rs.getString("uuid");
        r.juid = rs.getString("juid");

        r.userName = rs.getString("user_name");
        r.userEmail = rs.getString("user_email");
        r.companyName = rs.getString("company_name");
        r.jobTitle = rs.getString("job_title");

        r.status = rs.getString("status");
        r.appliedAt = rs.getTimestamp("applied_at");
        r.source = rs.getString("source");
        r.notes = rs.getString("notes");
        r.lastUpdatedAt = rs.getTimestamp("last_updated_at");
        return r;
    }

    private static ActivityRow mapActivity(ResultSet rs) throws SQLException {
        ActivityRow r = new ActivityRow();
        r.actuid = rs.getString("actuid");
        r.auid = rs.getString("auid");
        r.eventType = rs.getString("event_type");
        r.oldStatus = rs.getString("old_status");
        r.newStatus = rs.getString("new_status");
        r.eventTime = rs.getTimestamp("event_time");
        r.details = rs.getString("details");
        return r;
    }

    // -------------------------
    // CRUD: USER OPERATIONS
    // -------------------------

    public String createUser(String email, String passwordHash, String name) {
        String uuid = generateUUID();
        String sql = "INSERT INTO `user` (uuid, email, password_hash, name, created_at) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, uuid);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, name);

            ps.executeUpdate();
            return uuid;

        } catch (SQLException e) {
            throw new RuntimeException("createUser failed", e);
        }
    }

    public UserRow getUserByUuid(String uuid) {
        String sql = "SELECT uuid, email, password_hash, name, created_at FROM `user` WHERE uuid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, uuid);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserRow r = new UserRow();
                    r.uuid = rs.getString("uuid");
                    r.email = rs.getString("email");
                    r.passwordHash = rs.getString("password_hash");
                    r.name = rs.getString("name");
                    r.createdAt = rs.getTimestamp("created_at");
                    return r;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getUserByUuid failed", e);
        }
    }

    public boolean userEmailExists(String email) {
        String sql = "SELECT 1 FROM `user` WHERE email = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("userEmailExists failed", e);
        }
    }

    public void updateUser(String uuid, String email, String passwordHash, String name) {
        String sql = "UPDATE `user` SET email = ?, password_hash = ?, name = ? WHERE uuid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setString(3, name);
            ps.setString(4, uuid);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("User not found");
            }

        } catch (SQLException e) {
            throw new RuntimeException("updateUser failed", e);
        }
    }

    public void deleteUser(String uuid) {
        if (hasApplications(uuid)) {
            throw new RuntimeException("Cannot delete user with existing applications");
        }

        String sql = "DELETE FROM `user` WHERE uuid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, uuid);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("deleteUser failed", e);
        }
    }

    // -------------------------
    // CRUD: COMPANY OPERATIONS
    // -------------------------

    public String createCompany(String name, String industry, String locationCity,
                               String locationState, String companyUrl) {
        String cuid = generateUUID();
        String sql = "INSERT INTO company (cuid, name, industry, location_city, location_state, company_url, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cuid);
            ps.setString(2, name);
            ps.setString(3, industry);
            ps.setString(4, locationCity);
            ps.setString(5, locationState);
            ps.setString(6, companyUrl);

            ps.executeUpdate();
            return cuid;

        } catch (SQLException e) {
            throw new RuntimeException("createCompany failed", e);
        }
    }

    public CompanyRow getCompanyByCuid(String cuid) {
        String sql = "SELECT cuid, name, created_at FROM company WHERE cuid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cuid);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CompanyRow r = new CompanyRow();
                    r.cuid = rs.getString("cuid");
                    r.name = rs.getString("name");
                    r.createdAt = rs.getTimestamp("created_at");
                    return r;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getCompanyByCuid failed", e);
        }
    }

    public boolean companyNameExists(String name) {
        String sql = "SELECT 1 FROM company WHERE name = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("companyNameExists failed", e);
        }
    }

    public void updateCompany(String cuid, String name, String industry, String locationCity,
                             String locationState, String companyUrl) {
        String sql = "UPDATE company SET name = ?, industry = ?, location_city = ?, location_state = ?, company_url = ? WHERE cuid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, industry);
            ps.setString(3, locationCity);
            ps.setString(4, locationState);
            ps.setString(5, companyUrl);
            ps.setString(6, cuid);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Company not found");
            }

        } catch (SQLException e) {
            throw new RuntimeException("updateCompany failed", e);
        }
    }

    public void deleteCompany(String cuid) {
        if (companyHasJobs(cuid)) {
            throw new RuntimeException("Cannot delete company with existing jobs");
        }

        String sql = "DELETE FROM company WHERE cuid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cuid);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("deleteCompany failed", e);
        }
    }

    // -------------------------
    // CRUD: JOB OPERATIONS
    // -------------------------

    public String createJob(String cuid, String title, String employmentType, String workType,
                           String jobUrl, Integer salaryMin, Integer salaryMax) {
        String juid = generateUUID();
        String sql = "INSERT INTO job (juid, cuid, title, employment_type, work_type, job_url, salary_min, salary_max, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, juid);
            ps.setString(2, cuid);
            ps.setString(3, title);
            ps.setString(4, employmentType);
            ps.setString(5, workType);
            ps.setString(6, jobUrl);
            if (salaryMin != null) ps.setInt(7, salaryMin);
            else ps.setNull(7, Types.INTEGER);
            if (salaryMax != null) ps.setInt(8, salaryMax);
            else ps.setNull(8, Types.INTEGER);

            ps.executeUpdate();
            return juid;

        } catch (SQLException e) {
            throw new RuntimeException("createJob failed", e);
        }
    }

    public JobRow getJobByJuid(String juid) {
        String sql = "SELECT juid, cuid, title, job_url AS url, created_at FROM job WHERE juid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, juid);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JobRow r = new JobRow();
                    r.juid = rs.getString("juid");
                    r.cuid = rs.getString("cuid");
                    r.title = rs.getString("title");
                    r.url = rs.getString("url");
                    r.createdAt = rs.getTimestamp("created_at");
                    return r;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getJobByJuid failed", e);
        }
    }

    public boolean companyExists(String cuid) {
        String sql = "SELECT 1 FROM company WHERE cuid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cuid);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("companyExists failed", e);
        }
    }

    public void updateJob(String juid, String title, String employmentType, String workType,
                         String jobUrl, Integer salaryMin, Integer salaryMax) {
        String sql = "UPDATE job SET title = ?, employment_type = ?, work_type = ?, job_url = ?, salary_min = ?, salary_max = ? WHERE juid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, employmentType);
            ps.setString(3, workType);
            ps.setString(4, jobUrl);
            if (salaryMin != null) ps.setInt(5, salaryMin);
            else ps.setNull(5, Types.INTEGER);
            if (salaryMax != null) ps.setInt(6, salaryMax);
            else ps.setNull(6, Types.INTEGER);
            ps.setString(7, juid);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Job not found");
            }

        } catch (SQLException e) {
            throw new RuntimeException("updateJob failed", e);
        }
    }

    public void deleteJob(String juid) {
        if (jobHasApplications(juid)) {
            throw new RuntimeException("Cannot delete job with existing applications");
        }

        String sql = "DELETE FROM job WHERE juid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, juid);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("deleteJob failed", e);
        }
    }

    // -------------------------
    // CRUD: APPLICATION OPERATIONS
    // -------------------------

    public String createApplication(String uuid, String juid, String status, Timestamp appliedAt,
                                   String source, String notes) {
        String auid = generateUUID();
        String sql = "INSERT INTO application (auid, uuid, juid, status, applied_at, source, notes, last_updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, auid);
            ps.setString(2, uuid);
            ps.setString(3, juid);
            ps.setString(4, status);
            ps.setTimestamp(5, appliedAt);
            if (source != null) ps.setString(6, source);
            else ps.setNull(6, Types.VARCHAR);
            if (notes != null) ps.setString(7, notes);
            else ps.setNull(7, Types.VARCHAR);

            ps.executeUpdate();

            // Auto-create Activity record
            createActivityForApplication(auid, uuid);

            return auid;

        } catch (SQLException e) {
            throw new RuntimeException("createApplication failed", e);
        }
    }

    public ApplicationRow getApplicationByAuid(String auid) {
        String sql = "SELECT a.auid, a.uuid, a.juid, u.name AS user_name, u.email AS user_email, c.name AS company_name, j.title AS job_title, a.status, a.applied_at, a.source, a.notes, a.last_updated_at FROM application a JOIN `user` u ON a.uuid = u.uuid JOIN job j ON a.juid = j.juid JOIN company c ON j.cuid = c.cuid WHERE a.auid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, auid);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapApplicationJoined(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getApplicationByAuid failed", e);
        }
    }

    public boolean applicationExists(String auid) {
        String sql = "SELECT 1 FROM application WHERE auid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, auid);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("applicationExists failed", e);
        }
    }

    public boolean userJobApplicationExists(String uuid, String juid) {
        String sql = "SELECT 1 FROM application WHERE uuid = ? AND juid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, uuid);
            ps.setString(2, juid);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("userJobApplicationExists failed", e);
        }
    }

    public void updateApplicationStatus(String auid, String newStatus, Timestamp lastUpdatedAt) {
        // Get current status for Activity logging
        String selectSql = "SELECT status FROM application WHERE auid = ?";
        String oldStatus;

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {

            ps.setString(1, auid);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Application not found");
                }
                oldStatus = rs.getString("status");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get current status", e);
        }

        String updateSql = "UPDATE application SET status = ?, last_updated_at = ? WHERE auid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {

            ps.setString(1, newStatus);
            ps.setTimestamp(2, lastUpdatedAt);
            ps.setString(3, auid);

            ps.executeUpdate();

            // Auto-create Activity for status change
            try (PreparedStatement ps2 = conn.prepareStatement("SELECT uuid FROM application WHERE auid = ?")) {
                ps2.setString(1, auid);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        String uuid = rs.getString("uuid");
                        createActivityForStatusChange(auid, uuid, oldStatus, newStatus);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("updateApplicationStatus failed", e);
        }
    }

    public void updateApplicationNotes(String auid, String notes, Timestamp lastUpdatedAt) {
        String sql = "UPDATE application SET notes = ?, last_updated_at = ? WHERE auid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (notes != null) ps.setString(1, notes);
            else ps.setNull(1, Types.VARCHAR);
            ps.setTimestamp(2, lastUpdatedAt);
            ps.setString(3, auid);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Application not found");
            }

        } catch (SQLException e) {
            throw new RuntimeException("updateApplicationNotes failed", e);
        }
    }

    public void updateApplicationSource(String auid, String source, Timestamp lastUpdatedAt) {
        String sql = "UPDATE application SET source = ?, last_updated_at = ? WHERE auid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (source != null) ps.setString(1, source);
            else ps.setNull(1, Types.VARCHAR);
            ps.setTimestamp(2, lastUpdatedAt);
            ps.setString(3, auid);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Application not found");
            }

        } catch (SQLException e) {
            throw new RuntimeException("updateApplicationSource failed", e);
        }
    }

    public void deleteApplication(String auid) {
        String sql = "DELETE FROM application WHERE auid = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, auid);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("deleteApplication failed", e);
        }
    }

    // -------------------------
    // HELPER METHODS
    // -------------------------

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public boolean hasApplications(String userUuid) {
        String sql = "SELECT 1 FROM application WHERE uuid = ? LIMIT 1";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userUuid);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("hasApplications failed", e);
        }
    }

    public boolean companyHasJobs(String companyCuid) {
        String sql = "SELECT 1 FROM job WHERE cuid = ? LIMIT 1";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, companyCuid);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("companyHasJobs failed", e);
        }
    }

    public boolean jobHasApplications(String jobJuid) {
        String sql = "SELECT 1 FROM application WHERE juid = ? LIMIT 1";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jobJuid);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("jobHasApplications failed", e);
        }
    }

    private void createActivityForApplication(String auid, String uuid) {
        String actuid = generateUUID();
        String sql = "INSERT INTO activity (actuid, auid, uuid, event_type, old_status, new_status, event_time, details) VALUES (?, ?, ?, 'created', NULL, NULL, NOW(), 'Application created')";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, actuid);
            ps.setString(2, auid);
            ps.setString(3, uuid);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("createActivityForApplication failed", e);
        }
    }

    private void createActivityForStatusChange(String auid, String uuid, String oldStatus, String newStatus) {
        String actuid = generateUUID();
        String sql = "INSERT INTO activity (actuid, auid, uuid, event_type, old_status, new_status, event_time, details) VALUES (?, ?, ?, 'status_change', ?, ?, NOW(), 'Status updated via console')";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, actuid);
            ps.setString(2, auid);
            ps.setString(3, uuid);
            ps.setString(4, oldStatus);
            ps.setString(5, newStatus);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("createActivityForStatusChange failed", e);
        }
    }
}
