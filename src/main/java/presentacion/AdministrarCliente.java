package presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.electropolar.Cliente;
import com.electropolar.LogicaAdmin;

import Datos.ValidacionesBD;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class AdministrarCliente extends JPanel {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla; // 1) campo de instancia
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnCancelar;
    LogicaAdmin logic = new LogicaAdmin();

    public AdministrarCliente() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FFFFFF")); // Fondo principal

        // --- Panel superior: título y logo ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#B5EFE3"));
        panelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Administrar Cliente", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelSuperior.add(titulo, BorderLayout.CENTER);

        // Logo a la derecha
        ImageIcon icono = new ImageIcon(getClass().getResource("/logo.png"));
        Image img = icono.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(img));
        panelSuperior.add(lblLogo, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // --- Panel central: tabla de usuarios ---
        String[] columnas = {
                "ID", "Nombre", "RFC", "Correo", "Teléfono",
                "Calle", "Colonia", "NoExt", "NoInt", "CP",
                "Municipio", "Estado", "País", "CFDI"
        };
        // 2) Inicializamos modeloTabla **antes** de llamar a cargarUsuarios()
        // 1) Crea tu modelo específico
        LogicaAdmin.ClienteTableModel modelo = new LogicaAdmin.ClienteTableModel(columnas, 0);
        tablaClientes = new JTable(modelo);
        this.modeloTabla = modelo; // para usarlo en cargarClientes()

        tablaClientes.setRowHeight(24);
        tablaClientes.setBackground(Color.decode("#FFFFFF"));

        // Ocultar columna ID:
        TableColumn colId = tablaClientes.getColumnModel().getColumn(0);
        colId.setMinWidth(0);
        colId.setMaxWidth(0);
        colId.setPreferredWidth(0);
        colId.setResizable(false);

        tablaClientes.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirmarEdicion");
        tablaClientes.getActionMap().put("confirmarEdicion", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                logic.confirmarODescartarEdicion(tablaClientes);
            }
        });

        // Encabezado centrado y con color
        JTableHeader header = tablaClientes.getTableHeader();
        header.setBackground(Color.decode("#7CBBBB"));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        // 4) Ahora sí, cargamos los datos en el modelo
        cargarClientes();

        // --- Panel inferior: botones ---
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        panelSur.setBackground(Color.decode("#7CBBBB"));
        panelSur.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnModificar = new JButton("Modificar cliente");
        btnEliminar = new JButton("Eliminar cliente");
        btnCancelar = new JButton("Cancelar");

        btnModificar.addActionListener(e -> logic.iniciarEdicionCliente(tablaClientes));

        btnEliminar.addActionListener(e -> {
            LogicaAdmin logic = new LogicaAdmin();
            logic.eliminarClienteDesdeTabla(tablaClientes);
        });

        btnCancelar.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(AdministrarCliente.this);
            if (window instanceof JDialog) {
                ((JDialog) window).dispose();
            } else if (window instanceof JFrame) {
                ((JFrame) window).dispose();
            }
        });

        Dimension tamBoton = new Dimension(100, 30);
        for (JButton b : new JButton[] { btnModificar, btnEliminar, btnCancelar }) {
            b.setPreferredSize(tamBoton);
            b.setBackground(Color.decode("#A4C5E1"));
            b.setBorder(new LineBorder(Color.BLACK, 1, true));
            panelSur.add(b);
        }

        add(panelSur, BorderLayout.SOUTH);

    }

    public void cargarClientes() {
    modeloTabla.setRowCount(0);
    for (Cliente c : new ValidacionesBD().obtenerTodosClientes()) {
        modeloTabla.addRow(new Object[] {
            c.getIdCliente(),      // 0 → ID (oculta)
            c.getNombreCliente(),  // 1 → Nombre
            c.getRfc(),            // 2 → RFC
            c.getCorreo(),         // 3 → Correo
            c.getTelefono(),       // 4 → Teléfono
            c.getCalle(),          // 5 → Calle
            c.getColonia(),        // 6 → Colonia
            c.getNoExt(),          // 8 → NoInt  ← intercambiamos
            c.getNoInt(),          // 9 → CP     ← intercambiamos
            c.getCp(),             // 7 → NoExt  ← intercambiamos
            c.getMunicipio(),      // 10 → Municipio
            c.getEstado(),         // 11 → Estado
            c.getPais(),           // 12 → País
            c.getCfdi()            // 13 → CFDI
        });
    }
}
    /** Permite acceder a la tabla para cargar datos o añadir listeners */
    public JTable getTablaUsuarios() {
        return tablaClientes;
    }

    public JButton getBtnModificar() {
        return btnModificar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

}
