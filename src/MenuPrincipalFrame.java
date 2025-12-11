import javax.swing.*;
import java.awt.*;

public class MenuPrincipalFrame extends JFrame {
    private User currentUser;

    public MenuPrincipalFrame(User user) {
        this.currentUser = user;
        setupUI();
    }

    private void setupUI() {
        setTitle("Sistema de Gestion de Almacen");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 240, 240));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));

        JLabel lblTitle = new JLabel("SISTEMA DE GESTION DE ALMACEN", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        JLabel lblUser = new JLabel("Usuario: " + currentUser.getNombre(), JLabel.RIGHT);
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUser.setForeground(Color.WHITE);
        lblUser.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
        headerPanel.add(lblUser, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        optionsPanel.setBackground(new Color(240, 240, 240));

        JButton btnUsuarios = createButton("GESTION DE USUARIOS", new Color(60, 179, 113));
        btnUsuarios.addActionListener(e -> openUsersManagement());

        JButton btnProductos = createButton("GESTION DE PRODUCTOS", new Color(30, 144, 255));
        btnProductos.addActionListener(e -> openProductsManagement());

        JButton btnLogout = createButton("CERRAR SESION", new Color(220, 20, 60));
        btnLogout.addActionListener(e -> logout());

        optionsPanel.add(btnUsuarios);
        optionsPanel.add(btnProductos);
        optionsPanel.add(btnLogout);

        panel.add(optionsPanel, BorderLayout.CENTER);

        JLabel lblFooter = new JLabel("Proyecto Final - Sistema de Almacen ITLA", JLabel.CENTER);
        lblFooter.setFont(new Font("Arial", Font.ITALIC, 12));
        lblFooter.setForeground(Color.GRAY);
        lblFooter.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(lblFooter, BorderLayout.SOUTH);

        add(panel);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
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

    private void openUsersManagement() {
        UserController userController = new UserController();
        new UsersFrame(currentUser, userController).setVisible(true);
        this.setVisible(false);
    }

    private void openProductsManagement() {
        ProductController productController = new ProductController();
        new ProductsFrame(currentUser, productController).setVisible(true);
        this.setVisible(false);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Esta seguro que desea cerrar sesion?",
                "Confirmar Cierre de Sesion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}