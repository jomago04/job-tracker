package jobtracker.model;

import java.time.LocalDateTime;

public class Job {
    public String juid;
    public String cuid;
    public String title;
    public String employmentType; // internship, full_time, contract, part_time
    public String workType;       // remote, hybrid, on_site
    public String jobUrl;
    public Integer salaryMin;
    public Integer salaryMax;
    public LocalDateTime createdAt;

    public Job() {}
}