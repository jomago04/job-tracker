package jobtracker.service;

import jobtracker.business.*;
import jobtracker.dao.ReportDaoJdbc;
import jobtracker.dao.ReportDaoJdbc.*;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.List;

import static spark.Spark.*;

/**
 * Job Tracker REST API Service
 *
 * ============================================================================
 * HOW TO RUN THIS SERVICE
 * ============================================================================
 *
 * OPTION 1: Using Maven (Recommended for development)
 *   mvn compile exec:java -Dexec.mainClass=jobtracker.service.JobTrackerRestService
 *
 * OPTION 2: Build JAR and run
 *   mvn clean package
 *   java -jar target/job-tracker-1.0-SNAPSHOT.jar
 *
 * SERVICE WILL BE AVAILABLE AT: http://localhost:8080
 *
 * ============================================================================
 * API ENDPOINTS SUMMARY
 * ============================================================================
 *
 * USER ENDPOINTS:
 *   POST   /api/users                           - Create user
 *   GET    /api/users/{uuid}                    - Get user by ID
 *   GET    /api/users?limit=10&offset=0         - List all users (paginated)
 *   DELETE /api/users/{uuid}                    - Delete user
 *   GET    /api/users/email/{email}/exists      - Check if email exists
 *
 * COMPANY ENDPOINTS:
 *   POST   /api/companies                       - Create company
 *   GET    /api/companies/{cuid}                - Get company by ID
 *   GET    /api/companies?limit=10&offset=0     - List all companies (paginated)
 *   DELETE /api/companies/{cuid}                - Delete company
 *   GET    /api/companies/name/{name}/exists    - Check if company name exists
 *
 * JOB ENDPOINTS:
 *   POST   /api/jobs                            - Create job
 *   GET    /api/jobs/{juid}                     - Get job by ID
 *   GET    /api/jobs?limit=10&offset=0          - List all jobs (paginated)
 *   DELETE /api/jobs/{juid}                     - Delete job
 *   GET    /api/jobs/{juid}/exists              - Check if job exists
 *
 * APPLICATION ENDPOINTS:
 *   POST   /api/applications                    - Create application
 *   GET    /api/applications/{auid}             - Get application by ID
 *   GET    /api/applications?limit=10&offset=0  - List all applications (paginated)
 *   DELETE /api/applications/{auid}             - Delete application
 *   PUT    /api/applications/{auid}/status      - Update application status
 *   PUT    /api/applications/{auid}/notes       - Update application notes
 *   PUT    /api/applications/{auid}/source      - Update application source
 *   GET    /api/applications/{auid}/exists      - Check if application exists
 *   GET    /api/applications/user/{uuid}/job/{juid}/exists - Check duplicate application
 *
 * ACTIVITY ENDPOINTS:
 *   GET    /api/activities/application/{auid}   - Get activities for application
 *   GET    /api/activities?limit=10&offset=0&auid={filter} - List all activities
 *   GET    /api/activities/{actuid}             - Get activity by ID
 *   PUT    /api/activities/{actuid}/details     - Update activity details
 *
 * ============================================================================
 */
public class JobTrackerRestService {

    private static final Gson gson = new Gson();
    private static final UserManager userMgr = new UserManager();
    private static final CompanyManager companyMgr = new CompanyManager();
    private static final JobManager jobMgr = new JobManager();
    private static final ApplicationManager appMgr = new ApplicationManager();
    private static final ActivityManager activityMgr = new ActivityManager();

    public static void main(String[] args) {
        // Configure server
        port(8080);

        // Check database connectivity
        try {
            List<String> counts = new ReportDaoJdbc().getRowCounts();
            System.out.println("\n✓ Database connection successful!");
            System.out.println("Current row counts:");
            counts.forEach(c -> System.out.println("  " + c));
        } catch (Exception e) {
            System.err.println("✗ Database connection failed!");
            System.exit(1);
        }

        // =====================================================================
        // USER ENDPOINTS
        // =====================================================================

        /**
         * POST /api/users - Create a new user
         * Request body: {"email":"john@example.com","passwordHash":"hash123","name":"John Doe"}
         * Returns: User UUID as plain string
         * Status: 200 Created | 400 Bad Request | 409 Conflict | 500 Error
         *
         * curl -X POST http://localhost:8080/api/users \
         *   -H "Content-Type: application/json" \
         *   -d '{"email":"test@example.com","passwordHash":"hash123","name":"Test User"}'
         */
        post("/api/users", (req, res) -> {
            try {
                UserRow user = gson.fromJson(req.body(), UserRow.class);

                if (user.email == null || user.email.trim().isEmpty()) {
                    return ErrorHandler.badRequest(res, "Email is required");
                }
                if (user.passwordHash == null || user.passwordHash.trim().isEmpty()) {
                    return ErrorHandler.badRequest(res, "Password hash is required");
                }
                if (user.name == null || user.name.trim().isEmpty()) {
                    return ErrorHandler.badRequest(res, "Name is required");
                }

                if (userMgr.emailExists(user.email)) {
                    return ErrorHandler.conflict(res, "Email already exists");
                }

                res.status(201);
                res.type("application/json");
                String uuid = userMgr.saveUser(user);
                return ResponseBuilder.success(uuid);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/users/{uuid} - Get user by UUID
         * Returns: User object with all fields
         * Status: 200 Success | 404 Not Found | 500 Error
         *
         * curl http://localhost:8080/api/users/550e8400-e29b-41d4-a716-446655440000
         */
        get("/api/users/:uuid", (req, res) -> {
            try {
                res.type("application/json");
                String uuid = req.params(":uuid");
                UserRow user = userMgr.getUserById(uuid);

                if (user == null) {
                    return ErrorHandler.notFound(res, "User not found: " + uuid);
                }

                return ResponseBuilder.success(user);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/users?limit=10&offset=0 - List all users (paginated)
         * Query params: limit (default 10), offset (default 0)
         * Returns: Array of users
         * Status: 200 Success | 400 Bad Request | 500 Error
         *
         * curl "http://localhost:8080/api/users?limit=20&offset=0"
         */
        get("/api/users", (req, res) -> {
            try {
                res.type("application/json");
                int limit = Integer.parseInt(req.queryParamOrDefault("limit", "10"));
                int offset = Integer.parseInt(req.queryParamOrDefault("offset", "0"));

                if (limit < 1 || offset < 0) {
                    return ErrorHandler.badRequest(res, "limit must be >= 1, offset must be >= 0");
                }

                List<UserRow> users = userMgr.getAllUsers(limit, offset);
                return ResponseBuilder.success(users);
            } catch (NumberFormatException e) {
                return ErrorHandler.badRequest(res, "Invalid limit or offset format");
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * DELETE /api/users/{uuid} - Delete user
         * Returns: Empty response
         * Status: 204 No Content | 404 Not Found | 500 Error
         *
         * curl -X DELETE http://localhost:8080/api/users/550e8400-e29b-41d4-a716-446655440000
         */
        delete("/api/users/:uuid", (req, res) -> {
            try {
                res.type("application/json");
                String uuid = req.params(":uuid");

                if (userMgr.getUserById(uuid) == null) {
                    return ErrorHandler.notFound(res, "User not found: " + uuid);
                }

                userMgr.deleteUser(uuid);
                res.status(204);
                return "";
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/users/email/{email}/exists - Check if email exists
         * Returns: Boolean (true/false)
         * Status: 200 Success | 500 Error
         *
         * curl http://localhost:8080/api/users/email/test@example.com/exists
         */
        get("/api/users/email/:email/exists", (req, res) -> {
            try {
                res.type("application/json");
                String email = req.params(":email");
                boolean exists = userMgr.emailExists(email);
                return ResponseBuilder.success(exists);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        // =====================================================================
        // COMPANY ENDPOINTS
        // =====================================================================

        /**
         * POST /api/companies - Create company
         * Request: {"name":"TechCorp","industry":"Software","locationCity":"SF","locationState":"CA","companyUrl":"tech.com"}
         * Returns: Company UUID
         * Status: 201 Created | 400 Bad Request | 409 Conflict | 500 Error
         *
         * curl -X POST http://localhost:8080/api/companies \
         *   -H "Content-Type: application/json" \
         *   -d '{"name":"Acme Corp","industry":"Tech","locationCity":"NYC","locationState":"NY","companyUrl":"acme.com"}'
         */
        post("/api/companies", (req, res) -> {
            try {
                CompanyRow company = gson.fromJson(req.body(), CompanyRow.class);

                if (company.name == null || company.name.trim().isEmpty()) {
                    return ErrorHandler.badRequest(res, "Company name is required");
                }

                if (companyMgr.companyNameExists(company.name)) {
                    return ErrorHandler.conflict(res, "Company name already exists");
                }

                res.status(201);
                res.type("application/json");
                String cuid = companyMgr.saveCompany(company);
                return ResponseBuilder.success(cuid);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/companies/{cuid} - Get company by ID
         * Returns: Company object
         * Status: 200 Success | 404 Not Found | 500 Error
         *
         * curl http://localhost:8080/api/companies/550e8400-e29b-41d4-a716-446655440000
         */
        get("/api/companies/:cuid", (req, res) -> {
            try {
                res.type("application/json");
                String cuid = req.params(":cuid");
                CompanyRow company = companyMgr.getCompanyById(cuid);

                if (company == null) {
                    return ErrorHandler.notFound(res, "Company not found: " + cuid);
                }

                return ResponseBuilder.success(company);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/companies?limit=10&offset=0 - List companies (paginated)
         * Returns: Array of companies
         * Status: 200 Success | 400 Bad Request | 500 Error
         *
         * curl "http://localhost:8080/api/companies?limit=20&offset=0"
         */
        get("/api/companies", (req, res) -> {
            try {
                res.type("application/json");
                int limit = Integer.parseInt(req.queryParamOrDefault("limit", "10"));
                int offset = Integer.parseInt(req.queryParamOrDefault("offset", "0"));

                if (limit < 1 || offset < 0) {
                    return ErrorHandler.badRequest(res, "limit must be >= 1, offset must be >= 0");
                }

                List<CompanyRow> companies = companyMgr.getAllCompanies(limit, offset);
                return ResponseBuilder.success(companies);
            } catch (NumberFormatException e) {
                return ErrorHandler.badRequest(res, "Invalid limit or offset format");
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * DELETE /api/companies/{cuid} - Delete company
         * Status: 204 No Content | 404 Not Found | 409 Conflict (has jobs) | 500 Error
         *
         * curl -X DELETE http://localhost:8080/api/companies/550e8400-e29b-41d4-a716-446655440000
         */
        delete("/api/companies/:cuid", (req, res) -> {
            try {
                res.type("application/json");
                String cuid = req.params(":cuid");

                if (companyMgr.getCompanyById(cuid) == null) {
                    return ErrorHandler.notFound(res, "Company not found: " + cuid);
                }

                companyMgr.deleteCompany(cuid);
                res.status(204);
                return "";
            } catch (RuntimeException e) {
                if (e.getMessage().contains("Cannot delete company")) {
                    return ErrorHandler.conflict(res, "Cannot delete company with existing jobs");
                }
                return ErrorHandler.internalError(res, e);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/companies/name/{name}/exists - Check if company name exists
         * Returns: Boolean
         * Status: 200 Success | 500 Error
         *
         * curl http://localhost:8080/api/companies/name/Acme%20Corp/exists
         */
        get("/api/companies/name/:name/exists", (req, res) -> {
            try {
                res.type("application/json");
                String name = req.params(":name");
                boolean exists = companyMgr.companyNameExists(name);
                return ResponseBuilder.success(exists);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        // =====================================================================
        // JOB ENDPOINTS
        // =====================================================================

        /**
         * POST /api/jobs - Create job
         * Request: {"cuid":"...", "title":"Software Engineer", "employmentType":"full_time", "workType":"remote", "jobUrl":"...", "salaryMin":100000, "salaryMax":150000}
         * Returns: Job UUID
         * Status: 201 Created | 400 Bad Request | 404 Not Found | 500 Error
         *
         * curl -X POST http://localhost:8080/api/jobs \
         *   -H "Content-Type: application/json" \
         *   -d '{"cuid":"550e8400-e29b-41d4-a716-446655440000","title":"Senior Engineer","employmentType":"full_time","workType":"remote","jobUrl":"job-site.com"}'
         */
        post("/api/jobs", (req, res) -> {
            try {
                JobRow job = gson.fromJson(req.body(), JobRow.class);

                if (job.cuid == null || job.cuid.trim().isEmpty()) {
                    return ErrorHandler.badRequest(res, "Company ID (cuid) is required");
                }
                if (job.title == null || job.title.trim().isEmpty()) {
                    return ErrorHandler.badRequest(res, "Job title is required");
                }

                if (companyMgr.getCompanyById(job.cuid) == null) {
                    return ErrorHandler.notFound(res, "Company not found: " + job.cuid);
                }

                res.status(201);
                res.type("application/json");
                String juid = jobMgr.saveJob(job);
                return ResponseBuilder.success(juid);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/jobs/{juid} - Get job by ID
         * Returns: Job object
         * Status: 200 Success | 404 Not Found | 500 Error
         *
         * curl http://localhost:8080/api/jobs/550e8400-e29b-41d4-a716-446655440000
         */
        get("/api/jobs/:juid", (req, res) -> {
            try {
                res.type("application/json");
                String juid = req.params(":juid");
                JobRow job = jobMgr.getJobById(juid);

                if (job == null) {
                    return ErrorHandler.notFound(res, "Job not found: " + juid);
                }

                return ResponseBuilder.success(job);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/jobs?limit=10&offset=0 - List jobs (paginated)
         * Returns: Array of jobs
         * Status: 200 Success | 400 Bad Request | 500 Error
         *
         * curl "http://localhost:8080/api/jobs?limit=20&offset=0"
         */
        get("/api/jobs", (req, res) -> {
            try {
                res.type("application/json");
                int limit = Integer.parseInt(req.queryParamOrDefault("limit", "10"));
                int offset = Integer.parseInt(req.queryParamOrDefault("offset", "0"));

                if (limit < 1 || offset < 0) {
                    return ErrorHandler.badRequest(res, "limit must be >= 1, offset must be >= 0");
                }

                List<JobRow> jobs = jobMgr.getAllJobs(limit, offset);
                return ResponseBuilder.success(jobs);
            } catch (NumberFormatException e) {
                return ErrorHandler.badRequest(res, "Invalid limit or offset format");
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * DELETE /api/jobs/{juid} - Delete job
         * Status: 204 No Content | 404 Not Found | 409 Conflict (has applications) | 500 Error
         *
         * curl -X DELETE http://localhost:8080/api/jobs/550e8400-e29b-41d4-a716-446655440000
         */
        delete("/api/jobs/:juid", (req, res) -> {
            try {
                res.type("application/json");
                String juid = req.params(":juid");

                if (jobMgr.getJobById(juid) == null) {
                    return ErrorHandler.notFound(res, "Job not found: " + juid);
                }

                jobMgr.deleteJob(juid);
                res.status(204);
                return "";
            } catch (RuntimeException e) {
                if (e.getMessage().contains("Cannot delete job")) {
                    return ErrorHandler.conflict(res, "Cannot delete job with existing applications");
                }
                return ErrorHandler.internalError(res, e);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/jobs/{juid}/exists - Check if job exists
         * Returns: Boolean
         * Status: 200 Success | 500 Error
         *
         * curl http://localhost:8080/api/jobs/550e8400-e29b-41d4-a716-446655440000/exists
         */
        get("/api/jobs/:juid/exists", (req, res) -> {
            try {
                res.type("application/json");
                String juid = req.params(":juid");
                boolean exists = jobMgr.jobExists(juid);
                return ResponseBuilder.success(exists);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        // =====================================================================
        // APPLICATION ENDPOINTS
        // =====================================================================

        /**
         * POST /api/applications - Create application
         * Request: {"uuid":"...", "juid":"...", "status":"applied", "appliedAt":"2024-01-01T10:00:00", "source":"LinkedIn", "notes":"..."}
         * Returns: Application UUID
         * Status: 201 Created | 400 Bad Request | 404 Not Found | 409 Conflict | 500 Error
         *
         * curl -X POST http://localhost:8080/api/applications \
         *   -H "Content-Type: application/json" \
         *   -d '{"uuid":"...","juid":"...","status":"applied","source":"LinkedIn"}'
         */
        post("/api/applications", (req, res) -> {
            try {
                ApplicationRow app = gson.fromJson(req.body(), ApplicationRow.class);

                if (app.uuid == null || app.uuid.trim().isEmpty()) {
                    return ErrorHandler.badRequest(res, "User ID (uuid) is required");
                }
                if (app.juid == null || app.juid.trim().isEmpty()) {
                    return ErrorHandler.badRequest(res, "Job ID (juid) is required");
                }
                if (app.status == null || app.status.trim().isEmpty()) {
                    return ErrorHandler.badRequest(res, "Status is required");
                }

                if (userMgr.getUserById(app.uuid) == null) {
                    return ErrorHandler.notFound(res, "User not found: " + app.uuid);
                }
                if (jobMgr.getJobById(app.juid) == null) {
                    return ErrorHandler.notFound(res, "Job not found: " + app.juid);
                }

                if (appMgr.userJobApplicationExists(app.uuid, app.juid)) {
                    return ErrorHandler.conflict(res, "User already applied to this job");
                }

                res.status(201);
                res.type("application/json");
                String auid = appMgr.saveApplication(app);
                return ResponseBuilder.success(auid);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/applications/{auid} - Get application by ID
         * Returns: Application object with user/company/job details
         * Status: 200 Success | 404 Not Found | 500 Error
         *
         * curl http://localhost:8080/api/applications/550e8400-e29b-41d4-a716-446655440000
         */
        get("/api/applications/:auid", (req, res) -> {
            try {
                res.type("application/json");
                String auid = req.params(":auid");
                ApplicationRow app = appMgr.getApplicationById(auid);

                if (app == null) {
                    return ErrorHandler.notFound(res, "Application not found: " + auid);
                }

                return ResponseBuilder.success(app);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/applications?limit=10&offset=0 - List applications (paginated)
         * Returns: Array of applications with full details
         * Status: 200 Success | 400 Bad Request | 500 Error
         *
         * curl "http://localhost:8080/api/applications?limit=20&offset=0"
         */
        get("/api/applications", (req, res) -> {
            try {
                res.type("application/json");
                int limit = Integer.parseInt(req.queryParamOrDefault("limit", "10"));
                int offset = Integer.parseInt(req.queryParamOrDefault("offset", "0"));

                if (limit < 1 || offset < 0) {
                    return ErrorHandler.badRequest(res, "limit must be >= 1, offset must be >= 0");
                }

                List<ApplicationRow> apps = appMgr.getAllApplications(limit, offset);
                return ResponseBuilder.success(apps);
            } catch (NumberFormatException e) {
                return ErrorHandler.badRequest(res, "Invalid limit or offset format");
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * DELETE /api/applications/{auid} - Delete application (cascades activities)
         * Status: 204 No Content | 404 Not Found | 500 Error
         *
         * curl -X DELETE http://localhost:8080/api/applications/550e8400-e29b-41d4-a716-446655440000
         */
        delete("/api/applications/:auid", (req, res) -> {
            try {
                res.type("application/json");
                String auid = req.params(":auid");

                if (appMgr.getApplicationById(auid) == null) {
                    return ErrorHandler.notFound(res, "Application not found: " + auid);
                }

                appMgr.deleteApplication(auid);
                res.status(204);
                return "";
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * PUT /api/applications/{auid}/status - Update application status
         * Request: {"status":"phone_screen"} (valid: applied, phone_screen, interview, offer, rejected, withdrawn)
         * Returns: Updated application object
         * Status: 200 Success | 400 Bad Request | 404 Not Found | 500 Error
         *
         * curl -X PUT http://localhost:8080/api/applications/550e8400-e29b-41d4-a716-446655440000/status \
         *   -H "Content-Type: application/json" \
         *   -d '{"status":"interview"}'
         */
        put("/api/applications/:auid/status", (req, res) -> {
            try {
                res.type("application/json");
                String auid = req.params(":auid");

                ApplicationRow app = appMgr.getApplicationById(auid);
                if (app == null) {
                    return ErrorHandler.notFound(res, "Application not found: " + auid);
                }

                StatusUpdate update = gson.fromJson(req.body(), StatusUpdate.class);
                if (update.status == null || update.status.trim().isEmpty()) {
                    return ErrorHandler.badRequest(res, "Status is required");
                }

                ApplicationRow updated = appMgr.updateApplicationStatus(auid, update.status);
                return ResponseBuilder.success(updated);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * PUT /api/applications/{auid}/notes - Update application notes
         * Request: {"notes":"Good candidate"}
         * Returns: Empty response
         * Status: 204 No Content | 404 Not Found | 500 Error
         *
         * curl -X PUT http://localhost:8080/api/applications/550e8400-e29b-41d4-a716-446655440000/notes \
         *   -H "Content-Type: application/json" \
         *   -d '{"notes":"Good communications skills"}'
         */
        put("/api/applications/:auid/notes", (req, res) -> {
            try {
                res.type("application/json");
                String auid = req.params(":auid");

                if (appMgr.getApplicationById(auid) == null) {
                    return ErrorHandler.notFound(res, "Application not found: " + auid);
                }

                TextUpdate update = gson.fromJson(req.body(), TextUpdate.class);
                appMgr.updateApplicationNotes(auid, update.text);
                res.status(204);
                return "";
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * PUT /api/applications/{auid}/source - Update application source
         * Request: {"source":"LinkedIn"}
         * Returns: Empty response
         * Status: 204 No Content | 404 Not Found | 500 Error
         *
         * curl -X PUT http://localhost:8080/api/applications/550e8400-e29b-41d4-a716-446655440000/source \
         *   -H "Content-Type: application/json" \
         *   -d '{"source":"Indeed"}'
         */
        put("/api/applications/:auid/source", (req, res) -> {
            try {
                res.type("application/json");
                String auid = req.params(":auid");

                if (appMgr.getApplicationById(auid) == null) {
                    return ErrorHandler.notFound(res, "Application not found: " + auid);
                }

                TextUpdate update = gson.fromJson(req.body(), TextUpdate.class);
                appMgr.updateApplicationSource(auid, update.text);
                res.status(204);
                return "";
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/applications/{auid}/exists - Check if application exists
         * Returns: Boolean
         * Status: 200 Success | 500 Error
         *
         * curl http://localhost:8080/api/applications/550e8400-e29b-41d4-a716-446655440000/exists
         */
        get("/api/applications/:auid/exists", (req, res) -> {
            try {
                res.type("application/json");
                String auid = req.params(":auid");
                boolean exists = appMgr.applicationExists(auid);
                return ResponseBuilder.success(exists);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/applications/user/{uuid}/job/{juid}/exists - Check if user already applied to job
         * Returns: Boolean
         * Status: 200 Success | 500 Error
         *
         * curl http://localhost:8080/api/applications/user/550e8400-e29b-41d4-a716-446655440000/job/123e4567-e89b-12d3-a456-426614174000/exists
         */
        get("/api/applications/user/:uuid/job/:juid/exists", (req, res) -> {
            try {
                res.type("application/json");
                String uuid = req.params(":uuid");
                String juid = req.params(":juid");
                boolean exists = appMgr.userJobApplicationExists(uuid, juid);
                return ResponseBuilder.success(exists);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        // =====================================================================
        // ACTIVITY ENDPOINTS
        // =====================================================================

        /**
         * GET /api/activities/application/{auid} - Get all activities for an application
         * Returns: Array of activity records
         * Status: 200 Success | 500 Error
         *
         * curl http://localhost:8080/api/activities/application/550e8400-e29b-41d4-a716-446655440000
         */
        get("/api/activities/application/:auid", (req, res) -> {
            try {
                res.type("application/json");
                String auid = req.params(":auid");
                List<ActivityRow> activities = activityMgr.getActivityByApplicationId(auid);
                return ResponseBuilder.success(activities);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/activities?limit=10&offset=0&auid={filter} - List activities (paginated, optional filter)
         * Query params: limit (default 10), offset (default 0), auid (optional application id filter)
         * Returns: Array of activities
         * Status: 200 Success | 400 Bad Request | 500 Error
         *
         * curl "http://localhost:8080/api/activities?limit=20&offset=0"
         * curl "http://localhost:8080/api/activities?limit=10&offset=0&auid=550e8400-e29b-41d4-a716-446655440000"
         */
        get("/api/activities", (req, res) -> {
            try {
                res.type("application/json");
                int limit = Integer.parseInt(req.queryParamOrDefault("limit", "10"));
                int offset = Integer.parseInt(req.queryParamOrDefault("offset", "0"));
                String auidFilter = req.queryParamOrDefault("auid", null);

                if (limit < 1 || offset < 0) {
                    return ErrorHandler.badRequest(res, "limit must be >= 1, offset must be >= 0");
                }

                List<ActivityRow> activities = activityMgr.getAllActivities(limit, offset, auidFilter);
                return ResponseBuilder.success(activities);
            } catch (NumberFormatException e) {
                return ErrorHandler.badRequest(res, "Invalid limit or offset format");
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * GET /api/activities/{actuid} - Get activity by ID
         * Returns: Activity object
         * Status: 200 Success | 404 Not Found | 500 Error
         *
         * curl http://localhost:8080/api/activities/550e8400-e29b-41d4-a716-446655440000
         */
        get("/api/activities/:actuid", (req, res) -> {
            try {
                res.type("application/json");
                String actuid = req.params(":actuid");
                ActivityRow activity = activityMgr.getActivityById(actuid);

                if (activity == null) {
                    return ErrorHandler.notFound(res, "Activity not found: " + actuid);
                }

                return ResponseBuilder.success(activity);
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        /**
         * PUT /api/activities/{actuid}/details - Update activity details
         * Request: {"details":"Updated detail message"}
         * Returns: Empty response
         * Status: 204 No Content | 404 Not Found | 500 Error
         *
         * curl -X PUT http://localhost:8080/api/activities/550e8400-e29b-41d4-a716-446655440000/details \
         *   -H "Content-Type: application/json" \
         *   -d '{"details":"New detail message"}'
         */
        put("/api/activities/:actuid/details", (req, res) -> {
            try {
                res.type("application/json");
                String actuid = req.params(":actuid");

                if (activityMgr.getActivityById(actuid) == null) {
                    return ErrorHandler.notFound(res, "Activity not found: " + actuid);
                }

                TextUpdate update = gson.fromJson(req.body(), TextUpdate.class);
                activityMgr.updateActivityDetails(actuid, update.text);
                res.status(204);
                return "";
            } catch (Exception e) {
                return ErrorHandler.internalError(res, e);
            }
        });

        // Server startup message
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║          Job Tracker REST API Service Started              ║");
        System.out.println("║                  http://localhost:8080                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Helper classes for JSON deserialization
     */
    static class StatusUpdate {
        public String status;
    }

    static class TextUpdate {
        public String text;
    }
}
