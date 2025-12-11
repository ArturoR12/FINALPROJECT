import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginFrame() {
        setupUI();
    }

    private void setupUI() {
        setTitle("Sistema de Login - Almacen ITLA");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 240, 240));

        JLabel lblTitle = new JLabel("INICIO DE SESION", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(30, 144, 255));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        formPanel.setBackground(new Color(240, 240, 240));

        formPanel.add(new JLabel("Usuario:"));
        txtUser = new JTextField();
        formPanel.add(txtUser);

        formPanel.add(new JLabel("Contrasena:"));
        txtPass = new JPasswordField();
        formPanel.add(txtPass);

        JButton btnLogin = new JButton("INICIAR SESION");
        btnLogin.addActionListener(e -> login());

        JButton btnRegister = new JButton("REGISTRARSE");
        btnRegister.addActionListener(e -> openRegister());

        formPanel.add(btnLogin);
        formPanel.add(btnRegister);

        panel.add(formPanel, BorderLayout.CENTER);

        JLabel lblFooter = new JLabel("Sistema de Gestion de Almacen ITLA - Proyecto Final", JLabel.CENTER);
        lblFooter.setFont(new Font("Arial", Font.ITALIC, 12));
        lblFooter.setForeground(Color.GRAY);
        panel.add(lblFooter, BorderLayout.SOUTH);

        add(panel);

        txtPass.addActionListener(e -> login());
    }

    private void login() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe ingresar su usuario y contrasena.\nSi no esta registrado, haga clic en 'REGISTRARSE'.",
                    "Campos Requeridos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserController userController = new UserController();
            User usuario = userController.login(user, pass);

            if (usuario != null) {
                JOptionPane.showMessageDialog(this,
                        "Bienvenido " + usuario.getNombre() + " " + usuario.getApellido() + "!",
                        "Login Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

                new MenuPrincipalFrame(usuario).setVisible(true);
                dispose();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Usuario o contrasena incorrectos.\n" +
                                "Verifique sus credenciales o registrese si es nuevo usuario.",
                        "Error de Autenticacion",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al conectar con la base de datos:\n" + ex.getMessage(),
                    "Error de Conexion",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegister() {
        new RegisterFrame(this).setVisible(true);
        this.setVisible(false);
    }

    public void showFrame() {
        setVisible(true);
    }
}