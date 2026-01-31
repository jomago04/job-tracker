package jobtracker.model;

import java.time.LocalDateTime;

public class User {
    public String uuid;
    public String email;
    public String passwordHash;
    public String name;
    public LocalDateTime createdAt;

    public User() {}

    public User(String uuid, String email, String passwordHash, String name) {
        this.uuid = uuid;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
    }
}