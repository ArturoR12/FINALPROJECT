import java.sql.*;
import java.util.*;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Verificar conexión antes de cada operación
    private boolean checkConnection() {
        if (connection == null) {
            System.err.println("Error: La conexion a la base de datos es null");
            // Intentar reconectar
            this.connection = DatabaseConnection.getInstance().getConnection();
            return connection != null;
        }
        return true;
    }

    public boolean createUser(User user) {
        if (!checkConnection()) {
            System.err.println("No se puede crear usuario - sin conexion");
            return false;
        }

        String sql = "INSERT INTO users (username, password, nombre, apellido, telefono, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getNombre());
            stmt.setString(4, user.getApellido());
            stmt.setString(5, user.getTelefono());
            stmt.setString(6, user.getEmail());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en createUser: " + e.getMessage());
            if (e.getMessage().contains("Duplicate")) {
                throw new RuntimeException("El usuario ya existe");
            }
            return false;
        }
    }

    public User validateLogin(String username, String password) {
        if (!checkConnection()) {
            System.err.println("No se puede validar login - sin conexion");
            return null;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setNombre(rs.getString("nombre"));
                user.setApellido(rs.getString("apellido"));
                user.setTelefono(rs.getString("telefono"));
                user.setEmail(rs.getString("email"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error en validateLogin: " + e.getMessage());
        }
        return null;
    }

    // MÉTODO QUE FALTABA: getAllUsers()
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        if (!checkConnection()) {
            System.err.println("No se pueden obtener usuarios - sin conexion");
            return users;
        }

        String sql = "SELECT * FROM users ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setNombre(rs.getString("nombre"));
                user.setApellido(rs.getString("apellido"));
                user.setTelefono(rs.getString("telefono"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error en getAllUsers: " + e.getMessage());
        }
        return users;
    }

    // MÉTODO QUE FALTABA: updateUser()
    public boolean updateUser(User user) {
        if (!checkConnection()) {
            System.err.println("No se puede actualizar usuario - sin conexion");
            return false;
        }

        String sql = "UPDATE users SET nombre=?, apellido=?, telefono=?, email=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getNombre());
            stmt.setString(2, user.getApellido());
            stmt.setString(3, user.getTelefono());
            stmt.setString(4, user.getEmail());
            stmt.setInt(5, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en updateUser: " + e.getMessage());
            return false;
        }
    }

    // MÉTODO QUE FALTABA: deleteUser()
    public boolean deleteUser(int id) {
        if (!checkConnection()) {
            System.err.println("No se puede eliminar usuario - sin conexion");
            return false;
        }

        String sql = "DELETE FROM users WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en deleteUser: " + e.getMessage());
            return false;
        }
    }

    // MÉTODO QUE FALTABA: userExists()
    public boolean userExists(String username) {
        if (!checkConnection()) {
            System.err.println("No se puede verificar usuario - sin conexion");
            return false;
        }

        String sql = "SELECT id FROM users WHERE username=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("Error en userExists: " + e.getMessage());
            return false;
        }
    }

    // MÉTODO ADICIONAL: getUserById()
    public User getUserById(int id) {
        if (!checkConnection()) {
            System.err.println("No se puede obtener usuario - sin conexion");
            return null;
        }

        String sql = "SELECT * FROM users WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setNombre(rs.getString("nombre"));
                user.setApellido(rs.getString("apellido"));
                user.setTelefono(rs.getString("telefono"));
                user.setEmail(rs.getString("email"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error en getUserById: " + e.getMessage());
        }
        return null;
    }
}