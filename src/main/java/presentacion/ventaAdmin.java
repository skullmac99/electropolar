package presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.*;

import com.electropolar.Funciones;
import com.electropolar.LogicaAdmin;
import com.electropolar.Producto;
import com.electropolar.Proveedor;

import Datos.ValidacionesBD;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.List;

public class ventaAdmin extends JFrame {

    private JTextField txtFecha, txtHora;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JButton btnModificar;
    private JButton btnEliminar;
    LogicaAdmin logic = new LogicaAdmin();

    public ventaAdmin() {
        super("Sistema de Punto de Venta");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1400, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Icono de la aplicación
        Image icono = new ImageIcon(getClass().getResource("/logo.png")).getImage().getScaledInstance(1024, 1024,
                Image.SCALE_SMOOTH);
        setIconImage(icono);

        // ---------------------- PANEL NORTE ----------------------
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelNorte.setBackground(Color.decode("#B5EFE3"));
        panelNorte.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Botones de menú
        // Botón específico para Alta Producto
        JButton btnAltaProducto = new JButton("Alta Producto");
        btnAltaProducto.setPreferredSize(new Dimension(130, 40));
        btnAltaProducto.setBackground(Color.decode("#A4C5E1"));
        btnAltaProducto.setBorder(new LineBorder(Color.BLACK, 1, true));
        panelNorte.add(btnAltaProducto);

        JButton btnAltaUsuario = new JButton("Alta Usuario");
        btnAltaUsuario.setPreferredSize(new Dimension(130, 40));
        btnAltaUsuario.setBackground(Color.decode("#A4C5E1"));
        btnAltaUsuario.setBorder(new LineBorder(Color.BLACK, 1, true));
        panelNorte.add(btnAltaUsuario);

        JButton btnAdminUsuario = new JButton("Administrar Usuario");
        btnAdminUsuario.setPreferredSize(new Dimension(130, 40));
        btnAdminUsuario.setBackground(Color.decode("#A4C5E1"));
        btnAdminUsuario.setBorder(new LineBorder(Color.BLACK, 1, true));
        panelNorte.add(btnAdminUsuario);

        JButton btnAltaCliente = new JButton("Alta Cliente");
        btnAltaCliente.setPreferredSize(new Dimension(130, 40));
        btnAltaCliente.setBackground(Color.decode("#A4C5E1"));
        btnAltaCliente.setBorder(new LineBorder(Color.BLACK, 1, true));
        panelNorte.add(btnAltaCliente);

        JButton btnAdminCliente = new JButton("Administrar Cliente");
        btnAdminCliente.setPreferredSize(new Dimension(130, 40));
        btnAdminCliente.setBackground(Color.decode("#A4C5E1"));
        btnAdminCliente.setBorder(new LineBorder(Color.BLACK, 1, true));
        panelNorte.add(btnAdminCliente);

        JButton btnAltaProveedor = new JButton("Alta Proveedor");
        btnAltaProveedor.setPreferredSize(new Dimension(130, 40));
        btnAltaProveedor.setBackground(Color.decode("#A4C5E1"));
        btnAltaProveedor.setBorder(new LineBorder(Color.BLACK, 1, true));
        panelNorte.add(btnAltaProveedor);

        JButton btnReportes = new JButton("Reportes de ventas");
        btnReportes.setPreferredSize(new Dimension(130, 40));
        btnReportes.setBackground(Color.decode("#A4C5E1"));
        btnReportes.setBorder(new LineBorder(Color.BLACK, 1, true));
        panelNorte.add(btnReportes);

        // Acción para abrir el panel AltaUsuario
        btnAltaUsuario.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Alta de Usuario", true);
            dialog.setContentPane(new AltaUsuario());
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });

        // Acción para abrir el panel AltaUsuario
        btnAdminUsuario.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Administrar Usuario", true);
            dialog.setContentPane(new AdministarUsuario());
            dialog.setSize(600, 500);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });

        // Acción para abrir el panel AltaCliente
        btnAltaCliente.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Alta de Cliente", true);
            dialog.setContentPane(new AltaCliente());
            dialog.setSize(900, 500);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });

        // Acción para abrir el panel AdministarCliente
        btnAdminCliente.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Administrar Cliente", true);
            dialog.setContentPane(new AdministrarCliente());
            dialog.setSize(1200, 500);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });

        // Acción para abrir el panel AltaProducto
        btnAltaProducto.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Alta de Producto", true);
            dialog.setContentPane(new AltaProducto());
            dialog.setSize(800, 500);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            cargarProductos();
        });

        // Acción para abrir el panel AltaCliente
        btnAltaProveedor.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Alta de Proveedor", true);
            dialog.setContentPane(new AltaProveedor());
            dialog.setSize(900, 500);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });

        // Acción para abrir el panel ReporteVentasPanel
        btnReportes.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Reporte de Ventas", true);
            dialog.setContentPane(new ReporteVentasPanel());
            dialog.setSize(1100, 600); // puedes ajustar tamaño según diseño
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });

        // Espacio flexible
        panelNorte.add(Box.createHorizontalGlue());

        // Campo de fecha
        txtFecha = new JTextField(12);
        txtHora = new JTextField(12);
        txtFecha.setBackground(Color.decode("#A4C5E1"));
        txtHora.setBackground(Color.decode("#A4C5E1"));
        Funciones.inicializarFechaYHora(txtFecha, txtHora);
        panelNorte.add(new JLabel("Fecha"));
        panelNorte.add(txtFecha);
        // panelNorte.add(txtHora); // Puedes agregar la hora si lo deseas

        // Logo a la derecha
        ImageIcon imageIcon = new ImageIcon("electropolar\\src\\main\\java\\presentacion\\logo.png");
        Image imagenEscalada = imageIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(imagenEscalada));
        logo.setBounds(320, 5, 64, 50);
        panelNorte.add(logo);

        add(panelNorte, BorderLayout.NORTH);

         // --------------------- PANEL CENTRO ----------------------
        String[] columnas = {"Clave", "Nombre", "Descripción", "Unidad", "Precio", "Stock", "Proveedor"};
        LogicaAdmin.ProductosTableModel modelo = new LogicaAdmin.ProductosTableModel(columnas, 0);
        tablaProductos = new JTable(modelo);
        this.modeloTabla = modelo;

        tablaProductos.setRowHeight(24);
        tablaProductos.setBackground(Color.decode("#FFFFFF"));

        // Cargar proveedores en JComboBox
        ValidacionesBD dao = new ValidacionesBD();
        List<Proveedor> listaProveedores = dao.cargarProveedores();
        JComboBox<Proveedor> comboProveedores = new JComboBox<>();
        for (Proveedor p : listaProveedores) {
            comboProveedores.addItem(p);
        }
        // Asignar el JComboBox como editor en la columna “Proveedor”
        TableColumn colProveedor = tablaProductos.getColumnModel().getColumn(6);
        colProveedor.setCellEditor(new DefaultCellEditor(comboProveedores));

        tablaProductos.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirmarEdicion");
        tablaProductos.getActionMap().put("confirmarEdicion", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                logic.confirmarODescartarEdicionProd(tablaProductos);
            }
        });


        // Encabezado
        JTableHeader header = tablaProductos.getTableHeader();
        header.setBackground(Color.decode("#7CBBBB"));
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Celdas centradas
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tablaProductos.getColumnCount(); i++) {
            tablaProductos.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }

        // Panel con scroll
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(Color.decode("#FFFFFF"));
        panelCentro.setBorder(new EmptyBorder(5, 10, 5, 10));
        panelCentro.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);

        // Cargar productos desde la base de datos
        cargarProductos();

        // ---------------------- PANEL SUR ------------------------
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.setBackground(Color.decode("#7CBBBB"));
        panelSur.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnModificar = new JButton("Modificar Producto");
        btnEliminar = new JButton("Eliminar Producto");

       btnModificar.addActionListener(e -> logic.iniciarEdicionProducto(tablaProductos));

       btnEliminar.addActionListener(e -> logic.eliminarProductoSeleccionado(tablaProductos));
        Dimension tamBoton = new Dimension(100, 30);
        for (JButton b : new JButton[] { btnModificar, btnEliminar }) {
            b.setPreferredSize(tamBoton);
            b.setBackground(Color.decode("#A4C5E1"));
            b.setBorder(new LineBorder(Color.BLACK, 1, true));
            panelSur.add(b);
        }

        add(panelSur, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Carga productos desde la BD y los coloca en la tabla.
     */
    public void cargarProductos() {
    modeloTabla.setRowCount(0); // limpiar tabla
    ValidacionesBD dao = new ValidacionesBD();

    for (Producto p : dao.obtenerTodosProductos()) {
        //  Obtener el nombre del proveedor 
        String nombreProveedor = dao.obtenerNombreProveedorPorId(p.getIdproveedor());

        //  Crear la columna de proveedor como "ID - Nombre"
        String proveedorTexto = p.getIdproveedor() + " - " + nombreProveedor;

        modeloTabla.addRow(new Object[] {
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getUnidad(),
                String.format("$%.2f", p.getCostoPromedio()),
                p.getStock(),
                proveedorTexto
        });
    }
}


}
