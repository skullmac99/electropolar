package presentacion;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;

import com.electropolar.Cliente;
import com.electropolar.LogicaVentas;
import com.electropolar.Vendedor;

import Datos.ConexionBD;
import Datos.ValidacionesBD;
//import Datos.VentaDAO;

public class puntoVenta extends JFrame {

    // Componentes de la interfaz
    private JTextField txtFecha, txtHora;
    private JTextField txtClave, txtNombre, txtDescripcion, txtExistencia, txtUnidad, txtCantidad, txtDcto,
            txtPrecioUnit, txtTotalPagar;
    private JTextField txtFolio;
    private JTable tablaVenta;
    private LogicaVentas logicaVentas;
    private JComboBox<Cliente> comboCliente;
    private JComboBox<Vendedor> comboVendedor;

    public puntoVenta() {
        setTitle("Sistema de Punto de Venta");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 700);
        setBackground(Color.decode("#B5EFE3"));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Icono de la aplicación
        Image icono = new ImageIcon(getClass().getResource("/logo.png")).getImage().getScaledInstance(512, 512,
                Image.SCALE_SMOOTH);
        setIconImage(icono);

        // ------------------------- PANEL SUPERIOR -------------------------
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBackground(Color.decode("#B5EFE3"));

        // Fecha y Hora
        txtFecha = new JTextField(8);
        txtHora = new JTextField(8);
        txtFecha.setBackground(Color.decode("#A4C5E1"));
        txtHora.setBackground(Color.decode("#A4C5E1"));
        com.electropolar.Funciones.inicializarFechaYHora(txtFecha, txtHora);
        panelSuperior.add(new JLabel("Fecha"));
        panelSuperior.add(txtFecha);
        panelSuperior.add(txtHora);

        // Combo de Vendedores
        panelSuperior.add(new JLabel("Vendedor"));
        comboVendedor = new JComboBox<>(); // usa la variable de instancia
        comboVendedor.setBackground(Color.decode("#A4C5E1"));
        ValidacionesBD vbd = new ValidacionesBD();
        for (Vendedor v : vbd.cargarVendedores()) {
            comboVendedor.addItem(v);
        }
        panelSuperior.add(comboVendedor);

        // Folio
        panelSuperior.add(new JLabel("Folio"));
        txtFolio = new JTextField(8); // usa la variable de instancia
        txtFolio.setBackground(Color.decode("#A4C5E1"));
        txtFolio.setEditable(false);
        /*try {
            // Conecta y usa VentaDAO para obtener nextFolio
            Connection conn = ConexionBD.conectar();
            VentaDAO ventaDao = new VentaDAO(conn);
            int nextFolio = ventaDao.obtenerNextFolio();
            txtFolio.setText(String.valueOf(nextFolio));
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            txtFolio.setText("1");
        }*/
        panelSuperior.add(txtFolio);

        // Botones de repetición y cancelación
        JButton btnRepetirTicket = new JButton("Repetir ticket");
        btnRepetirTicket.setBackground(Color.decode("#A4C5E1"));
        btnRepetirTicket.addActionListener(e -> {
            JDialog dlg = new JDialog(this, "Reimprimir ticket", true);
            dlg.setContentPane(new ReimpresionTicket());
            dlg.setSize(800, 400);
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);
        });
        panelSuperior.add(btnRepetirTicket);

        JButton btnCancelarticket = new JButton("Cancelar ticket");
        btnCancelarticket.setBackground(Color.decode("#A4C5E1")); 
        btnCancelarticket.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Cancelar ticket", true);
            dialog.setContentPane(new CancelarTicket());
            dialog.setSize(800, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });
        panelSuperior.add(btnCancelarticket);

        // Combo de Clientes
        panelSuperior.add(new JLabel("Cliente"));
        comboCliente = new JComboBox<>(); // usa la variable de instancia
        comboCliente.setBackground(Color.decode("#A4C5E1"));
        for (Cliente cliente : vbd.cargarClientes()) {
            comboCliente.addItem(cliente);
        }
        panelSuperior.add(comboCliente);

        // Logo
        panelSuperior.add(Box.createHorizontalStrut(100));
        panelSuperior.add(new JLabel(new ImageIcon("logo.png")));

        add(panelSuperior, BorderLayout.NORTH);

        // ------------------------- PANEL CENTRAL -------------------------
        String[] columnas = { "Clave", "Nombre", "Cantidad", "Unidad", "Precio unitario", "Dcto", "Importe" };
        DefaultTableModel modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ninguna celda editable
            }
        };
        tablaVenta = new JTable(modeloTabla);
        // Evitar que se puedan reordenar o redimensionar las columnas
        tablaVenta.getTableHeader().setReorderingAllowed(false);
        tablaVenta.getTableHeader().setResizingAllowed(false);

        // Selección de una sola fila
        tablaVenta.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Altura de las filas para mejor visualización
        tablaVenta.setRowHeight(24);
        // Ancho sugerido por columna (ajustable)
        tablaVenta.getColumnModel().getColumn(0).setPreferredWidth(70); // Clave
        tablaVenta.getColumnModel().getColumn(1).setPreferredWidth(160); // Nombre
        tablaVenta.getColumnModel().getColumn(2).setPreferredWidth(70); // Cantidad
        tablaVenta.getColumnModel().getColumn(3).setPreferredWidth(70); // Unidad
        tablaVenta.getColumnModel().getColumn(4).setPreferredWidth(100); // Precio unitario
        tablaVenta.getColumnModel().getColumn(5).setPreferredWidth(60); // Dcto
        tablaVenta.getColumnModel().getColumn(6).setPreferredWidth(90); // Importe
        tablaVenta.getTableHeader().setBackground(Color.decode("#7CBBBB"));
        tablaVenta.setBackground(Color.decode("#FFFFFF"));

        JScrollPane scrollPane = new JScrollPane(tablaVenta);
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.decode("#FFFFFF"));
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // ------------------------- PANEL INFERIOR -------------------------
        JPanel panelInferior = new JPanel(new GridBagLayout());
        panelInferior.setBackground(Color.decode("#B5EFE3"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos de entrada
        txtClave = new JTextField(8);
        txtNombre = new JTextField(12);
        txtDescripcion = new JTextField(20);
        txtExistencia = new JTextField(5);
        txtUnidad = new JTextField(5);
        txtCantidad = new JTextField("1", 5);
        txtDcto = new JTextField("0", 5);
        txtPrecioUnit = new JTextField(8);
        txtTotalPagar = new JTextField(10);

        // Inicializar lógica
        logicaVentas = new LogicaVentas(
                this, txtClave, txtNombre, txtDescripcion, txtExistencia, txtUnidad,
                txtCantidad, txtDcto, txtPrecioUnit, txtTotalPagar, tablaVenta);

        /*getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("INSERT"), "finalizarVenta");
        getRootPane().getActionMap().put("finalizarVenta", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cliente CLI = (Cliente) comboCliente.getSelectedItem();
                Vendedor vd = (Vendedor) comboVendedor.getSelectedItem();
                //logicaVentas.confirmarYFinalizarVenta(CLI, vd);
            }
        });*/

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelarVenta");

        getRootPane().getActionMap().put("cancelarVenta", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opt = JOptionPane.showConfirmDialog(puntoVenta.this,
                        "¿Estás seguro de cancelar la venta en curso?",
                        "Cancelar venta", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    logicaVentas.cancelarVentaEnCurso();
                }
            }
        });
        

        // No editables
        txtNombre.setEditable(false);
        txtDescripcion.setEditable(false);
        txtExistencia.setEditable(false);
        txtUnidad.setEditable(false);
        txtPrecioUnit.setEditable(false);
        txtTotalPagar.setEditable(false);

        for (JTextField tf : new JTextField[] {
                txtClave, txtNombre, txtDescripcion, txtExistencia,
                txtUnidad, txtCantidad, txtDcto, txtPrecioUnit, txtTotalPagar
        }) {
            tf.setBackground(Color.decode("#A4C5E1"));
            tf.setBorder(new LineBorder(Color.BLACK, 1, true));
        }

        // ------------------ EVENTOS ------------------

        txtClave.addActionListener(e -> logicaVentas.buscarYMostrarProducto());
        txtCantidad.addActionListener(e -> logicaVentas.procesarCantidad());

        // ------------------ DISEÑO DE PANELES ------------------

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelInferior.add(new JLabel("clave producto"), gbc);
        gbc.gridx = 1;
        panelInferior.add(txtClave, gbc);
        gbc.gridx = 2;
        panelInferior.add(new JLabel("Nombre"), gbc);
        gbc.gridx = 3;
        panelInferior.add(txtNombre, gbc);
        gbc.gridx = 4;
        panelInferior.add(new JLabel("cantidad"), gbc);
        gbc.gridx = 5;
        panelInferior.add(txtCantidad, gbc);
        gbc.gridx = 6;
        panelInferior.add(new JLabel("Unidad"), gbc);
        gbc.gridx = 7;
        panelInferior.add(txtUnidad, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelInferior.add(new JLabel("Existencia"), gbc);
        gbc.gridx = 1;
        panelInferior.add(txtExistencia, gbc);
        gbc.gridx = 2;
        panelInferior.add(new JLabel("Descripcion"), gbc);
        gbc.gridx = 3;
        gbc.gridwidth = 3;
        panelInferior.add(txtDescripcion, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 6;
        panelInferior.add(new JLabel("Dcto"), gbc);
        gbc.gridx = 7;
        panelInferior.add(txtDcto, gbc);
        gbc.gridx = 8;
        panelInferior.add(new JLabel("Precio unitario"), gbc);
        gbc.gridx = 9;
        panelInferior.add(txtPrecioUnit, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(Color.decode("#A4C5E1"));
        btnBuscar.addActionListener(e -> {
            JDialog dialog = new JDialog((Frame) null, "Buscar Producto", true);
            dialog.setSize(800, 400);
            dialog.setLocationRelativeTo(null);

            BuscarProducto panelBusqueda = new BuscarProducto(logicaVentas); // Pasar instancia de LogicaVentas
            dialog.setContentPane(panelBusqueda);

            dialog.setVisible(true);
        });

        panelInferior.add(btnBuscar, gbc);

        gbc.gridx = 8;
        gbc.gridy = 2;
        panelInferior.add(new JLabel("Total a pagar"), gbc);
        gbc.gridx = 9;
        panelInferior.add(txtTotalPagar, gbc);

        add(panelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }

}