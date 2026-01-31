package jobtracker.ui;

import jobtracker.dao.ReportDaoJdbc;

import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private final ReportDaoJdbc reports = new ReportDaoJdbc();

    public void run() {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                printMenu();
                String choice = sc.nextLine().trim();

                switch (choice) {
                    case "1":
                        showRowCounts();
                        break;
                    case "2":
                        listApplications(sc);
                        break;
                    case "3":
                        showApplicationActivity(sc);
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

    private void printMenu() {
        System.out.println();
        System.out.println("=== Job Tracker Console ===");
        System.out.println("1) Show row counts");
        System.out.println("2) List recent applications (joined view)");
        System.out.println("3) Show activity for an application (by auid)");
        System.out.println("0) Exit");
        System.out.print("> ");
    }

    private void showRowCounts() {
        List<String> counts = reports.getRowCounts();
        System.out.println("\nRow counts:");
        for (String line : counts) System.out.println("  " + line);
    }

    private void listApplications(Scanner sc) {
        System.out.print("How many? (e.g., 10): ");
        int limit = parseIntOrDefault(sc.nextLine(), 10);

        List<ReportDaoJdbc.ApplicationRow> rows = reports.listApplicationsDetailed(limit);

        System.out.println("\nRecent applications:");
        for (ReportDaoJdbc.ApplicationRow r : rows) {
            System.out.printf(
                "auid=%s | %s (%s) | %s | %s | %s | %s%n",
                r.auid,
                r.userName, r.userEmail,
                r.companyName,
                r.jobTitle,
                r.status,
                r.appliedAt
            );
        }
        if (rows.isEmpty()) System.out.println("(none)");
    }

    private void showApplicationActivity(Scanner sc) {
        System.out.print("Enter auid: ");
        String auid = sc.nextLine().trim();

        List<ReportDaoJdbc.ActivityRow> events = reports.listActivityForApplication(auid);

        System.out.println("\nActivity timeline:");
        for (ReportDaoJdbc.ActivityRow e : events) {
            System.out.printf(
                "%s | %s | %s -> %s | %s%n",
                e.eventTime,
                e.eventType,
                e.oldStatus,
                e.newStatus,
                (e.details == null ? "" : e.details)
            );
        }
        if (events.isEmpty()) System.out.println("(none / bad auid)");
    }

    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception ignored) { return def; }
    }
}