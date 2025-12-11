import java.sql.*;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private static final String JDBC_URL = "jdbc:mysql://almacenitla-db-itla-3837.e.aivencloud.com:25037/almacenitlafinal?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "avnadmin";
    private static final String PASSWORD = "AVNS_pPa2xcIg1UbjOzcsoMg";

    private DatabaseConnection() {
        connect();
    }

    private void connect() {
        try {
            System.out.println("=== INICIANDO CONEXION A BASE DE DATOS ===");

            // 1. Cargar driver MySQL
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("OK - Driver MySQL cargado correctamente");
            } catch (ClassNotFoundException e) {
                System.err.println("ERROR: Driver MySQL no encontrado");
                System.err.println("SOLUCION: Descarga mysql-connector-j-8.x.x.jar de:");
                System.err.println("          https://dev.mysql.com/downloads/connector/j/");
                System.err.println("          y agregalo como libreria a tu proyecto");
                return;
            }

            // 2. Establecer conexion
            System.out.println("Conectando a: " + JDBC_URL);
            this.connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("OK - Conexion exitosa a la base de datos");

            // 3. Crear/verificar tablas
            createTablesIfNotExists();
            System.out.println("=== CONEXION ESTABLECIDA CORRECTAMENTE ===\n");

        } catch (SQLException e) {
            System.err.println("ERROR DE CONEXION SQL: " + e.getMessage());
            System.err.println("Verifica:");
            System.err.println("1. Tu base de datos en Aiven este ACTIVA");
            System.err.println("2. Las credenciales sean correctas");
            System.err.println("3. No haya bloqueo de firewall (puerto 25037)");
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            } else {
                System.out.println("Reconectando a la base de datos...");
                connect();
                return connection;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener conexion: " + e.getMessage());
            return null;
        }
    }

    private void createTablesIfNotExists() {
        if (connection == null) {
            System.err.println("No hay conexion para crear las tablas");
            return;
        }

        try (Statement statement = connection.createStatement()) {
            // 1. TABLA DE USUARIOS
            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "nombre VARCHAR(100) NOT NULL, " +
                    "apellido VARCHAR(100) NOT NULL, " +
                    "telefono VARCHAR(15) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            statement.execute(usersTable);
            System.out.println("OK - Tabla 'users' verificada/creada");

            // 2. TABLA DE PRODUCTOS - ESTRUCTURA CORREGIDA
            // Primero verificar si existe con estructura incorrecta
            try {
                ResultSet rs = statement.executeQuery("SHOW TABLES LIKE 'productos'");
                if (rs.next()) {
                    // La tabla existe, verificar columnas
                    ResultSet columns = statement.executeQuery("SHOW COLUMNS FROM productos");
                    boolean hasNombre = false;
                    while (columns.next()) {
                        if (columns.getString("Field").equalsIgnoreCase("nombre")) {
                            hasNombre = true;
                            break;
                        }
                    }

                    if (!hasNombre) {
                        System.out.println("ADVERTENCIA: Tabla 'productos' existe pero sin columna 'nombre'");
                        System.out.println("Renombrando a 'productos_vieja' y creando nueva...");
                        statement.execute("RENAME TABLE productos TO productos_vieja");
                    }
                }
            } catch (SQLException e) {
                // Ignorar errores
            }

            String productsTable = "CREATE TABLE IF NOT EXISTS productos (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre VARCHAR(100) NOT NULL, " +
                    "marca VARCHAR(100), " +
                    "categoria VARCHAR(100), " +
                    "precio DECIMAL(10,2) NOT NULL, " +
                    "cantidad_disponible INT NOT NULL, " +
                    "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            statement.execute(productsTable);
            System.out.println("OK - Tabla 'productos' verificada/creada con estructura correcta");

            // 3. INSERTAR DATOS DE PRUEBA
            insertDatosDePrueba(statement);

        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void insertDatosDePrueba(Statement statement) throws SQLException {
        // Verificar si ya hay usuarios
        ResultSet rsUsers = statement.executeQuery("SELECT COUNT(*) FROM users");
        rsUsers.next();
        if (rsUsers.getInt(1) == 0) {
            statement.execute("INSERT INTO users (username, password, nombre, apellido, telefono, email) VALUES " +
                    "('admin', '123456', 'Administrador', 'Sistema', '809-123-4567', 'admin@almacen.com')");
            statement.execute("INSERT INTO users (username, password, nombre, apellido, telefono, email) VALUES " +
                    "('juan', '123456', 'Juan', 'Perez', '809-987-6543', 'juan@empresa.com')");
            System.out.println("OK - Usuarios de prueba insertados");
        }

        // Verificar si ya hay productos
        ResultSet rsProducts = statement.executeQuery("SELECT COUNT(*) FROM productos");
        rsProducts.next();
        if (rsProducts.getInt(1) == 0) {
            statement.execute("INSERT INTO productos (nombre, marca, categoria, precio, cantidad_disponible) VALUES " +
                    "('Laptop Dell Inspiron 15', 'Dell', 'Computadoras', 899.99, 10)");
            statement.execute("INSERT INTO productos (nombre, marca, categoria, precio, cantidad_disponible) VALUES " +
                    "('Mouse Inalambrico Logitech M170', 'Logitech', 'Accesorios', 15.99, 50)");
            statement.execute("INSERT INTO productos (nombre, marca, categoria, precio, cantidad_disponible) VALUES " +
                    "('Teclado Mecanico Redragon Kumara', 'Redragon', 'Accesorios', 45.50, 25)");
            System.out.println("OK - Productos de prueba insertados");
        }
    }
}