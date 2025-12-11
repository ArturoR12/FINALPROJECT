import java.sql.*;
import java.util.*;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    private boolean checkConnection() {
        if (connection == null) {
            this.connection = DatabaseConnection.getInstance().getConnection();
            return connection != null;
        }
        return true;
    }

    public boolean createProduct(Product product) {
        if (!checkConnection()) {
            System.err.println("No hay conexion para crear producto");
            return false;
        }

        // Primero verificar si la tabla tiene la estructura correcta
        if (!verifyTableStructure()) {
            return false;
        }

        String sql = "INSERT INTO productos (nombre, marca, categoria, precio, cantidad_disponible) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getNombre());
            stmt.setString(2, product.getMarca());
            stmt.setString(3, product.getCategoria());
            stmt.setDouble(4, product.getPrecio());
            stmt.setInt(5, product.getCantidadDisponible());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
                System.out.println("OK - Producto creado: " + product.getNombre());
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("ERROR en createProduct: " + e.getMessage());
            System.err.println("SQL: " + sql);

            if (e.getMessage().contains("Unknown column")) {
                showTableStructure();
                System.err.println("SOLUCION: Ejecuta este SQL para corregir la tabla:");
                System.err.println("ALTER TABLE productos CHANGE nombre_producto nombre VARCHAR(100) NOT NULL;");
                System.err.println("O si la tabla no existe:");
                System.err.println(getCreateTableSQL());
            }
            return false;
        }
    }

    private boolean verifyTableStructure() {
        if (!checkConnection()) return false;

        try (Statement stmt = connection.createStatement()) {
            // Verificar si la tabla existe
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'productos'");
            if (!rs.next()) {
                System.out.println("La tabla 'productos' no existe. Creandola...");
                return createProductTable(stmt);
            }

            // Verificar columnas
            ResultSet columns = stmt.executeQuery("SHOW COLUMNS FROM productos");
            boolean hasNombre = false;
            boolean hasPrecio = false;
            boolean hasCantidad = false;

            while (columns.next()) {
                String colName = columns.getString("Field");
                if (colName.equalsIgnoreCase("nombre")) hasNombre = true;
                if (colName.equalsIgnoreCase("precio")) hasPrecio = true;
                if (colName.equalsIgnoreCase("cantidad_disponible")) hasCantidad = true;
            }

            if (!hasNombre || !hasPrecio || !hasCantidad) {
                System.out.println("La tabla 'productos' tiene estructura incorrecta. Corrigiendo...");
                return recreateTable(stmt);
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error verificando estructura: " + e.getMessage());
            return false;
        }
    }

    private boolean createProductTable(Statement stmt) throws SQLException {
        String sql = getCreateTableSQL();
        stmt.execute(sql);
        System.out.println("OK - Tabla 'productos' creada");
        return true;
    }

    private boolean recreateTable(Statement stmt) throws SQLException {
        // Crear tabla temporal con datos existentes
        try {
            stmt.execute("CREATE TABLE productos_temp LIKE productos");
            stmt.execute("INSERT INTO productos_temp SELECT * FROM productos");
        } catch (SQLException e) {
            // Si hay error de estructura, crear vacia
        }

        // Eliminar tabla vieja
        stmt.execute("DROP TABLE IF EXISTS productos");

        // Crear nueva con estructura correcta
        stmt.execute(getCreateTableSQL());

        // Intentar copiar datos
        try {
            stmt.execute("INSERT INTO productos (nombre, marca, categoria, precio, cantidad_disponible) " +
                    "SELECT nombre_producto, marca, categoria, precio, cantidad FROM productos_temp");
        } catch (SQLException e) {
            System.out.println("No se pudieron migrar los datos. Tabla creada vacia.");
        }

        // Eliminar temporal
        stmt.execute("DROP TABLE IF EXISTS productos_temp");

        System.out.println("OK - Tabla 'productos' recreada con estructura correcta");
        return true;
    }

    private String getCreateTableSQL() {
        return "CREATE TABLE productos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "marca VARCHAR(100), " +
                "categoria VARCHAR(100), " +
                "precio DECIMAL(10,2) NOT NULL, " +
                "cantidad_disponible INT NOT NULL, " +
                "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
    }

    private void showTableStructure() {
        if (!checkConnection()) return;

        System.out.println("\n=== ESTRUCTURA DE TABLA 'productos' ===");

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("DESCRIBE productos");
            System.out.println("Columnas:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("Field") + " : " + rs.getString("Type"));
            }
        } catch (SQLException e) {
            System.err.println("No se pudo obtener la estructura: " + e.getMessage());
        }
        System.out.println("=== FIN ESTRUCTURA ===\n");
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        if (!checkConnection()) {
            System.err.println("No hay conexion para obtener productos");
            return products;
        }

        String sql = "SELECT id, nombre, marca, categoria, precio, cantidad_disponible FROM productos ORDER BY id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setNombre(rs.getString("nombre"));
                product.setMarca(rs.getString("marca"));
                product.setCategoria(rs.getString("categoria"));
                product.setPrecio(rs.getDouble("precio"));
                product.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                products.add(product);
            }

            System.out.println("OK - Obtenidos " + products.size() + " productos");

        } catch (SQLException e) {
            System.err.println("Error en getAllProducts: " + e.getMessage());
            showTableStructure();
        }
        return products;
    }

    public boolean updateProduct(Product product) {
        if (!checkConnection()) return false;

        String sql = "UPDATE productos SET nombre=?, marca=?, categoria=?, precio=?, cantidad_disponible=? WHERE id=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getNombre());
            stmt.setString(2, product.getMarca());
            stmt.setString(3, product.getCategoria());
            stmt.setDouble(4, product.getPrecio());
            stmt.setInt(5, product.getCantidadDisponible());
            stmt.setInt(6, product.getId());

            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                System.out.println("OK - Producto actualizado: " + product.getNombre());
            }
            return result;

        } catch (SQLException e) {
            System.err.println("Error en updateProduct: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        if (!checkConnection()) return false;

        String sql = "DELETE FROM productos WHERE id=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                System.out.println("OK - Producto eliminado ID: " + id);
            }
            return result;

        } catch (SQLException e) {
            System.err.println("Error en deleteProduct: " + e.getMessage());
            return false;
        }
    }

    public Product getProductById(int id) {
        if (!checkConnection()) return null;

        String sql = "SELECT id, nombre, marca, categoria, precio, cantidad_disponible FROM productos WHERE id=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setNombre(rs.getString("nombre"));
                product.setMarca(rs.getString("marca"));
                product.setCategoria(rs.getString("categoria"));
                product.setPrecio(rs.getDouble("precio"));
                product.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                return product;
            }

        } catch (SQLException e) {
            System.err.println("Error en getProductById: " + e.getMessage());
        }
        return null;
    }
}