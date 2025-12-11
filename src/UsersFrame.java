// VENTANA DE GESTIÓN DE USUARIOS (Renombrado para claridad)
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class UsersFrame extends JFrame {
    private User currentUser;
    private UserController userController;
    private JTable table;
    private DefaultTableModel tableModel;

    public UsersFrame(User user, UserController userController) {
        this.currentUser = user;
        this.userController = userController;
        setupUI();
        loadUsers();
    }

    private void setupUI() {
        setTitle("Gestión de Usuarios");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel lblTitle = new JLabel("Usuarios Registrados", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Tabla
        String[] columns = {"Nombre", "Apellido", "Teléfono", "Email", "Usuario"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnUpdate = new JButton("Actualizar");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnBack = new JButton("Volver al Menú");

        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnBack.addActionListener(e -> goBack());

        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userController.getAllUsers();
        for (User user : users) {
            tableModel.addRow(user.toTableRow());
        }
    }

    private void updateUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un usuario de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) table.getValueAt(row, 4);
        List<User> users = userController.getAllUsers();
        User userToUpdate = null;

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                userToUpdate = user;
                break;
            }
        }

        if (userToUpdate != null) {
            showUserForm(userToUpdate, "Actualizar Usuario");
        }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un usuario de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) table.getValueAt(row, 4);

        if (username.equals(currentUser.getUsername())) {
            JOptionPane.showMessageDialog(this,
                    "No puede eliminarse a sí mismo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar al usuario '" + username + "'?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            List<User> users = userController.getAllUsers();
            User userToDelete = null;

            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    userToDelete = user;
                    break;
                }
            }

            if (userToDelete != null && userController.deleteUser(userToDelete.getId())) {
                JOptionPane.showMessageDialog(this,
                        "Usuario eliminado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            }
        }
    }

    private void showUserForm(User user, String title) {
        JTextField txtNombre = new JTextField(user.getNombre());
        JTextField txtApellido = new JTextField(user.getApellido());
        JTextField txtTelefono = new JTextField(user.getTelefono());
        JTextField txtEmail = new JTextField(user.getEmail());

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(txtApellido);
        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTelefono);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);

        int result = JOptionPane.showConfirmDialog(this, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            user.setNombre(txtNombre.getText().trim());
            user.setApellido(txtApellido.getText().trim());
            user.setTelefono(txtTelefono.getText().trim());
            user.setEmail(txtEmail.getText().trim());

            if (userController.updateUser(user)) {
                JOptionPane.showMessageDialog(this,
                        "Usuario actualizado exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            }
        }
    }

    private void goBack() {
        new MenuPrincipalFrame(currentUser).setVisible(true);
        dispose();
    }
}