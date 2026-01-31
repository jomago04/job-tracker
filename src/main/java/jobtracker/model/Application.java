package jobtracker.model;

import java.time.LocalDateTime;

public class Application {
    public String auid;
    public String uuid;
    public String juid;
    public String status;          // applied, phone_screen, interview, offer, rejected, withdrawn
    public LocalDateTime appliedAt;
    public String source;          // linkedin, handshake, referral, company_site, other
    public String notes;
    public LocalDateTime lastUpdatedAt;

    public Application() {}
}