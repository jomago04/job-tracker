package jobtracker.business;

import jobtracker.dao.ReportDaoJdbc;
import jobtracker.dao.ReportDaoJdbc.ActivityRow;
import java.util.List;

public class ActivityManager {
    private ReportDaoJdbc dao = new ReportDaoJdbc();

    /**
     * Get all activity records for a specific application
     */
    public List<ActivityRow> getActivityByApplicationId(String auid) {
        if (auid == null || auid.trim().isEmpty()) {
            return List.of();
        }
        return dao.listActivityForApplication(auid);
    }

    /**
     * Get all activity records paginated, optionally filtered by application ID
     */
    public List<ActivityRow> getAllActivities(int limit, int offset, String auidFilter) {
        return dao.listActivities(limit, offset, auidFilter);
    }

    public ActivityRow getActivityById(String actuid) {
        if (actuid == null || actuid.trim().isEmpty()) {
            return null;
        }
        // Note: ReportDaoJdbc doesn't have a single-activity getter yet
        // For now, we rely on the filtered list methods
        // A full implementation would add getActivityByActuid() to DAO
        return null;
    }

    /**
     * Allow user to update Activity details if they noticed a mistake
     * Note: This is a simple implementation. Full version would also update in DB.
     */
    public void updateActivityDetails(String actuid, String details) {
        if (actuid == null || actuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity ID is required");
        }
        if (details == null) {
            details = "";
        }

        // In a full implementation, this would call a DAO method to update activity details
        // For now, this is a placeholder for the business logic
        // dao.updateActivityDetails(actuid, details);
    }
}
