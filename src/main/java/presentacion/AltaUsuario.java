package presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import com.electropolar.LogicaAdmin;

public class AltaUsuario extends JPanel {

    private JTextField txtNombre, txtApellidoP, txtApellidoM;
    private JComboBox comboRol;
    private JButton btnGuardar, btnCancelar;
    private final LogicaAdmin logic = new LogicaAdmin();


    public AltaUsuario() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FFFFFF")); // fondo amarillo claro

        // ------------------- Panel superior -------------------
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#B5EFE3")); // barra superior
        panelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Alta Usuario", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 26));
        panelSuperior.add(titulo, BorderLayout.CENTER);

        ImageIcon imageIcon = new ImageIcon("electropolar/src/main/java/presentacion/logo.png");
        Image imagenEscalada = imageIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(imagenEscalada));
        panelSuperior.add(logo, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // ------------------- Panel central -------------------
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(Color.decode("#FFFFFF"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtNombre = crearCampo();
        txtApellidoP = crearCampo();
        txtApellidoM = crearCampo();
        String[]roles={"VENDEDOR","ADMINISTRADOR"};
        comboRol = new JComboBox<>(roles);
        comboRol.setFont(new Font("Arial", Font.PLAIN,14));
        comboRol.setBackground(Color.decode("#A4C5E1"));

        int y = 0;

        gbc.gridx = 0; gbc.gridy = y;
        panelCentral.add(new JLabel("Nombre"), gbc);
        gbc.gridx = 1;
        panelCentral.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = ++y;
        panelCentral.add(new JLabel("Apellido paterno"), gbc);
        gbc.gridx = 1;
        panelCentral.add(txtApellidoP, gbc);

        gbc.gridx = 0; gbc.gridy = ++y;
        panelCentral.add(new JLabel("Apellido materno"), gbc);
        gbc.gridx = 1;
        panelCentral.add(txtApellidoM, gbc);

        gbc.gridx = 0; gbc.gridy = ++y;
        panelCentral.add(new JLabel("ROL"), gbc);
        gbc.gridx = 1;
        panelCentral.add(comboRol, gbc);

        add(panelCentral, BorderLayout.CENTER);

        // ------------------- Panel inferior -------------------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Color.decode("#7CBBBB"));

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(Color.decode("#A4C5E1"));
        btnGuardar.addActionListener(e -> logic.procesarGuardarUsuario(
            txtNombre, txtApellidoP,
            txtApellidoM , comboRol
        ));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.decode("#A4C5E1"));
        // ← Aquí cerramos la ventana padre:
        btnCancelar.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(AltaUsuario.this);
            if (window != null) {
                window.dispose();
            }
        });

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private JTextField crearCampo() {
        JTextField campo = new JTextField(20);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setBackground(Color.decode("#A4C5E1"));
        campo.setBorder(new LineBorder(Color.GRAY));
        return campo;
    }

    // Getters
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtApellidoP() { return txtApellidoP; }
    public JTextField getTxtApellidoM() { return txtApellidoM; }
    public JComboBox<String> getcoboROl() {return comboRol;}
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}
