package presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.electropolar.LogicaAdmin;
import com.electropolar.Proveedor;

import Datos.ValidacionesBD;

import java.awt.*;
import java.awt.event.KeyEvent;

public class AltaProducto extends JPanel {

    private JTextField txtIdProducto, txtNombre, txtDescripcion, txtPrecio, txtStock, txtUnidad;
    private JComboBox comboProveedor;
    private JButton btnGuardar, btnCancelar;
    private final LogicaAdmin logic = new LogicaAdmin();

    public AltaProducto() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FFFFFF"));

        // ------------------- Panel superior -------------------
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#B5EFE3"));
        panelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Alta Producto", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelSuperior.add(titulo, BorderLayout.CENTER);

        ImageIcon imageIcon = new ImageIcon("electropolar/src/main/java/presentacion/logo.png");
        Image imagenEscalada = imageIcon.getImage()
                .getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(imagenEscalada));
        panelSuperior.add(logo, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // ------------------- Inicializar campos -------------------
        txtIdProducto = new JTextField(15);
        txtNombre = new JTextField(20);
        txtDescripcion = new JTextField(20);
        txtPrecio = new JTextField(10);
        txtStock = new JTextField(5);
        txtUnidad = new JTextField(5);
        comboProveedor = new JComboBox<>();
        ValidacionesBD vbd = new ValidacionesBD();
        for (Proveedor proveedor : vbd.cargarProveedores()) {
            comboProveedor.addItem(proveedor);
        }

        // Estilos comunes
        Font fontCampos = new Font("SansSerif", Font.PLAIN, 14);
        Color campoFondo = Color.decode("#A4C5E1");
        for (JTextField tf : new JTextField[] { txtIdProducto, txtNombre, txtDescripcion, txtPrecio, txtStock,
                txtUnidad }) {
            tf.setFont(fontCampos);
            tf.setBackground(campoFondo);
            tf.setBorder(new LineBorder(Color.GRAY));
        }
        comboProveedor.setBackground(Color.decode("#A4C5E1"));

        // ------------------- Panel central -------------------
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(Color.decode("#FEFEFE"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // ID Producto
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panelCentral.add(new JLabel("ID Producto"), gbc);
        gbc.gridx = 1;
        panelCentral.add(txtIdProducto, gbc);

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCentral.add(new JLabel("Nombre"), gbc);
        gbc.gridx = 1;
        panelCentral.add(txtNombre, gbc);

        // Descripción (ocupa 2 cols)
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelCentral.add(new JLabel("Descripción"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panelCentral.add(txtDescripcion, gbc);
        gbc.gridwidth = 1;

        // Precio
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelCentral.add(new JLabel("Precio"), gbc);
        gbc.gridx = 1;
        panelCentral.add(txtPrecio, gbc);

        // Stock
        gbc.gridx = 0;
        gbc.gridy = 4;
        panelCentral.add(new JLabel("Stock"), gbc);
        gbc.gridx = 1;
        panelCentral.add(txtStock, gbc);

        // Unidad
        gbc.gridx = 0;
        gbc.gridy = 5;
        panelCentral.add(new JLabel("Unidad"), gbc);
        gbc.gridx = 1;
        panelCentral.add(txtUnidad, gbc);

        // Proveedor
        gbc.gridx = 0;
        gbc.gridy = 6;
        panelCentral.add(new JLabel("Proveedor"), gbc);
        gbc.gridx = 1;
        panelCentral.add(comboProveedor, gbc);

        add(panelCentral, BorderLayout.CENTER);

        // ------------------- Panel botones -------------------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotones.setBackground(Color.decode("#FEFEFE"));

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(Color.decode("#A4C5E1"));
        btnGuardar.addActionListener(e -> logic.procesarGuardarProducto(
                txtIdProducto, txtNombre, txtDescripcion,
                txtUnidad, txtPrecio, txtStock, comboProveedor));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.decode("#A4C5E1"));
        // ← Aquí cerramos la ventana padre:
        btnCancelar.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(AltaProducto.this);
            if (window != null) {
                window.dispose();
            }
        });

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);

        // Validación: solo números y un punto decimal en txtPrecio
        txtPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();

                // Permitir solo números, punto y tecla de borrado
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume(); // Bloquear el carácter
                }

                // Evitar más de un punto decimal
                if (c == '.' && txtPrecio.getText().contains(".")) {
                    e.consume();
                }
            }
        });

        // Validación: solo números enteros en txtStock
        txtStock.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();

                // Solo permitir dígitos
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });

        // --- Validación en tiempo real para el campo ID del producto ---
        txtIdProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();

                // Evita espacios
                if (Character.isWhitespace(c)) {
                    e.consume();
                    return;
                }

                // Evita que se escriban más de 8 caracteres
                if (txtIdProducto.getText().length() >= 8) {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null,
                            "El ID del producto no puede tener más de 8 caracteres.",
                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

    }

    // Getters si los necesitas desde fuera
    public JTextField getTxtIdProducto() {
        return txtIdProducto;
    }

    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JTextField getTxtDescripcion() {
        return txtDescripcion;
    }

    public JTextField getTxtPrecio() {
        return txtPrecio;
    }

    public JTextField getTxtStock() {
        return txtStock;
    }

    public JTextField getTxtUnidad() {
        return txtUnidad;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }
}
