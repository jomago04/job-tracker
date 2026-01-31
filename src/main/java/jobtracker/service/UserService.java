package jobtracker.service;

import jobtracker.dao.UserDao;
import jobtracker.model.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void register(User u) {
        // later: validate email format, password rules, etc.
        userDao.create(u);
    }

    public Optional<User> findByEmail(String email) {
        return userDao.getByEmail(email);
    }

    public List<User> listUsers() {
        return userDao.listAll();
    }
}