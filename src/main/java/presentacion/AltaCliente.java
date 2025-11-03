package presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.electropolar.LogicaAdmin;

import java.awt.*;

public class AltaCliente extends JPanel {

    // Campos de texto
    private final JTextField txtNombre, txtCorreo, txtRFC, txtTelefono, txtCalle,
            txtColonia, txtNoExt, txtNoInt, txtCP, txtMunicipio, txtEstado,
            txtPais, txtCFDI;
    private JButton btnGuardar, btnCancelar;
    private final LogicaAdmin logic = new LogicaAdmin();

    public AltaCliente() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FFFFFF"));

        // ----------------- Panel superior -----------------
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#B5EFE3"));
        panelSuperior.setBorder(new EmptyBorder(15, 10, 15, 10));

        JLabel lblTitulo = new JLabel("Alta Cliente", JLabel.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        panelSuperior.add(lblTitulo, BorderLayout.CENTER);

        ImageIcon icono = new ImageIcon("electropolar/src/main/java/presentacion/logo.png");
        Image img = icono.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        panelSuperior.add(new JLabel(new ImageIcon(img)), BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // ----------------- Panel central -----------------
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(Color.decode("#FFFFFF"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fuente = new Font("SansSerif", Font.PLAIN, 14);
        Color fondoCampo = Color.decode("#A4C5E1");

        // Inicialización
    
        txtNombre = new JTextField(20);
        txtCorreo = new JTextField(20);
        txtRFC = new JTextField(15);
        txtTelefono = new JTextField(15);
        txtCalle = new JTextField(20);
        txtColonia = new JTextField(20);
        txtNoExt = new JTextField(6);
        txtNoInt = new JTextField(6);
        txtCP = new JTextField(10);
        txtMunicipio = new JTextField(15);
        txtEstado = new JTextField(15);
        txtPais = new JTextField(15);
        txtCFDI = new JTextField(15);

        JTextField[] campos = { txtNombre, txtCorreo, txtRFC, txtTelefono, txtCalle,
                txtColonia, txtNoExt, txtNoInt, txtCP,  txtMunicipio, txtEstado, txtPais, txtCFDI };

        for (JTextField campo : campos) {
            campo.setFont(fuente);
            campo.setBackground(fondoCampo);
            campo.setBorder(new LineBorder(Color.GRAY));
        }

        int y = 0;

        // Fila 2
        addCampo(panelCentral, gbc, y++, "Nombre", txtNombre, "RFC", txtRFC);
        // Fila 3
        addCampo(panelCentral, gbc, y++, "Correo", txtCorreo, "Teléfono", txtTelefono);
        // Fila 5
        addCampo(panelCentral, gbc, y++, "Calle", txtCalle, "Colonia", txtColonia);
        // Fila 4
        addTripleCampo(panelCentral, gbc, y++, "No. ext", txtNoExt, "No. int", txtNoInt, "C.P:", txtCP);
        // Fila 6
        addCampo(panelCentral, gbc, y++, "Municipio", txtMunicipio, "Estado", txtEstado);
        // Fila 7
        addCampo(panelCentral, gbc, y++, "País", txtPais, "CFDI", txtCFDI);

        add(panelCentral, BorderLayout.CENTER);

        // ----------------- Panel botones -----------------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotones.setBackground(Color.decode("#FFFFFF"));

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(Color.decode("#A4C5E1"));
        btnGuardar.addActionListener(e -> {
            logic.procesarGuardarCliente(txtNombre, txtRFC, txtCorreo, txtTelefono, txtCalle,
                    txtColonia, txtNoExt, txtNoInt, txtCP, txtMunicipio,
                    txtEstado, txtPais, txtCFDI);
        });

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.decode("#A4C5E1"));
        // ← Aquí cerramos la ventana padre:
        btnCancelar.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(AltaCliente.this);
            if (window != null) {
                window.dispose();
            }
        });

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void addCampo(JPanel panel, GridBagConstraints gbc, int fila,
            String etiqueta1, JTextField campo1, String etiqueta2, JTextField campo2) {
        gbc.gridy = fila;

        gbc.gridx = 0;
        panel.add(new JLabel(etiqueta1), gbc);

        gbc.gridx = 1;
        panel.add(campo1, gbc);

        if (etiqueta2 != null && !etiqueta2.isEmpty()) {
            gbc.gridx = 2;
            panel.add(new JLabel(etiqueta2), gbc);

            gbc.gridx = 3;
            panel.add(campo2, gbc);
        }
    }

    private void addTripleCampo(JPanel panel, GridBagConstraints gbc, int fila,
            String etiqueta1, JTextField campo1,
            String etiqueta2, JTextField campo2,
            String etiqueta3, JTextField campo3) {
        gbc.gridy = fila;

        gbc.gridx = 0;
        panel.add(new JLabel(etiqueta1), gbc);
        gbc.gridx = 1;
        panel.add(campo1, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel(etiqueta2), gbc);
        gbc.gridx = 3;
        panel.add(campo2, gbc);

        gbc.gridx = 4;
        panel.add(new JLabel(etiqueta3), gbc);
        gbc.gridx = 5;
        panel.add(campo3, gbc);
    }
}
