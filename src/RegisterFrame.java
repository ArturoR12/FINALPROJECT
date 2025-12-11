import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterFrame extends JFrame {
    private JTextField txtUser, txtNombre, txtApellido, txtTelefono, txtEmail;
    private JPasswordField txtPass, txtConfirmPass;
    private UserController userController;
    private LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.userController = new UserController();
        setupUI();
    }

    private void setupUI() {
        setTitle("Registro de Usuario");
        setSize(400, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addField(panel, "Usuario:", txtUser = new JTextField());
        addField(panel, "Contraseña:", txtPass = new JPasswordField());
        addField(panel, "Confirmar:", txtConfirmPass = new JPasswordField());
        addField(panel, "Nombre:", txtNombre = new JTextField());
        addField(panel, "Apellido:", txtApellido = new JTextField());
        addField(panel, "Telefono:", txtTelefono = new JTextField());
        addField(panel, "Email:", txtEmail = new JTextField());

        JButton btnRegister = new JButton("Registrarse");
        btnRegister.addActionListener(e -> register());

        JButton btnBack = new JButton("Volver");
        btnBack.addActionListener(e -> goBack());

        panel.add(btnRegister);
        panel.add(btnBack);

        add(panel);
    }

    private void addField(JPanel panel, String label, JComponent field) {
        panel.add(new JLabel(label));
        panel.add(field);
    }

    private void register() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());
        String confirmPassword = new String(txtConfirmPass.getPassword());
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || email.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Las contraseñas no coinciden",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (userController.userExists(username)) {
                JOptionPane.showMessageDialog(this,
                        "El nombre de usuario ya esta en uso",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = new User(username, password, nombre, apellido, telefono, email);

            if (userController.register(newUser)) {
                JOptionPane.showMessageDialog(this,
                        "Usuario registrado exitosamente",
                        "Exito",
                        JOptionPane.INFORMATION_MESSAGE);
                goBack();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al registrar usuario",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        this.dispose();
        loginFrame.showFrame();
    }
}