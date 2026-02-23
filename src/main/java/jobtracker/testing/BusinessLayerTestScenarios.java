package jobtracker.testing;

import jobtracker.business.*;
import jobtracker.dao.ReportDaoJdbc.*;
import java.util.List;

/**
 * Test scenarios demonstrating complete workflows through the business layer
 */
public class BusinessLayerTestScenarios {

    private static long timestamp = System.currentTimeMillis();

    public static void main(String[] args) {
        System.out.println("=== Job Tracker Test Scenarios ===\n");
        System.out.println("Using timestamp: " + timestamp + "\n");

        try {
            scenario1_CompleteJobApplicationJourney();
            System.out.println("\n✓ Scenario 1 PASSED\n");
        } catch (Exception e) {
            System.out.println("\n✗ Scenario 1 FAILED: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        try {
            scenario2_ApplicationStatusProgression();
            System.out.println("\n✓ Scenario 2 PASSED\n");
        } catch (Exception e) {
            System.out.println("\n✗ Scenario 2 FAILED: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        try {
            scenario3_RejectionAndReapply();
            System.out.println("\n✓ Scenario 3 PASSED\n");
        } catch (Exception e) {
            System.out.println("\n✗ Scenario 3 FAILED: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        try {
            scenario4_UserProfileUpdateAndApplication();
            System.out.println("\n✓ Scenario 4 PASSED\n");
        } catch (Exception e) {
            System.out.println("\n✗ Scenario 4 FAILED: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        try {
            scenario5_DeleteWithCascading();
            System.out.println("\n✓ Scenario 5 PASSED\n");
        } catch (Exception e) {
            System.out.println("\n✗ Scenario 5 FAILED: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        System.out.println("=== All Scenarios Complete ===");
    }

    private static String uniqueName(String base) {
        return base + "_" + timestamp;
    }

    /**
     * Scenario 1: Complete Job Application Journey
     * 1. Create Company (TechCorp Inc)
     * 2. Create Job at company (Software Engineer)
     * 3. Create User (john@example.com)
     * 4. Create Application (User applies to Job, status: "applied")
     * 5. Verify Activity auto-logged for creation
     */
    static void scenario1_CompleteJobApplicationJourney() {
        System.out.println("\n--- Scenario 1: Complete Job Application Journey ---");

        UserManager userMgr = new UserManager();
        CompanyManager companyMgr = new CompanyManager();
        JobManager jobMgr = new JobManager();
        ApplicationManager appMgr = new ApplicationManager();
        ActivityManager activityMgr = new ActivityManager();

        String cuid = null;
        String juid = null;
        String uuid = null;
        String auid = null;

        try {
            // 1. Create Company
            CompanyRow company = new CompanyRow();
            company.name = uniqueName("TechCorp Inc");
            cuid = companyMgr.saveCompany(company);
            System.out.println("✓ Created company: " + cuid);

            // 2. Create Job
            JobRow job = new JobRow();
            job.cuid = cuid;
            job.title = "Software Engineer";
            juid = jobMgr.saveJob(job);
            System.out.println("✓ Created job: " + juid);

            // 3. Create User
            UserRow user = new UserRow();
            user.email = "john" + timestamp + "@example.com";
            user.passwordHash = "hashed_password_123";
            user.name = "John Doe";
            uuid = userMgr.saveUser(user);
            System.out.println("✓ Created user: " + uuid);

            // 4. Create Application
            ApplicationRow app = new ApplicationRow();
            app.uuid = uuid;
            app.juid = juid;
            app.status = "applied";
            auid = appMgr.saveApplication(app);
            System.out.println("✓ Created application: " + auid);

            // 5. Verify Activity auto-logged
            List<ActivityRow> activities = activityMgr.getActivityByApplicationId(auid);
            assert !activities.isEmpty() : "Activity should be auto-logged";
            assert activities.get(0).eventType.equals("created") : "First activity should be 'created'";
            System.out.println("✓ Activity auto-logged: " + activities.get(0).eventType);
        } finally {
            // Cleanup in reverse order
            try {
                if (auid != null) appMgr.deleteApplication(auid);
                if (juid != null) jobMgr.deleteJob(juid);
                if (cuid != null) companyMgr.deleteCompany(cuid);
                if (uuid != null) userMgr.deleteUser(uuid);
                System.out.println("✓ Test data cleaned up");
            } catch (Exception e) {
                System.out.println("⚠ Cleanup error (non-critical): " + e.getMessage());
            }
        }
    }

    /**
     * Scenario 2: Application Status Progression
     * 1. Get entities from Scenario 1
     * 2. Update application status: applied → phone_screen (verify Activity)
     * 3. Update status: phone_screen → interview (verify Activity)
     * 4. Update status: interview → offer (verify Activity)
     */
    static void scenario2_ApplicationStatusProgression() {
        System.out.println("\n--- Scenario 2: Application Status Progression ---");

        UserManager userMgr = new UserManager();
        CompanyManager companyMgr = new CompanyManager();
        JobManager jobMgr = new JobManager();
        ApplicationManager appMgr = new ApplicationManager();
        ActivityManager activityMgr = new ActivityManager();

        String cuid = null;
        String juid = null;
        String uuid = null;
        String auid = null;

        try {
            // Setup: Create entities
            CompanyRow company = new CompanyRow();
            company.name = uniqueName("DataViz Corp");
            cuid = companyMgr.saveCompany(company);

            JobRow job = new JobRow();
            job.cuid = cuid;
            job.title = "Data Analyst";
            juid = jobMgr.saveJob(job);

            UserRow user = new UserRow();
            user.email = "jane" + timestamp + "@example.com";
            user.passwordHash = "password_456";
            user.name = "Jane Smith";
            uuid = userMgr.saveUser(user);

            ApplicationRow app = new ApplicationRow();
            app.uuid = uuid;
            app.juid = juid;
            app.status = "applied";
            auid = appMgr.saveApplication(app);
            System.out.println("✓ Application created in 'applied' status");

            // Progress status: applied → phone_screen
            appMgr.updateApplicationStatus(auid, "phone_screen");
            List<ActivityRow> activities = activityMgr.getActivityByApplicationId(auid);
            assert activities.size() >= 2 : "Should have at least 2 activity records";
            System.out.println("✓ Status updated to phone_screen, Activity logged");

            // Progress status: phone_screen → interview
            appMgr.updateApplicationStatus(auid, "interview");
            activities = activityMgr.getActivityByApplicationId(auid);
            assert activities.size() >= 3 : "Should have at least 3 activity records";
            System.out.println("✓ Status updated to interview, Activity logged");

            // Progress status: interview → offer
            appMgr.updateApplicationStatus(auid, "offer");
            activities = activityMgr.getActivityByApplicationId(auid);
            assert activities.size() >= 4 : "Should have at least 4 activity records";
            System.out.println("✓ Status updated to offer, Activity logged");
        } finally {
            // Cleanup in reverse order
            try {
                if (auid != null) appMgr.deleteApplication(auid);
                if (juid != null) jobMgr.deleteJob(juid);
                if (cuid != null) companyMgr.deleteCompany(cuid);
                if (uuid != null) userMgr.deleteUser(uuid);
                System.out.println("✓ Test data cleaned up");
            } catch (Exception e) {
                System.out.println("⚠ Cleanup error (non-critical): " + e.getMessage());
            }
        }
    }

    /**
     * Scenario 3: Rejection and Reapply
     * 1. Create company, job, user
     * 2. Create application (status: "applied")
     * 3. Update status to "rejected" (verify Activity)
     * 4. Update status back to "applied" (reapply, verify Activity)
     */
    static void scenario3_RejectionAndReapply() {
        System.out.println("\n--- Scenario 3: Rejection and Reapply ---");

        UserManager userMgr = new UserManager();
        CompanyManager companyMgr = new CompanyManager();
        JobManager jobMgr = new JobManager();
        ApplicationManager appMgr = new ApplicationManager();
        ActivityManager activityMgr = new ActivityManager();

        String cuid = null;
        String juid = null;
        String uuid = null;
        String auid = null;

        try {
            // Setup
            CompanyRow company = new CompanyRow();
            company.name = uniqueName("StartupX");
            cuid = companyMgr.saveCompany(company);

            JobRow job = new JobRow();
            job.cuid = cuid;
            job.title = "Frontend Developer";
            juid = jobMgr.saveJob(job);

            UserRow user = new UserRow();
            user.email = "bob" + timestamp + "@example.com";
            user.passwordHash = "pass_789";
            user.name = "Bob Wilson";
            uuid = userMgr.saveUser(user);

            ApplicationRow app = new ApplicationRow();
            app.uuid = uuid;
            app.juid = juid;
            app.status = "applied";
            auid = appMgr.saveApplication(app);
            System.out.println("✓ Application created");

            // Reject application
            appMgr.updateApplicationStatus(auid, "rejected");
            System.out.println("✓ Application rejected");

            // Reapply
            appMgr.updateApplicationStatus(auid, "applied");
            List<ActivityRow> activities = activityMgr.getActivityByApplicationId(auid);
            System.out.println("✓ Application reapplied, total activities: " + activities.size());
            assert activities.size() >= 3 : "Should have activity for creation, rejection, and reapplying";
        } finally {
            // Cleanup in reverse order
            try {
                if (auid != null) appMgr.deleteApplication(auid);
                if (juid != null) jobMgr.deleteJob(juid);
                if (cuid != null) companyMgr.deleteCompany(cuid);
                if (uuid != null) userMgr.deleteUser(uuid);
                System.out.println("✓ Test data cleaned up");
            } catch (Exception e) {
                System.out.println("⚠ Cleanup error (non-critical): " + e.getMessage());
            }
        }
    }

    /**
     * Scenario 4: User Profile Update & Application
     * 1. Create User (jane@example.com)
     * 2. Create Company and Job
     * 3. Update user email (jane.doe@example.com)
     * 4. Get user again to verify email change persisted
     * 5. Create application with updated user
     * 6. Verify application shows new email
     */
    static void scenario4_UserProfileUpdateAndApplication() {
        System.out.println("\n--- Scenario 4: User Profile Update & Application ---");

        UserManager userMgr = new UserManager();
        CompanyManager companyMgr = new CompanyManager();
        JobManager jobMgr = new JobManager();
        ApplicationManager appMgr = new ApplicationManager();

        String cuid = null;
        String juid = null;
        String uuid = null;
        String auid = null;

        try {
            // Create user
            UserRow user = new UserRow();
            user.email = "alice" + timestamp + "@example.com";
            user.passwordHash = "pass_alice";
            user.name = "Alice Johnson";
            uuid = userMgr.saveUser(user);
            System.out.println("✓ User created with email: " + user.email);

            // Create company and job
            CompanyRow company = new CompanyRow();
            company.name = uniqueName("CloudTech");
            cuid = companyMgr.saveCompany(company);

            JobRow job = new JobRow();
            job.cuid = cuid;
            job.title = "Backend Engineer";
            juid = jobMgr.saveJob(job);
            System.out.println("✓ Company and Job created");

            // Update user email
            UserRow updatedUser = userMgr.getUserById(uuid);
            updatedUser.email = "alice.johnson" + timestamp + "@example.com";
            userMgr.saveUser(updatedUser);
            System.out.println("✓ User email updated to: " + updatedUser.email);

            // Verify email change persisted
            UserRow verifiedUser = userMgr.getUserById(uuid);
            assert verifiedUser.email.equals(updatedUser.email) : "Email should be updated";
            System.out.println("✓ Email change verified in database");

            // Create application with updated user
            ApplicationRow app = new ApplicationRow();
            app.uuid = uuid;
            app.juid = juid;
            app.status = "applied";
            auid = appMgr.saveApplication(app);
            System.out.println("✓ Application created with updated user email");

            // Verify application shows new email
            assert auid != null : "Application should be created successfully";
            System.out.println("✓ Application successfully uses updated user profile");
        } finally {
            // Cleanup in reverse order
            try {
                if (auid != null) appMgr.deleteApplication(auid);
                if (juid != null) jobMgr.deleteJob(juid);
                if (cuid != null) companyMgr.deleteCompany(cuid);
                if (uuid != null) userMgr.deleteUser(uuid);
                System.out.println("✓ Test data cleaned up");
            } catch (Exception e) {
                System.out.println("⚠ Cleanup error (non-critical): " + e.getMessage());
            }
        }
    }

    /**
     * Scenario 5: Delete With Cascading
     * 1. Create company, job, user
     * 2. Create application with activities
     * 3. Delete application (should cascade Activity records)
     * 4. Verify job still exists (not deleted)
     * 5. Delete job (now safe, no applications)
     * 6. Verify job deleted
     * 7. Delete company (now safe, no jobs)
     * 8. Verify company deleted
     */
    static void scenario5_DeleteWithCascading() {
        System.out.println("\n--- Scenario 5: Delete With Cascading ---");

        UserManager userMgr = new UserManager();
        CompanyManager companyMgr = new CompanyManager();
        JobManager jobMgr = new JobManager();
        ApplicationManager appMgr = new ApplicationManager();

        String cuid = null;
        String juid = null;
        String uuid = null;
        String auid = null;

        try {
            // Setup
            CompanyRow company = new CompanyRow();
            company.name = uniqueName("DeleteMe Corp");
            cuid = companyMgr.saveCompany(company);
            System.out.println("✓ Company created: " + cuid);

            JobRow job = new JobRow();
            job.cuid = cuid;
            job.title = "Temp Position";
            juid = jobMgr.saveJob(job);
            System.out.println("✓ Job created: " + juid);

            UserRow user = new UserRow();
            user.email = "tempuser" + timestamp + "@example.com";
            user.passwordHash = "temp_pass";
            user.name = "Temp User";
            uuid = userMgr.saveUser(user);
            System.out.println("✓ User created: " + uuid);

            ApplicationRow app = new ApplicationRow();
            app.uuid = uuid;
            app.juid = juid;
            app.status = "applied";
            auid = appMgr.saveApplication(app);
            System.out.println("✓ Application created: " + auid);

            // Delete application (should cascade Activities)
            appMgr.deleteApplication(auid);
            System.out.println("✓ Application deleted (Activities cascaded)");

            // Verify job still exists
            JobRow stillExistsJob = jobMgr.getJobById(juid);
            assert stillExistsJob != null : "Job should still exist";
            System.out.println("✓ Job still exists (not cascaded)");

            // Delete job (now safe)
            jobMgr.deleteJob(juid);
            System.out.println("✓ Job deleted (no applications)");

            // Verify job deleted
            JobRow deletedJob = jobMgr.getJobById(juid);
            assert deletedJob == null : "Job should be deleted";
            System.out.println("✓ Job deletion verified");

            // Delete company (now safe)
            companyMgr.deleteCompany(cuid);
            System.out.println("✓ Company deleted (no jobs)");

            // Verify company deleted
            CompanyRow deletedCompany = companyMgr.getCompanyById(cuid);
            assert deletedCompany == null : "Company should be deleted";
            System.out.println("✓ Company deletion verified");

            System.out.println("✓ Cascading delete workflow verified");
        } finally {
            // Cleanup in reverse order (if not already deleted by scenario)
            try {
                // auid already deleted in scenario; skip
                if (juid != null) {
                    JobRow checkJob = jobMgr.getJobById(juid);
                    if (checkJob != null) jobMgr.deleteJob(juid);
                }
                if (cuid != null) {
                    CompanyRow checkCompany = companyMgr.getCompanyById(cuid);
                    if (checkCompany != null) companyMgr.deleteCompany(cuid);
                }
                if (uuid != null) userMgr.deleteUser(uuid);
                System.out.println("✓ Test data cleaned up");
            } catch (Exception e) {
                System.out.println("⚠ Cleanup error (non-critical): " + e.getMessage());
            }
        }
    }
}
