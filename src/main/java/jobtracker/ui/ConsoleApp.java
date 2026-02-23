package jobtracker.ui;

import jobtracker.dao.ReportDaoJdbc;
import jobtracker.business.UserManager;
import jobtracker.business.CompanyManager;
import jobtracker.business.JobManager;
import jobtracker.business.ApplicationManager;
import jobtracker.business.ActivityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private final ReportDaoJdbc reports = new ReportDaoJdbc();
    private final UserManager userManager = new UserManager();
    private final CompanyManager companyManager = new CompanyManager();
    private final JobManager jobManager = new JobManager();
    private final ApplicationManager applicationManager = new ApplicationManager();
    private final ActivityManager activityManager = new ActivityManager();

    // "last list" state so View-by-index works
    private List<ReportDaoJdbc.UserRow> lastUsers = new ArrayList<>();
    private List<ReportDaoJdbc.CompanyRow> lastCompanies = new ArrayList<>();
    private List<ReportDaoJdbc.JobRow> lastJobs = new ArrayList<>();
    private List<ReportDaoJdbc.ApplicationRow> lastApplications = new ArrayList<>();
    private List<ReportDaoJdbc.ActivityRow> lastActivities = new ArrayList<>();

    public void run() {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                printMainMenu();
                String choice = sc.nextLine().trim();

                switch (choice) {
                    case "1":
                        showRowCounts();
                        break;
                    case "2":
                        browseUsers(sc);
                        break;
                    case "3":
                        browseCompanies(sc);
                        break;
                    case "4":
                        browseJobs(sc);
                        break;
                    case "5":
                        browseApplications(sc);
                        break;
                    case "6":
                        browseActivities(sc);
                        break;
                    case "7":
                        runServiceTestSuite(sc);
                        break;
                    case "0":
                        System.out.println("Bye.");
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("=== Job Tracker Console ===");
        System.out.println("1) Show row counts");
        System.out.println("2) Browse users");
        System.out.println("3) Browse companies");
        System.out.println("4) Browse jobs");
        System.out.println("5) Browse applications");
        System.out.println("6) Browse activities");
        System.out.println("7) Run REST API Service Test Suite");
        System.out.println("0) Exit");
        System.out.print("> ");
    }

    private void showRowCounts() {
        List<String> counts = reports.getRowCounts();
        System.out.println("\nRow counts:");
        for (String line : counts) System.out.println("  " + line);
    }

    // -------------------------
    // USERS
    // -------------------------
    private void browseUsers(Scanner sc) {
        int limit = promptInt(sc, "Page size (e.g., 10): ", 10);
        int offset = 0;

        while (true) {
            System.out.println();
            System.out.println("=== Browse: user ===");
            System.out.println("Commands: [l]ist  [n]ext  [p]rev  [v]iew  [c]reate  [u]pdate  [d]elete  [b]ack");
            System.out.printf("Page: offset=%d limit=%d%n", offset, limit);
            System.out.print("> ");

            String cmd = sc.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "l":
                    lastUsers = userManager.getAllUsers(limit, offset);
                    printUserList(lastUsers);
                    break;
                case "n":
                    offset += limit;
                    lastUsers = userManager.getAllUsers(limit, offset);
                    if (lastUsers.isEmpty() && offset >= limit) offset -= limit;
                    printUserList(lastUsers);
                    break;
                case "p":
                    offset = Math.max(0, offset - limit);
                    lastUsers = userManager.getAllUsers(limit, offset);
                    printUserList(lastUsers);
                    break;
                case "v":
                    viewUserByIndex(sc);
                    break;
                case "c":
                    createUserFlow(sc);
                    break;
                case "u":
                    updateUserFlow(sc, lastUsers);
                    break;
                case "d":
                    deleteUserFlow(sc, lastUsers);
                    break;
                case "b":
                    return;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    private void printUserList(List<ReportDaoJdbc.UserRow> rows) {
        System.out.println("\nUsers:");
        if (rows.isEmpty()) { System.out.println("(none)"); return; }

        for (int i = 0; i < rows.size(); i++) {
            var r = rows.get(i);
            System.out.printf("[%d] uuid=%s | %s | %s%n",
                    i, safe(r.uuid), safe(r.name), safe(r.email));
        }
    }

    private void viewUserByIndex(Scanner sc) {
        if (lastUsers.isEmpty()) { System.out.println("Nothing listed yet. Use 'l' first."); return; }
        int idx = promptInt(sc, "Index: ", -1);
        if (idx < 0 || idx >= lastUsers.size()) { System.out.println("Bad index."); return; }

        var r = lastUsers.get(idx);
        System.out.println("\nUser details:");
        System.out.println("uuid: " + safe(r.uuid));
        System.out.println("email: " + safe(r.email));
        System.out.println("name: " + safe(r.name));
        System.out.println("password_hash: " + safeOrDash(r.passwordHash));
        System.out.println("created_at: " + safe(r.createdAt));
    }

    // -------------------------
    // COMPANIES
    // -------------------------
    private void browseCompanies(Scanner sc) {
        int limit = promptInt(sc, "Page size (e.g., 10): ", 10);
        int offset = 0;

        while (true) {
            System.out.println();
            System.out.println("=== Browse: company ===");
            System.out.println("Commands: [l]ist  [n]ext  [p]rev  [v]iew  [c]reate  [u]pdate  [d]elete  [b]ack");
            System.out.printf("Page: offset=%d limit=%d%n", offset, limit);
            System.out.print("> ");

            String cmd = sc.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "l":
                    lastCompanies = companyManager.getAllCompanies(limit, offset);
                    printCompanyList(lastCompanies);
                    break;
                case "n":
                    offset += limit;
                    lastCompanies = companyManager.getAllCompanies(limit, offset);
                    if (lastCompanies.isEmpty() && offset >= limit) offset -= limit;
                    printCompanyList(lastCompanies);
                    break;
                case "p":
                    offset = Math.max(0, offset - limit);
                    lastCompanies = companyManager.getAllCompanies(limit, offset);
                    printCompanyList(lastCompanies);
                    break;
                case "v":
                    viewCompanyByIndex(sc);
                    break;
                case "c":
                    createCompanyFlow(sc);
                    break;
                case "u":
                    updateCompanyFlow(sc, lastCompanies);
                    break;
                case "d":
                    deleteCompanyFlow(sc, lastCompanies);
                    break;
                case "b":
                    return;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    private void printCompanyList(List<ReportDaoJdbc.CompanyRow> rows) {
        System.out.println("\nCompanies:");
        if (rows.isEmpty()) { System.out.println("(none)"); return; }

        for (int i = 0; i < rows.size(); i++) {
            var r = rows.get(i);
            System.out.printf("[%d] cuid=%s | %s%n",
                    i, safe(r.cuid), safe(r.name));
        }
    }

    private void viewCompanyByIndex(Scanner sc) {
        if (lastCompanies.isEmpty()) { System.out.println("Nothing listed yet. Use 'l' first."); return; }
        int idx = promptInt(sc, "Index: ", -1);
        if (idx < 0 || idx >= lastCompanies.size()) { System.out.println("Bad index."); return; }

        var r = lastCompanies.get(idx);
        System.out.println("\nCompany details:");
        System.out.println("cuid: " + safe(r.cuid));
        System.out.println("name: " + safe(r.name));
        System.out.println("created_at: " + safe(r.createdAt));
    }

    // -------------------------
    // JOBS
    // -------------------------
    private void browseJobs(Scanner sc) {
        int limit = promptInt(sc, "Page size (e.g., 10): ", 10);
        int offset = 0;

        while (true) {
            System.out.println();
            System.out.println("=== Browse: job ===");
            System.out.println("Commands: [l]ist  [n]ext  [p]rev  [v]iew  [c]reate  [u]pdate  [d]elete  [b]ack");
            System.out.printf("Page: offset=%d limit=%d%n", offset, limit);
            System.out.print("> ");

            String cmd = sc.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "l":
                    lastJobs = jobManager.getAllJobs(limit, offset);
                    printJobList(lastJobs);
                    break;
                case "n":
                    offset += limit;
                    lastJobs = jobManager.getAllJobs(limit, offset);
                    if (lastJobs.isEmpty() && offset >= limit) offset -= limit;
                    printJobList(lastJobs);
                    break;
                case "p":
                    offset = Math.max(0, offset - limit);
                    lastJobs = jobManager.getAllJobs(limit, offset);
                    printJobList(lastJobs);
                    break;
                case "v":
                    viewJobByIndex(sc);
                    break;
                case "c":
                    createJobFlow(sc);
                    break;
                case "u":
                    updateJobFlow(sc, lastJobs);
                    break;
                case "d":
                    deleteJobFlow(sc, lastJobs);
                    break;
                case "b":
                    return;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    private void printJobList(List<ReportDaoJdbc.JobRow> rows) {
        System.out.println("\nJobs:");
        if (rows.isEmpty()) { System.out.println("(none)"); return; }

        for (int i = 0; i < rows.size(); i++) {
            var r = rows.get(i);
            System.out.printf("[%d] juid=%s | cuid=%s | %s%n",
                    i, safe(r.juid), safe(r.cuid), safe(r.title));
        }
    }

    private void viewJobByIndex(Scanner sc) {
        if (lastJobs.isEmpty()) { System.out.println("Nothing listed yet. Use 'l' first."); return; }
        int idx = promptInt(sc, "Index: ", -1);
        if (idx < 0 || idx >= lastJobs.size()) { System.out.println("Bad index."); return; }

        var r = lastJobs.get(idx);
        System.out.println("\nJob details:");
        System.out.println("juid: " + safe(r.juid));
        System.out.println("cuid: " + safe(r.cuid));
        System.out.println("title: " + safe(r.title));
        System.out.println("url: " + safeOrDash(r.url));
        System.out.println("created_at: " + safe(r.createdAt));
    }

    // -------------------------
    // APPLICATIONS (joined list view)
    // -------------------------
    private void browseApplications(Scanner sc) {
        int limit = promptInt(sc, "Page size (e.g., 10): ", 10);
        int offset = 0;

        while (true) {
            System.out.println();
            System.out.println("=== Browse: application ===");
            System.out.println("Commands: [l]ist  [n]ext  [p]rev  [v]iew  [c]reate  [u]pdate  [d]elete  [b]ack");
            System.out.printf("Page: offset=%d limit=%d%n", offset, limit);
            System.out.print("> ");

            String cmd = sc.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "l":
                    lastApplications = applicationManager.getAllApplications(limit, offset);
                    printApplicationList(lastApplications);
                    break;
                case "n":
                    offset += limit;
                    lastApplications = applicationManager.getAllApplications(limit, offset);
                    if (lastApplications.isEmpty() && offset >= limit) offset -= limit;
                    printApplicationList(lastApplications);
                    break;
                case "p":
                    offset = Math.max(0, offset - limit);
                    lastApplications = applicationManager.getAllApplications(limit, offset);
                    printApplicationList(lastApplications);
                    break;
                case "v":
                    viewApplicationByIndex(sc);
                    break;
                case "c":
                    createApplicationFlow(sc);
                    break;
                case "u":
                    updateApplicationFlow(sc, lastApplications);
                    break;
                case "d":
                    deleteApplicationFlow(sc, lastApplications);
                    break;
                case "b":
                    return;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    private void printApplicationList(List<ReportDaoJdbc.ApplicationRow> rows) {
        System.out.println("\nApplications:");
        if (rows.isEmpty()) { System.out.println("(none)"); return; }

        for (int i = 0; i < rows.size(); i++) {
            var r = rows.get(i);
            System.out.printf(
                    "[%d] auid=%s | %s (%s) | %s | %s | %s | %s%n",
                    i,
                    safe(r.auid),
                    safe(r.userName), safe(r.userEmail),
                    safe(r.companyName),
                    safe(r.jobTitle),
                    safe(r.status),
                    safe(r.appliedAt)
            );
        }
    }

    private void viewApplicationByIndex(Scanner sc) {
        if (lastApplications.isEmpty()) { System.out.println("Nothing listed yet. Use 'l' first."); return; }
        int idx = promptInt(sc, "Index: ", -1);
        if (idx < 0 || idx >= lastApplications.size()) { System.out.println("Bad index."); return; }

        var r = lastApplications.get(idx);
        System.out.println("\nApplication details:");
        System.out.println("auid: " + safe(r.auid));
        System.out.println("uuid(user): " + safeOrDash(r.uuid));
        System.out.println("juid(job): " + safeOrDash(r.juid));
        System.out.println("status: " + safe(r.status));
        System.out.println("applied_at: " + safe(r.appliedAt));
        System.out.println("source: " + safeOrDash(r.source));
        System.out.println("notes: " + safeOrDash(r.notes));
        System.out.println("last_updated_at: " + safe(r.lastUpdatedAt));
        System.out.println();
        System.out.println("user_name: " + safe(r.userName));
        System.out.println("user_email: " + safe(r.userEmail));
        System.out.println("company_name: " + safe(r.companyName));
        System.out.println("job_title: " + safe(r.jobTitle));
    }

    // -------------------------
    // ACTIVITIES
    // -------------------------
    private void browseActivities(Scanner sc) {
        int limit = promptInt(sc, "Page size (e.g., 10): ", 10);
        int offset = 0;

        System.out.print("Filter by auid? (blank = none): ");
        String auidFilter = sc.nextLine().trim();
        if (auidFilter.isEmpty()) auidFilter = null;

        while (true) {
            System.out.println();
            System.out.println("=== Browse: activity ===");
            System.out.println("Commands: [l]ist  [n]ext  [p]rev  [v]iew index  [b]ack");
            System.out.printf("Page: offset=%d limit=%d", offset, limit);
            if (auidFilter != null) System.out.print(" (auid=" + auidFilter + ")");
            System.out.println();
            System.out.print("> ");

            String cmd = sc.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "l":
                    lastActivities = reports.listActivities(limit, offset, auidFilter);
                    printActivityList(lastActivities);
                    break;
                case "n":
                    offset += limit;
                    lastActivities = reports.listActivities(limit, offset, auidFilter);
                    if (lastActivities.isEmpty() && offset >= limit) offset -= limit;
                    printActivityList(lastActivities);
                    break;
                case "p":
                    offset = Math.max(0, offset - limit);
                    lastActivities = reports.listActivities(limit, offset, auidFilter);
                    printActivityList(lastActivities);
                    break;
                case "v":
                    viewActivityByIndex(sc);
                    break;
                case "b":
                    return;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    private void printActivityList(List<ReportDaoJdbc.ActivityRow> rows) {
        System.out.println("\nActivities:");
        if (rows.isEmpty()) { System.out.println("(none)"); return; }

        for (int i = 0; i < rows.size(); i++) {
            var e = rows.get(i);
            System.out.printf(
                    "[%d] %s | auid=%s | %s | %s -> %s%n",
                    i,
                    safe(e.eventTime),
                    safe(e.auid),
                    safe(e.eventType),
                    safeOrDash(e.oldStatus),
                    safeOrDash(e.newStatus)
            );
        }
    }

    private void viewActivityByIndex(Scanner sc) {
        if (lastActivities.isEmpty()) { System.out.println("Nothing listed yet. Use 'l' first."); return; }
        int idx = promptInt(sc, "Index: ", -1);
        if (idx < 0 || idx >= lastActivities.size()) { System.out.println("Bad index."); return; }

        var e = lastActivities.get(idx);
        System.out.println("\nActivity details:");
        System.out.println("actuid: " + safe(e.actuid));
        System.out.println("auid: " + safe(e.auid));
        System.out.println("event_type: " + safe(e.eventType));
        System.out.println("old_status: " + safeOrDash(e.oldStatus));
        System.out.println("new_status: " + safeOrDash(e.newStatus));
        System.out.println("event_time: " + safe(e.eventTime));
        System.out.println("details: " + (e.details == null || e.details.trim().isEmpty() ? "—" : e.details));
    }

    // -------------------------
    // USER CRUD OPERATIONS
    // -------------------------

    private void createUserFlow(Scanner sc) {
        try {
            System.out.println("\n=== Create User ===");
            String email = promptEmail(sc);

            if (userManager.emailExists(email)) {
                System.out.println("ERROR: Email already exists.");
                return;
            }

            String password = promptNonEmpty(sc, "Password hash: ");
            String name = promptNonEmpty(sc, "Name: ");

            ReportDaoJdbc.UserRow user = new ReportDaoJdbc.UserRow();
            user.email = email;
            user.passwordHash = password;
            user.name = name;

            String newUuid = userManager.saveUser(user);
            System.out.println("SUCCESS: User created with UUID: " + newUuid);

        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void updateUserFlow(Scanner sc, List<ReportDaoJdbc.UserRow> lastUsers) {
        if (lastUsers.isEmpty()) {
            System.out.println("No users listed. Use 'l' first.");
            return;
        }

        int idx = promptInt(sc, "Index to update: ", -1);
        if (idx < 0 || idx >= lastUsers.size()) {
            System.out.println("Invalid index.");
            return;
        }

        var user = lastUsers.get(idx);
        System.out.println("\nCurrent user:");
        System.out.println("  email: " + safe(user.email));
        System.out.println("  name: " + safe(user.name));
        System.out.println("  password_hash: " + safeOrDash(user.passwordHash));

        System.out.print("Update which field? (email/password_hash/name/all/cancel): ");
        String field = sc.nextLine().trim().toLowerCase();

        try {
            switch (field) {
                case "email":
                    String newEmail = promptEmail(sc);
                    if (userManager.emailExists(newEmail) && !newEmail.equals(user.email)) {
                        System.out.println("ERROR: Email already exists.");
                        return;
                    }
                    user.email = newEmail;
                    userManager.saveUser(user);
                    System.out.println("User updated.");
                    break;
                case "password_hash":
                    String newPassword = promptNonEmpty(sc, "New password hash: ");
                    user.passwordHash = newPassword;
                    userManager.saveUser(user);
                    System.out.println("User updated.");
                    break;
                case "name":
                    String newName = promptNonEmpty(sc, "New name: ");
                    user.name = newName;
                    userManager.saveUser(user);
                    System.out.println("User updated.");
                    break;
                case "all":
                    String allEmail = promptEmail(sc);
                    if (userManager.emailExists(allEmail) && !allEmail.equals(user.email)) {
                        System.out.println("ERROR: Email already exists.");
                        return;
                    }
                    String allPassword = promptNonEmpty(sc, "Password hash: ");
                    String allName = promptNonEmpty(sc, "Name: ");
                    user.email = allEmail;
                    user.passwordHash = allPassword;
                    user.name = allName;
                    userManager.saveUser(user);
                    System.out.println("User updated.");
                    break;
                case "cancel":
                    System.out.println("Cancelled.");
                    break;
                default:
                    System.out.println("Unknown field.");
            }
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void deleteUserFlow(Scanner sc, List<ReportDaoJdbc.UserRow> lastUsers) {
        if (lastUsers.isEmpty()) {
            System.out.println("No users listed. Use 'l' first.");
            return;
        }

        int idx = promptInt(sc, "Index to delete: ", -1);
        if (idx < 0 || idx >= lastUsers.size()) {
            System.out.println("Invalid index.");
            return;
        }

        var user = lastUsers.get(idx);
        System.out.println("\nUser to delete: " + safe(user.name) + " (" + safe(user.email) + ")");

        if (!promptYesNo(sc, "Confirm deletion?")) {
            System.out.println("Cancelled.");
            return;
        }

        try {
            userManager.deleteUser(user.uuid);
            System.out.println("User deleted.");
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    // -------------------------
    // COMPANY CRUD OPERATIONS
    // -------------------------

    private void createCompanyFlow(Scanner sc) {
        try {
            System.out.println("\n=== Create Company ===");
            String name = promptNonEmpty(sc, "Company name: ");

            if (companyManager.companyNameExists(name)) {
                System.out.println("ERROR: Company name already exists.");
                return;
            }

            promptOptional(sc, "Industry: ");
            promptOptional(sc, "Location (city): ");
            promptOptional(sc, "Location (state): ");
            promptOptional(sc, "Company URL: ");

            ReportDaoJdbc.CompanyRow company = new ReportDaoJdbc.CompanyRow();
            company.name = name;

            String newCuid = companyManager.saveCompany(company);
            System.out.println("SUCCESS: Company created with ID: " + newCuid);

        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void updateCompanyFlow(Scanner sc, List<ReportDaoJdbc.CompanyRow> lastCompanies) {
        if (lastCompanies.isEmpty()) {
            System.out.println("No companies listed. Use 'l' first.");
            return;
        }

        int idx = promptInt(sc, "Index to update: ", -1);
        if (idx < 0 || idx >= lastCompanies.size()) {
            System.out.println("Invalid index.");
            return;
        }

        var company = lastCompanies.get(idx);
        System.out.println("\nCurrent company: " + safe(company.name));

        System.out.print("Update which field? (name/industry/location_city/location_state/url/all/cancel): ");
        String field = sc.nextLine().trim().toLowerCase();

        try {
            var fullCompany = reports.getCompanyByCuid(company.cuid);

            switch (field) {
                case "name":
                    String newName = promptNonEmpty(sc, "New name: ");
                    if (reports.companyNameExists(newName) && !newName.equals(company.name)) {
                        System.out.println("ERROR: Company name already exists.");
                        return;
                    }
                    reports.updateCompany(company.cuid, newName, fullCompany.name, null, null, null);
                    System.out.println("Company updated.");
                    break;
                case "cancel":
                    System.out.println("Cancelled.");
                    break;
                default:
                    System.out.println("Unknown field.");
            }
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void deleteCompanyFlow(Scanner sc, List<ReportDaoJdbc.CompanyRow> lastCompanies) {
        if (lastCompanies.isEmpty()) {
            System.out.println("No companies listed. Use 'l' first.");
            return;
        }

        int idx = promptInt(sc, "Index to delete: ", -1);
        if (idx < 0 || idx >= lastCompanies.size()) {
            System.out.println("Invalid index.");
            return;
        }

        var company = lastCompanies.get(idx);
        System.out.println("\nCompany to delete: " + safe(company.name));

        if (!promptYesNo(sc, "Confirm deletion?")) {
            System.out.println("Cancelled.");
            return;
        }

        try {
            reports.deleteCompany(company.cuid);
            System.out.println("Company deleted.");
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    // -------------------------
    // JOB CRUD OPERATIONS
    // -------------------------

    private void createJobFlow(Scanner sc) {
        try {
            System.out.println("\n=== Create Job ===");

            // Select company
            System.out.println("Select a company first...");
            int limit = promptInt(sc, "Page size for company list: ", 5);
            int offset = 0;
            List<ReportDaoJdbc.CompanyRow> companies = new ArrayList<>();

            while (true) {
                companies = reports.listCompanies(limit, offset);
                if (companies.isEmpty()) {
                    System.out.println("No companies found. Create a company first.");
                    return;
                }

                System.out.println("Companies:");
                for (int i = 0; i < companies.size(); i++) {
                    System.out.printf("[%d] %s%n", i, safe(companies.get(i).name));
                }

                String cmd = promptOptional(sc, "Enter company index (or 'n' for next, 'p' for prev): ");
                if (cmd.equals("n")) {
                    offset += limit;
                    continue;
                } else if (cmd.equals("p")) {
                    offset = Math.max(0, offset - limit);
                    continue;
                }

                try {
                    int idx = Integer.parseInt(cmd);
                    if (idx >= 0 && idx < companies.size()) {
                        break;
                    }
                    System.out.println("Invalid index.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }

            String cuid = companies.get(Integer.parseInt(promptOptional(sc, "Enter company index: "))).cuid;

            String title = promptNonEmpty(sc, "Job title: ");
            String employmentType = promptEnumChoice(sc, "Employment type", "internship", "full_time", "contract", "part_time");
            String workType = promptEnumChoice(sc, "Work type", "remote", "hybrid", "on_site");
            String jobUrl = promptOptional(sc, "Job URL: ");
            Integer salaryMin = promptPositiveInt(sc, "Salary min (optional)");
            Integer salaryMax = promptPositiveInt(sc, "Salary max (optional)");

            String newJuid = reports.createJob(cuid, title, employmentType, workType,
                    jobUrl.isEmpty() ? null : jobUrl, salaryMin, salaryMax);
            System.out.println("SUCCESS: Job created with ID: " + newJuid);

        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void updateJobFlow(Scanner sc, List<ReportDaoJdbc.JobRow> lastJobs) {
        if (lastJobs.isEmpty()) {
            System.out.println("No jobs listed. Use 'l' first.");
            return;
        }

        int idx = promptInt(sc, "Index to update: ", -1);
        if (idx < 0 || idx >= lastJobs.size()) {
            System.out.println("Invalid index.");
            return;
        }

        var job = lastJobs.get(idx);
        System.out.println("\nCurrent job: " + safe(job.title));

        if (!promptYesNo(sc, "Update this job?")) {
            System.out.println("Cancelled.");
            return;
        }

        try {
            String newTitle = promptNonEmpty(sc, "Job title: ");
            String employmentType = promptEnumChoice(sc, "Employment type", "internship", "full_time", "contract", "part_time");
            String workType = promptEnumChoice(sc, "Work type", "remote", "hybrid", "on_site");
            String jobUrl = promptOptional(sc, "Job URL: ");
            Integer salaryMin = promptPositiveInt(sc, "Salary min (optional)");
            Integer salaryMax = promptPositiveInt(sc, "Salary max (optional)");

            reports.updateJob(job.juid, newTitle, employmentType, workType,
                    jobUrl.isEmpty() ? null : jobUrl, salaryMin, salaryMax);
            System.out.println("Job updated.");
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void deleteJobFlow(Scanner sc, List<ReportDaoJdbc.JobRow> lastJobs) {
        if (lastJobs.isEmpty()) {
            System.out.println("No jobs listed. Use 'l' first.");
            return;
        }

        int idx = promptInt(sc, "Index to delete: ", -1);
        if (idx < 0 || idx >= lastJobs.size()) {
            System.out.println("Invalid index.");
            return;
        }

        var job = lastJobs.get(idx);
        System.out.println("\nJob to delete: " + safe(job.title));

        if (!promptYesNo(sc, "Confirm deletion?")) {
            System.out.println("Cancelled.");
            return;
        }

        try {
            reports.deleteJob(job.juid);
            System.out.println("Job deleted.");
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    // -------------------------
    // APPLICATION CRUD OPERATIONS
    // -------------------------

    private void createApplicationFlow(Scanner sc) {
        try {
            System.out.println("\n=== Create Application ===");

            // Select user
            System.out.println("Step 1: Select User");
            List<ReportDaoJdbc.UserRow> users = userManager.getAllUsers(5, 0);
            if (users.isEmpty()) {
                System.out.println("No users found. Create a user first.");
                return;
            }

            for (int i = 0; i < users.size(); i++) {
                System.out.printf("[%d] %s (%s)%n", i, safe(users.get(i).name), safe(users.get(i).email));
            }

            int userIdx = promptInt(sc, "Enter user index: ", -1);
            if (userIdx < 0 || userIdx >= users.size()) {
                System.out.println("Invalid index.");
                return;
            }
            String uuid = users.get(userIdx).uuid;

            // Select job
            System.out.println("\nStep 2: Select Job");
            List<ReportDaoJdbc.JobRow> jobs = jobManager.getAllJobs(5, 0);
            if (jobs.isEmpty()) {
                System.out.println("No jobs found. Create a job first.");
                return;
            }

            for (int i = 0; i < jobs.size(); i++) {
                System.out.printf("[%d] %s%n", i, safe(jobs.get(i).title));
            }

            int jobIdx = promptInt(sc, "Enter job index: ", -1);
            if (jobIdx < 0 || jobIdx >= jobs.size()) {
                System.out.println("Invalid index.");
                return;
            }
            String juid = jobs.get(jobIdx).juid;

            // Check for duplicates
            if (applicationManager.userJobApplicationExists(uuid, juid)) {
                System.out.println("ERROR: Application already exists for this user-job pair.");
                return;
            }

            // Application details
            String status = promptEnumChoice(sc, "Status", "applied", "phone_screen", "interview", "offer", "rejected", "withdrawn");
            String source = promptEnumChoice(sc, "Source", "linkedin", "handshake", "referral", "company_site", "other");
            String notes = promptOptional(sc, "Notes: ");

            ReportDaoJdbc.ApplicationRow app = new ReportDaoJdbc.ApplicationRow();
            app.uuid = uuid;
            app.juid = juid;
            app.status = status;
            app.source = source.isEmpty() ? null : source;
            app.notes = notes.isEmpty() ? null : notes;

            String newAuid = applicationManager.saveApplication(app);
            System.out.println("SUCCESS: Application created with ID: " + newAuid);
            System.out.println("Activity record auto-created.");

        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void updateApplicationFlow(Scanner sc, List<ReportDaoJdbc.ApplicationRow> lastApplications) {
        if (lastApplications.isEmpty()) {
            System.out.println("No applications listed. Use 'l' first.");
            return;
        }

        int idx = promptInt(sc, "Index to update: ", -1);
        if (idx < 0 || idx >= lastApplications.size()) {
            System.out.println("Invalid index.");
            return;
        }

        var app = lastApplications.get(idx);
        System.out.println("\nCurrent application:");
        System.out.println("  status: " + safe(app.status));
        System.out.println("  source: " + safeOrDash(app.source));
        System.out.println("  notes: " + safeOrDash(app.notes));

        System.out.print("Update which field? (status/source/notes/cancel): ");
        String field = sc.nextLine().trim().toLowerCase();

        try {
            switch (field) {
                case "status":
                    String newStatus = promptEnumChoice(sc, "New status", "applied", "phone_screen", "interview", "offer", "rejected", "withdrawn");
                    applicationManager.updateApplicationStatus(app.auid, newStatus);
                    System.out.println("Application status updated. Activity record auto-created.");
                    break;
                case "source":
                    String newSource = promptEnumChoice(sc, "New source", "linkedin", "handshake", "referral", "company_site", "other");
                    applicationManager.updateApplicationSource(app.auid, newSource);
                    System.out.println("Application source updated.");
                    break;
                case "notes":
                    String newNotes = promptOptional(sc, "New notes: ");
                    applicationManager.updateApplicationNotes(app.auid, newNotes.isEmpty() ? null : newNotes);
                    System.out.println("Application notes updated.");
                    break;
                case "cancel":
                    System.out.println("Cancelled.");
                    break;
                default:
                    System.out.println("Unknown field.");
            }
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void deleteApplicationFlow(Scanner sc, List<ReportDaoJdbc.ApplicationRow> lastApplications) {
        if (lastApplications.isEmpty()) {
            System.out.println("No applications listed. Use 'l' first.");
            return;
        }

        int idx = promptInt(sc, "Index to delete: ", -1);
        if (idx < 0 || idx >= lastApplications.size()) {
            System.out.println("Invalid index.");
            return;
        }

        var app = lastApplications.get(idx);
        System.out.println("\nApplication to delete:");
        System.out.println("  User: " + safe(app.userName));
        System.out.println("  Job: " + safe(app.jobTitle));
        System.out.println("  Status: " + safe(app.status));

        if (!promptYesNo(sc, "Confirm deletion?")) {
            System.out.println("Cancelled.");
            return;
        }

        try {
            applicationManager.deleteApplication(app.auid);
            System.out.println("Application deleted (Activity records cascaded).");
        } catch (RuntimeException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    // -------------------------
    // Helpers
    // -------------------------
    private int promptInt(Scanner sc, String prompt, int def) {
        System.out.print(prompt);
        String s = sc.nextLine();
        return parseIntOrDefault(s, def);
    }

    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception ignored) { return def; }
    }

    private String safe(Object o) {
        return (o == null) ? "" : String.valueOf(o);
    }

    private String safeOrDash(Object o) {
        String s = safe(o);
        return (s.trim().isEmpty()) ? "—" : s;
    }

    // -------------------------
    // PROMPTING HELPERS
    // -------------------------

    private String promptEmail(Scanner sc) {
        while (true) {
            System.out.print("Email: ");
            String email = sc.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("Email required.");
                continue;
            }
            if (!email.contains("@")) {
                System.out.println("Invalid email format (must contain @).");
                continue;
            }
            return email;
        }
    }

    private String promptNonEmpty(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String val = sc.nextLine().trim();
            if (!val.isEmpty()) return val;
            System.out.println("Field required.");
        }
    }

    private String promptOptional(Scanner sc, String prompt) {
        System.out.print(prompt + " (press Enter to skip): ");
        return sc.nextLine().trim();
    }

    private String promptEnumChoice(Scanner sc, String prompt, String... validValues) {
        while (true) {
            System.out.print(prompt + " (" + String.join("/", validValues) + "): ");
            String val = sc.nextLine().trim().toLowerCase();
            for (String v : validValues) {
                if (v.equalsIgnoreCase(val)) return v;
            }
            System.out.println("Invalid choice.");
        }
    }

    private Integer promptIntOrNull(Scanner sc, String prompt) {
        System.out.print(prompt + " (press Enter to skip): ");
        String val = sc.nextLine().trim();
        if (val.isEmpty()) return null;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Skipping.");
            return null;
        }
    }

    private Integer promptPositiveInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String val = sc.nextLine().trim();
            if (val.isEmpty()) return null;
            try {
                int num = Integer.parseInt(val);
                if (num >= 0) return num;
                System.out.println("Must be non-negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }

    private boolean promptYesNo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String resp = sc.nextLine().trim().toUpperCase();
            if (resp.equals("Y")) return true;
            if (resp.equals("N")) return false;
            System.out.println("Please enter Y or N.");
        }
    }

    /**
     * Runs the EndpointTestSuiteRunner directly in-process, displays output, and returns to menu.
     */
    private void runServiceTestSuite(Scanner sc) {
        System.out.println("\nRunning REST API Service Test Suite...\n");
        try {
            boolean passed = jobtracker.testing.EndpointTestSuiteRunner.runAll();
            if (passed) {
                System.out.println("\n✅ All tests passed! Returning to main menu. Press Enter to continue.");
            } else {
                System.out.println("\n❌ Some tests failed. See above for details. Press Enter to return to menu.");
            }
        } catch (Exception e) {
            System.out.println("Error running test suite: " + e.getMessage());
        }
        sc.nextLine();
    }

    public static void main(String[] args) {
        new ConsoleApp().run();
    }
}
