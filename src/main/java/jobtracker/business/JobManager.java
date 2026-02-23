package jobtracker.business;

import jobtracker.dao.ReportDaoJdbc;
import jobtracker.dao.ReportDaoJdbc.JobRow;
import java.util.List;

public class JobManager {
    private ReportDaoJdbc dao = new ReportDaoJdbc();

    /**
     * Save job with smart logic: if ID is null/empty = insert, else = update
     */
    public String saveJob(JobRow job) {
        // Validate required fields
        if (job.cuid == null || job.cuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Company ID is required");
        }
        if (job.title == null || job.title.trim().isEmpty()) {
            throw new IllegalArgumentException("Job title is required");
        }

        // Verify company exists
        if (!dao.companyExists(job.cuid)) {
            throw new IllegalArgumentException("Company does not exist");
        }

        // Use provided values or defaults
        String employmentType = job.title != null && job.title.contains("Intern") ? "internship" : "full_time";
        String workType = "remote";

        // Insert or Update logic
        if (job.juid == null || job.juid.trim().isEmpty()) {
            // New record - INSERT
            return dao.createJob(
                job.cuid,
                job.title,
                employmentType,
                workType,
                null, // job_url
                null, // salary_min
                null  // salary_max
            );
        } else {
            // Existing record - UPDATE
            dao.updateJob(
                job.juid,
                job.title,
                employmentType,
                workType,
                null, // job_url
                null, // salary_min
                null  // salary_max
            );
            return job.juid;
        }
    }

    public JobRow getJobById(String juid) {
        if (juid == null || juid.trim().isEmpty()) {
            return null;
        }
        return dao.getJobByJuid(juid);
    }

    public List<JobRow> getAllJobs(int limit, int offset) {
        return dao.listJobs(limit, offset);
    }

    public void deleteJob(String juid) {
        if (juid == null || juid.trim().isEmpty()) {
            throw new IllegalArgumentException("Job ID is required");
        }
        dao.deleteJob(juid);
    }

    public boolean jobExists(String juid) {
        if (juid == null || juid.trim().isEmpty()) {
            return false;
        }
        return getJobById(juid) != null;
    }
}
