package jobtracker.business;

import jobtracker.dao.ReportDaoJdbc;
import jobtracker.dao.ReportDaoJdbc.CompanyRow;
import java.util.List;

public class CompanyManager {
    private ReportDaoJdbc dao = new ReportDaoJdbc();

    /**
     * Save company with smart logic: if ID is null/empty = insert, else = update
     */
    public String saveCompany(CompanyRow company) {
        // Validate required fields
        if (company.name == null || company.name.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required");
        }

        // Insert or Update logic
        if (company.cuid == null || company.cuid.trim().isEmpty()) {
            // New record - INSERT
            return dao.createCompany(
                company.name,
                null, // industry
                null, // location_city
                null, // location_state
                null  // company_url
            );
        } else {
            // Existing record - UPDATE
            dao.updateCompany(
                company.cuid,
                company.name,
                null, // industry
                null, // location_city
                null, // location_state
                null  // company_url
            );
            return company.cuid;
        }
    }

    public CompanyRow getCompanyById(String cuid) {
        if (cuid == null || cuid.trim().isEmpty()) {
            return null;
        }
        return dao.getCompanyByCuid(cuid);
    }

    public List<CompanyRow> getAllCompanies(int limit, int offset) {
        return dao.listCompanies(limit, offset);
    }

    public void deleteCompany(String cuid) {
        if (cuid == null || cuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Company ID is required");
        }
        dao.deleteCompany(cuid);
    }

    public boolean companyNameExists(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return dao.companyNameExists(name);
    }
}
