package jobtracker.business;

import jobtracker.dao.ReportDaoJdbc;
import jobtracker.dao.ReportDaoJdbc.UserRow;
import java.util.List;

public class UserManager {
    private ReportDaoJdbc dao = new ReportDaoJdbc();

    /**
     * Save user with smart logic: if ID is null/empty = insert, else = update
     */
    public String saveUser(UserRow user) {
        // Validate required fields
        if (user.email == null || user.email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.passwordHash == null || user.passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash is required");
        }
        if (user.name == null || user.name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        // Check email format (basic validation)
        if (!user.email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Insert or Update logic
        if (user.uuid == null || user.uuid.trim().isEmpty()) {
            // New record - INSERT
            return dao.createUser(user.email, user.passwordHash, user.name);
        } else {
            // Existing record - UPDATE
            dao.updateUser(user.uuid, user.email, user.passwordHash, user.name);
            return user.uuid;
        }
    }

    /**
     * Get user by ID
     */
    public UserRow getUserById(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return null;
        }
        return dao.getUserByUuid(uuid);
    }

    /**
     * Get all users with pagination
     */
    public List<UserRow> getAllUsers(int limit, int offset) {
        return dao.listUsers(limit, offset);
    }

    /**
     * Delete user by ID
     */
    public void deleteUser(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException("UUID is required");
        }
        dao.deleteUser(uuid);
    }

    /**
     * Check if email exists (excluding current user if updating)
     */
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return dao.userEmailExists(email);
    }
}
