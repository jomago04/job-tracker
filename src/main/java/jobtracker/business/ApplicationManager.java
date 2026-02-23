package jobtracker.business;

import jobtracker.dao.ReportDaoJdbc;
import jobtracker.dao.ReportDaoJdbc.ApplicationRow;
import java.sql.Timestamp;
import java.util.List;

public class ApplicationManager {
    private ReportDaoJdbc dao = new ReportDaoJdbc();

    /**
     * Save application with smart logic: if ID is null/empty = insert, else = update
     * Note: Application creation auto-logs Activity record
     */
    public String saveApplication(ApplicationRow app) {
        // Validate required fields
        if (app.uuid == null || app.uuid.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (app.juid == null || app.juid.trim().isEmpty()) {
            throw new IllegalArgumentException("Job ID is required");
        }
        if (app.status == null || app.status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }

        // Validate enum values for status
        validateStatus(app.status);

        // Insert or Update logic
        if (app.auid == null || app.auid.trim().isEmpty()) {
            // New record - INSERT (auto-logs Activity)
            Timestamp now = new Timestamp(System.currentTimeMillis());
            return dao.createApplication(
                app.uuid,
                app.juid,
                app.status,
                now,
                app.source,
                app.notes
            );
        } else {
            // Existing record - UPDATE (no auto-logging for general update, only status change has auto-logging)
            throw new IllegalArgumentException("Use updateApplicationStatus() or relevant update method for existing applications");
        }
    }

    public ApplicationRow getApplicationById(String auid) {
        if (auid == null || auid.trim().isEmpty()) {
            return null;
        }
        return dao.getApplicationByAuid(auid);
    }

    public List<ApplicationRow> getAllApplications(int limit, int offset) {
        return dao.listApplicationsDetailedPaged(limit, offset);
    }

    public void deleteApplication(String auid) {
        if (auid == null || auid.trim().isEmpty()) {
            throw new IllegalArgumentException("Application ID is required");
        }
        dao.deleteApplication(auid);
    }

    /**
     * Update application status with auto-logging to Activity
     */
    public ApplicationRow updateApplicationStatus(String auid, String newStatus) {
        if (auid == null || auid.trim().isEmpty()) {
            throw new IllegalArgumentException("Application ID is required");
        }
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }

        // Validate enum value
        validateStatus(newStatus);

        // Update status and auto-create Activity record
        Timestamp now = new Timestamp(System.currentTimeMillis());
        dao.updateApplicationStatus(auid, newStatus, now);

        // Return updated application
        return dao.getApplicationByAuid(auid);
    }

    /**
     * Update application notes
     */
    public void updateApplicationNotes(String auid, String notes) {
        if (auid == null || auid.trim().isEmpty()) {
            throw new IllegalArgumentException("Application ID is required");
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        dao.updateApplicationNotes(auid, notes, now);
    }

    /**
     * Update application source
     */
    public void updateApplicationSource(String auid, String source) {
        if (auid == null || auid.trim().isEmpty()) {
            throw new IllegalArgumentException("Application ID is required");
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        dao.updateApplicationSource(auid, source, now);
    }

    public boolean applicationExists(String auid) {
        if (auid == null || auid.trim().isEmpty()) {
            return false;
        }
        return dao.applicationExists(auid);
    }

    public boolean userJobApplicationExists(String uuid, String juid) {
        if (uuid == null || uuid.trim().isEmpty() || juid == null || juid.trim().isEmpty()) {
            return false;
        }
        return dao.userJobApplicationExists(uuid, juid);
    }

    private void validateStatus(String status) {
        String[] validStatuses = {"applied", "phone_screen", "interview", "offer", "rejected", "withdrawn"};
        for (String valid : validStatuses) {
            if (valid.equalsIgnoreCase(status)) {
                return;
            }
        }
        throw new IllegalArgumentException("Invalid status. Must be one of: applied, phone_screen, interview, offer, rejected, withdrawn");
    }
}
