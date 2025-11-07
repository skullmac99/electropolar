package presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.electropolar.LogicaAdmin;
import com.electropolar.Usuarios;

import Datos.ValidacionesBD;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class AdministarUsuario extends JPanel {

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnCancelar;
    LogicaAdmin logic = new LogicaAdmin();

    public AdministarUsuario() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FFFFFF")); // Fondo principal

        // --- Panel superior: título y logo ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#B5EFE3"));
        panelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Administrar Usuario", SwingConstants.CENTER);
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
            "IdCliente","Nombre", "Apellido Paterno", "Apellido Materno", "Rol"
        };
        LogicaAdmin.UsuarioTableModel modelo = new LogicaAdmin.UsuarioTableModel(columnas, 0);
        tablaUsuarios = new JTable(modelo);
        this.modeloTabla = modelo; // para usarlo en cargarClientes()

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setRowHeight(24);
        tablaUsuarios.setBackground(Color.decode("#FFFFFF"));

        // Ocultar columna ID:
        TableColumn colId = tablaUsuarios.getColumnModel().getColumn(0);
        colId.setMinWidth(0);
        colId.setMaxWidth(0);
        colId.setPreferredWidth(0);
        colId.setResizable(false);


        tablaUsuarios.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirmarEdicion");
        tablaUsuarios.getActionMap().put("confirmarEdicion", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                logic.confirmarODescartarEdicionUs(tablaUsuarios);
            }
        });

        // Encabezado centrado y con color
        JTableHeader header = tablaUsuarios.getTableHeader();
        header.setBackground(Color.decode("#7CBBBB"));
        ((DefaultTableCellRenderer)header.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        scroll.setBorder(new LineBorder(Color.GRAY));

        add(scroll, BorderLayout.CENTER);
        cargarUsuarios();

        // --- Panel inferior: botones ---
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        panelSur.setBackground(Color.decode("#7CBBBB"));
        panelSur.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnModificar = new JButton("Modificar usuario");
        btnEliminar  = new JButton("Eliminar usuario");
        btnCancelar  = new JButton("Cancelar");

        btnModificar.addActionListener(e -> logic.iniciarEdicionUsurio(tablaUsuarios));

         btnEliminar.addActionListener(e -> {
            LogicaAdmin logic = new LogicaAdmin();
            logic.eliminarUsuarioDesdeTabla(tablaUsuarios);
        });
       
        btnCancelar.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(AdministarUsuario.this);
            if (window != null) {
                window.dispose();
            }
        });

        Dimension tamBoton = new Dimension(100, 30);
        for (JButton b : new JButton[]{btnModificar, btnEliminar, btnCancelar}) {
            b.setPreferredSize(tamBoton);
            b.setBackground(Color.decode("#A4C5E1"));
            b.setBorder(new LineBorder(Color.BLACK, 1, true));
            panelSur.add(b);
        }

        add(panelSur, BorderLayout.SOUTH);
    }


   public void cargarUsuarios() {
        modeloTabla.setRowCount(0);
        for (Usuarios u : new ValidacionesBD().obtenerTodosUsuarios()) {
            modeloTabla.addRow(new Object[]{
                u.getIdUsuario(),
                u.getNombre(),
                u.getApellidoPat(),
                u.getApellidoMat(),
                u.getRolUsuario()
            });
        }
    }

    /** Permite acceder a la tabla para cargar datos o añadir listeners */
    public JTable getTablaUsuarios() {
        return tablaUsuarios;
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
