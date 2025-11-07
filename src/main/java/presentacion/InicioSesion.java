package presentacion;

import javax.swing.*;
import javax.swing.border.LineBorder;

import Datos.ValidacionesBD;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InicioSesion extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnAceptar;
    private JButton btnCancelar;

    public InicioSesion() {
        setTitle("Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 280);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        Image icono = new ImageIcon(getClass().getResource("/logo.png")).getImage().getScaledInstance(256, 256,
                Image.SCALE_SMOOTH);
        setIconImage(icono);

        // Panel superior (título y logo)
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(Color.decode("#B5EFE3")); // oro claro
        panelSuperior.setBounds(0, 0, 400, 60);
        panelSuperior.setLayout(null);

        JLabel lblTitulo = new JLabel("Inicio Sesión");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitulo.setBounds(150, 15, 120, 30);
        panelSuperior.add(lblTitulo);

        ImageIcon imageIcon = new ImageIcon("electropolar\\src\\main\\java\\presentacion\\logo.png");
        Image imagenEscalada = imageIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        ImageIcon iconoPersonalizado = new ImageIcon(imagenEscalada);
        JLabel logo = new JLabel(iconoPersonalizado);
        logo.setBounds(310, 5, 64, 50);
        panelSuperior.add(logo);

        add(panelSuperior);

        // Panel inferior (zona de campos y botones)
        JPanel panelCampos = new JPanel();
        panelCampos.setBackground(Color.decode("#FFFFFF")); // beige claro
        panelCampos.setBounds(0, 60, 400, 220);
        panelCampos.setLayout(null);

        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setBounds(60, 30, 100, 25);
        panelCampos.add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(160, 30, 160, 25);
        txtUsuario.setBorder(new LineBorder(Color.BLACK, 1, true));
        txtUsuario.setBackground(Color.decode("#A4C5E1"));
        panelCampos.add(txtUsuario);

        JLabel lblContrasena = new JLabel("Contraseña");
        lblContrasena.setBounds(60, 70, 100, 25);
        panelCampos.add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(160, 70, 160, 25);
        txtContrasena.setBorder(new LineBorder(Color.BLACK, 1, true));
        txtContrasena.setBackground(Color.decode("#A4C5E1"));
        panelCampos.add(txtContrasena);

        btnAceptar = new JButton("Aceptar");
        btnAceptar.setBounds(90, 130, 100, 30);
        btnAceptar.setBackground(Color.decode("#A4C5E1")); // verde claro
        panelCampos.add(btnAceptar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(210, 130, 100, 30);
        btnCancelar.setBackground(Color.decode("#A4C5E1"));
        panelCampos.add(btnCancelar);

        // Deshabilitar copiar, pegar y cortar en txtUsuario
        txtUsuario.getInputMap().put(KeyStroke.getKeyStroke("control C"), "none");
        txtUsuario.getInputMap().put(KeyStroke.getKeyStroke("control V"), "none");
        txtUsuario.getInputMap().put(KeyStroke.getKeyStroke("control X"), "none");
        txtUsuario.getActionMap().put("copy", null);
        txtUsuario.getActionMap().put("paste", null);
        txtUsuario.getActionMap().put("cut", null);

        // Deshabilitar copiar, pegar y cortar en txtContrasena
        txtContrasena.getInputMap().put(KeyStroke.getKeyStroke("control C"), "none");
        txtContrasena.getInputMap().put(KeyStroke.getKeyStroke("control V"), "none");
        txtContrasena.getInputMap().put(KeyStroke.getKeyStroke("control X"), "none");
        txtContrasena.getActionMap().put("copy", null);
        txtContrasena.getActionMap().put("paste", null);
        txtContrasena.getActionMap().put("cut", null);

        add(panelCampos);

        // Al presionar Enter en el campo usuario → pasa al campo contraseña
        txtUsuario.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("ENTER"), "moverASiguienteCampo");
        txtUsuario.getActionMap().put("moverASiguienteCampo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtContrasena.requestFocus();
            }
        });

        // Al presionar Enter en el campo contraseña → ejecutar inicio de sesión
        txtContrasena.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("ENTER"), "iniciarSesion");
        txtContrasena.getActionMap().put("iniciarSesion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAceptar.doClick(); // Simula clic en "Aceptar"
            }
        });

        // Acción del botón Aceptar
        btnAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = txtUsuario.getText();
                String contrasena = new String(txtContrasena.getPassword());
                ValidacionesBD vbd = new ValidacionesBD();

                // Obtengo el rol directamente: devuelve "VENDEDOR", "ADMINISTRADOR" o null
                String rol = vbd.obtenerRol(usuario, contrasena);

                if (rol == null) {
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Según el rol, abro la ventana correspondiente:
                SwingUtilities.invokeLater(() -> {
                    dispose(); // cierro el login
                    if ("VENDEDOR".equalsIgnoreCase(rol)) {
                        JOptionPane.showMessageDialog(null, "Acceso concedido");
                        new puntoVenta().setVisible(true);
                    } else if ("ADMINISTRADOR".equalsIgnoreCase(rol)) {
                        JOptionPane.showMessageDialog(null, "Acceso concedido");
                        new ventaAdmin().setVisible(true);
                    } else {
                        // por si hay otros roles
                        JOptionPane.showMessageDialog(null, "Rol desconocido: " + rol, "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        });

        // Acción del botón Cancelar
        btnCancelar.addActionListener(e -> System.exit(0));
    }

}
