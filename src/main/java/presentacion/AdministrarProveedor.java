package presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.electropolar.LogicaAdmin;
import com.electropolar.Proveedor;

import Datos.ValidacionesBD;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class AdministrarProveedor extends JPanel {

    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla; // 1) campo de instancia
    private JButton btnModificar;
    private JButton btnCancelar;
    LogicaAdmin logic = new LogicaAdmin();

    public AdministrarProveedor() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FFFFFF")); // Fondo principal

        // --- Panel superior: título y logo ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#B5EFE3"));
        panelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Administrar Proveedor", SwingConstants.CENTER);
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
                "ID", "Nombre", "RFC", "Correo", "Teléfono", "Estado", "Ciudad",
                "Calle", "Colonia", "NoExt", "NoInt", "CP",
                "Municipio", "País", "Estatus"
        };
        // 2) Inicializamos modeloTabla **antes** de llamar a cargarUsuarios()
        // 1) Crea tu modelo específico
        LogicaAdmin.ProveedoresTableModel modelo = new LogicaAdmin.ProveedoresTableModel(columnas, 0);
        tablaProveedores = new JTable(modelo);
        this.modeloTabla = modelo; // para usarlo en cargarClientes()

        tablaProveedores.setRowHeight(24);
        tablaProveedores.setBackground(Color.decode("#FFFFFF"));

        // Ocultar columna ID:
        TableColumn colId = tablaProveedores.getColumnModel().getColumn(0);
        colId.setMinWidth(0);
        colId.setMaxWidth(0);
        colId.setPreferredWidth(0);
        colId.setResizable(false);

        tablaProveedores.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirmarEdicion");
        tablaProveedores.getActionMap().put("confirmarEdicion", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                logic.confirmarODescartarEdicionProveedores(tablaProveedores);
            }
        });

        // Encabezado centrado y con color
        JTableHeader header = tablaProveedores.getTableHeader();
        header.setBackground(Color.decode("#7CBBBB"));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        add(new JScrollPane(tablaProveedores), BorderLayout.CENTER);
        // 4) Ahora sí, cargamos los datos en el modelo
        cargarProveedores();

        // --- Panel inferior: botones ---
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        panelSur.setBackground(Color.decode("#7CBBBB"));
        panelSur.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnModificar = new JButton("Modificar proveedor");
        btnCancelar = new JButton("Cancelar");

        btnModificar.addActionListener(e -> logic.iniciarEdicionProveedor(tablaProveedores));

        btnCancelar.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(AdministrarProveedor.this);
            if (window instanceof JDialog) {
                ((JDialog) window).dispose();
            } else if (window instanceof JFrame) {
                ((JFrame) window).dispose();
            }
        });

        Dimension tamBoton = new Dimension(100, 30);
        for (JButton b : new JButton[] { btnModificar, btnCancelar }) {
            b.setPreferredSize(tamBoton);
            b.setBackground(Color.decode("#A4C5E1"));
            b.setBorder(new LineBorder(Color.BLACK, 1, true));
            panelSur.add(b);
        }

        add(panelSur, BorderLayout.SOUTH);

    }

    public void cargarProveedores() {
        modeloTabla.setRowCount(0);
        for (Proveedor p : new ValidacionesBD().obtenerTodosProveedores()) {
            modeloTabla.addRow(new Object[] {
                    p.getIdProveedor(), // 0 → ID (opulta)
                    p.getNombre(), // 1 → Nombre
                    p.getRfc(), // 2 → RFC
                    p.getCorreo(), // 3 → Correo
                    p.getTelefono(), // 4 → Teléfono
                    p.getEstado(), // 11 → Estado
                    p.getCiudad(), // 11 → Estado
                    p.getCalle(), // 5 → Calle
                    p.getColonia(), // 6 → Colonia
                    p.getNumeroExte(), // 8 → NoInt ← intercambiamos
                    p.getNumeroInt(), // 9 → CP ← intercambiamos
                    p.getCodigoPost(), // 7 → NoExt ← intercambiamos
                    p.getMunicipio(), // 10 → Municipio
                    p.getPais(), // 12 → País
                    p.getEstatus() // 13 → CFDI
            });
        }
    }

    /** Permite acceder a la tabla para cargar datos o añadir listeners */
    public JTable getTablaUsuarios() {
        return tablaProveedores;
    }

    public JButton getBtnModificar() {
        return btnModificar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

}
