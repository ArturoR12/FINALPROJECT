import java.util.List;

public class ProductController {
    private ProductDAO productDAO;

    public ProductController() {
        this.productDAO = new ProductDAO();
    }

    public boolean createProduct(Product product) {
        if (product.getNombre() == null || product.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (product.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (product.getCantidadDisponible() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }

        return productDAO.createProduct(product);
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public boolean updateProduct(Product product) {
        if (product.getId() <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }
        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }
        return productDAO.deleteProduct(id);
    }

    public Product getProductById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }
        return productDAO.getProductById(id);
    }
}