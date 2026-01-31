package jobtracker.model;

import java.time.LocalDateTime;

public class Company {
    public String cuid;
    public String name;
    public String industry;
    public String locationCity;
    public String locationState;
    public String companyUrl;
    public LocalDateTime createdAt;

    public Company() {}

    public Company(String cuid, String name) {
        this.cuid = cuid;
        this.name = name;
    }
}