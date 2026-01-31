package jobtracker.dao;

import jobtracker.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    void create(User u);
    Optional<User> getById(String uuid);
    Optional<User> getByEmail(String email);
    List<User> listAll();
    void update(User u);
    void delete(String uuid);
}