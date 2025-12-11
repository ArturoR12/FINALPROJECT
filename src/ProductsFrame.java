import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ProductsFrame extends JFrame {
    private User currentUser;
    private ProductController productController;
    private JTable table;
    private DefaultTableModel tableModel;

    public ProductsFrame(User user, ProductController productController) {
        this.currentUser = user;
        this.productController = productController;
        setupUI();
        loadProducts();
    }

    private void setupUI() {
        setTitle("Gestion de Productos - Almacen ITLA");
        setSize(1000, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("PRODUCTOS DE ALMACEN", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        lblTitle.setForeground(new Color(30, 144, 255));
        panel.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Nombre", "Marca", "Categoria", "Precio", "Cantidad Disponible"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Double.class;
                if (columnIndex == 4) return Integer.class;
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        // Renderizador para la columna de Precio (columna 3)
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            private java.text.DecimalFormat format = new java.text.DecimalFormat("$#,##0.00");

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                    boolean isSelected, boolean hasFocus,
                                                                    int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value instanceof Number) {
                    setText(format.format(value));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else if (value == null) {
                    setText("$0.00");
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    try {
                        double num = Double.parseDouble(value.toString());
                        setText(format.format(num));
                        setHorizontalAlignment(SwingConstants.RIGHT);
                    } catch (NumberFormatException e) {
                        setText(value.toString());
                    }
                }

                return c;
            }
        });

        // Renderizador para la columna de Cantidad (columna 4)
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                    boolean isSelected, boolean hasFocus,
                                                                    int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(30, 144, 255));
        header.setForeground(Color.WHITE);

        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnNew = createButton("NUEVO PRODUCTO", new Color(60, 179, 113));
        btnNew.addActionListener(e -> newProduct());

        JButton btnEdit = createButton("EDITAR", new Color(255, 165, 0));
        btnEdit.addActionListener(e -> editProduct());

        JButton btnDelete = createButton("ELIMINAR", new Color(220, 20, 60));
        btnDelete.addActionListener(e -> deleteProduct());

        JButton btnRefresh = createButton("ACTUALIZAR", new Color(70, 130, 180));
        btnRefresh.addActionListener(e -> loadProducts());

        JButton btnBack = createButton("VOLVER AL MENU", new Color(128, 128, 128));
        btnBack.addActionListener(e -> goBack());

        buttonPanel.add(btnNew);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void loadProducts() {
        tableModel.setRowCount(0);

        try {
            List<Product> products = productController.getAllProducts();

            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay productos registrados.\nHaz clic en 'NUEVO PRODUCTO' para agregar el primero.",
                        "Informacion",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Product product : products) {
                    tableModel.addRow(product.toTableRow());
                }

                double valorTotal = products.stream()
                        .mapToDouble(p -> p.getPrecio() * p.getCantidadDisponible())
                        .sum();

                JOptionPane.showMessageDialog(this,
                        String.format("Cargados %d productos\nValor total en inventario: $%.2f",
                                products.size(), valorTotal),
                        "Productos Cargados",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void newProduct() {
        showProductForm(null);
    }

    private void editProduct() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un producto de la tabla para editar.",
                    "Seleccion Requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Product> products = productController.getAllProducts();
            if (selectedRow < products.size()) {
                Product selectedProduct = products.get(selectedRow);
                showProductForm(selectedProduct);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un producto para eliminar.",
                    "Seleccion Requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Product> products = productController.getAllProducts();
            if (selectedRow < products.size()) {
                Product productToDelete = products.get(selectedRow);

                int confirm = JOptionPane.showConfirmDialog(this,
                        String.format("Esta seguro que desea eliminar el producto?\n\n" +
                                        "Nombre: %s\n" +
                                        "Precio: $%.2f\n" +
                                        "Cantidad: %d",
                                productToDelete.getNombre(),
                                productToDelete.getPrecio(),
                                productToDelete.getCantidadDisponible()),
                        "Confirmar Eliminacion",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (productController.deleteProduct(productToDelete.getId())) {
                        JOptionPane.showMessageDialog(this,
                                "Producto eliminado exitosamente.",
                                "Exito",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadProducts();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProductForm(Product product) {
        boolean isEdit = (product != null);
        String title = isEdit ? "EDITAR PRODUCTO" : "NUEVO PRODUCTO";

        JTextField txtNombre = new JTextField(25);
        JTextField txtMarca = new JTextField(25);
        JTextField txtCategoria = new JTextField(25);
        JTextField txtPrecio = new JTextField(25);
        JTextField txtCantidad = new JTextField(25);

        if (isEdit) {
            txtNombre.setText(product.getNombre());
            txtMarca.setText(product.getMarca());
            txtCategoria.setText(product.getCategoria());
            txtPrecio.setText(String.valueOf(product.getPrecio()));
            txtCantidad.setText(String.valueOf(product.getCantidadDisponible()));
        }

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Nombre del Producto:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Marca:"));
        formPanel.add(txtMarca);
        formPanel.add(new JLabel("Categoria:"));
        formPanel.add(txtCategoria);
        formPanel.add(new JLabel("Precio ($):"));
        formPanel.add(txtPrecio);
        formPanel.add(new JLabel("Cantidad Disponible:"));
        formPanel.add(txtCantidad);

        Object[] options;
        if (isEdit) {
            options = new Object[]{"GUARDAR CAMBIOS", "ELIMINAR PRODUCTO", "CANCELAR"};
        } else {
            options = new Object[]{"CREAR PRODUCTO", "CANCELAR"};
        }

        int option = JOptionPane.showOptionDialog(this,
                formPanel,
                title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (isEdit) {
            switch (option) {
                case 0:
                    saveProductChanges(product, txtNombre, txtMarca, txtCategoria, txtPrecio, txtCantidad);
                    break;
                case 1:
                    deleteProduct();
                    break;
            }
        } else {
            if (option == 0) {
                createNewProduct(txtNombre, txtMarca, txtCategoria, txtPrecio, txtCantidad);
            }
        }
    }

    private void saveProductChanges(Product product, JTextField... fields) {
        try {
            if (fields[0].getText().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es obligatorio");
            }

            product.setNombre(fields[0].getText().trim());
            product.setMarca(fields[1].getText().trim());
            product.setCategoria(fields[2].getText().trim());
            product.setPrecio(Double.parseDouble(fields[3].getText().trim()));
            product.setCantidadDisponible(Integer.parseInt(fields[4].getText().trim()));

            if (productController.updateProduct(product)) {
                JOptionPane.showMessageDialog(this,
                        "Producto actualizado exitosamente.",
                        "Exito",
                        JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Precio y Cantidad deben ser numeros validos.",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error de Validacion",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createNewProduct(JTextField... fields) {
        try {
            if (fields[0].getText().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del producto es obligatorio");
            }

            Product newProduct = new Product(
                    fields[0].getText().trim(),
                    fields[1].getText().trim(),
                    fields[2].getText().trim(),
                    Double.parseDouble(fields[3].getText().trim()),
                    Integer.parseInt(fields[4].getText().trim())
            );

            if (productController.createProduct(newProduct)) {
                JOptionPane.showMessageDialog(this,
                        "Producto creado exitosamente.",
                        "Exito",
                        JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Precio y Cantidad deben ser numeros validos.\n" +
                            "Ejemplo: 99.99 para precio, 10 para cantidad",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error de Validacion",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al crear producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        new MenuPrincipalFrame(currentUser).setVisible(true);
        dispose();
    }
}