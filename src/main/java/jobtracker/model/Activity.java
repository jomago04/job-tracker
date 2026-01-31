package jobtracker.model;

import java.time.LocalDateTime;

public class Activity {
    public String actuid;
    public String auid;
    public String uuid;
    public String eventType;       // created, status_change, note_added, interview_scheduled, followup_set
    public String oldStatus;
    public String newStatus;
    public LocalDateTime eventTime;
    public String details;

    public Activity() {}
}