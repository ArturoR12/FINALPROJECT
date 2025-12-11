public class Product {
    private int id;
    private String nombre;
    private String marca;
    private String categoria;
    private double precio;
    private int cantidadDisponible;

    public Product() {}

    public Product(String nombre, String marca, String categoria, double precio, int cantidadDisponible) {
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidadDisponible = cantidadDisponible;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public Object[] toTableRow() {
        return new Object[]{
                nombre,
                marca != null ? marca : "N/A",
                categoria != null ? categoria : "General",
                precio,  // ← CAMBIO: SOLO EL NÚMERO, NO STRING
                cantidadDisponible
        };
    }

    @Override
    public String toString() {
        return String.format("Producto[id=%d, nombre=%s, precio=$%.2f, cantidad=%d]",
                id, nombre, precio, cantidadDisponible);
    }
}