import java.util.List;

public class UserController {
    private UserDAO userDAO;
    private User currentUser;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) {
        User user = userDAO.validateLogin(username, password);
        if (user != null) {
            this.currentUser = user;
        }
        return user;
    }

    public boolean register(User user) {
        try {
            return userDAO.createUser(user);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean userExists(String username) {
        return userDAO.userExists(username);
    }
}