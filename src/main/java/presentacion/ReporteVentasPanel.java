package presentacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.electropolar.Cliente;
import com.electropolar.Vendedor;
import com.electropolar.Venta;
import Datos.ValidacionesBD;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReporteVentasPanel extends JPanel {

    private JSpinner fechaDesde, fechaHasta;
    private JButton btnConsultar;
    private JTable tblVentas;
    private DefaultTableModel modelo;
    private JComboBox<Cliente> comboCliente;
    private JComboBox<Vendedor> comboVendedor;

    private final ValidacionesBD dao = new ValidacionesBD();

    public ReporteVentasPanel() {
        setLayout(new BorderLayout(5, 5));
        setBackground(Color.decode("#FFFFFF")); // color de fondo principal
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // -------- Panel de filtros --------
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlFiltros.setBackground(Color.decode("#B5EFE3"));
        pnlFiltros.setBorder(new LineBorder(Color.GRAY, 1, true));

        Font fuente = new Font("SansSerif", Font.PLAIN, 14);
        Color fondoCampo = Color.decode("#A4C5E1");

        // Fechas
        fechaDesde = new JSpinner(new SpinnerDateModel());
        fechaHasta = new JSpinner(new SpinnerDateModel());
        JSpinner[] fechas = { fechaDesde, fechaHasta };
        for (JSpinner s : fechas) {
            s.setFont(fuente);
            s.setPreferredSize(new Dimension(120, 25));
        }

        pnlFiltros.add(new JLabel("Desde:"));
        pnlFiltros.add(fechaDesde);
        pnlFiltros.add(new JLabel("Hasta:"));
        pnlFiltros.add(fechaHasta);

        // Combo Vendedor
        pnlFiltros.add(new JLabel("Vendedor:"));
        comboVendedor = new JComboBox<>();
        comboVendedor.setFont(fuente);
        comboVendedor.setBackground(fondoCampo);
        comboVendedor.setPreferredSize(new Dimension(180, 25));
        for (Vendedor v : dao.cargarVendedores()) comboVendedor.addItem(v);
        pnlFiltros.add(comboVendedor);

        // Combo Cliente
        pnlFiltros.add(new JLabel("Cliente:"));
        comboCliente = new JComboBox<>();
        comboCliente.setFont(fuente);
        comboCliente.setBackground(fondoCampo);
        comboCliente.setPreferredSize(new Dimension(180, 25));
        for (Cliente c : dao.cargarClientes()) comboCliente.addItem(c);
        pnlFiltros.add(comboCliente);

        // Botón consultar
        btnConsultar = new JButton("Consultar");
        btnConsultar.setFont(fuente);
        btnConsultar.setBackground(fondoCampo);
        btnConsultar.setPreferredSize(new Dimension(110, 30));
        btnConsultar.setBorder(new LineBorder(Color.BLACK, 1, true));
        btnConsultar.addActionListener(e -> cargarVentas());
        pnlFiltros.add(btnConsultar);

        add(pnlFiltros, BorderLayout.NORTH);

        // -------- Tabla de resultados --------
        String[] columnas = { "ID", "Fecha", "Vendedor", "Cliente", "Total" };
        modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblVentas = new JTable(modelo);
        tblVentas.setRowHeight(24);
        tblVentas.setFont(fuente);
        tblVentas.setBackground(Color.decode("#FFFFFF"));

        // Encabezado de tabla
        JTableHeader header = tblVentas.getTableHeader();
        header.setBackground(Color.decode("#7CBBBB"));
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Celdas centradas
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tblVentas.getColumnCount(); i++) {
            tblVentas.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }

        JScrollPane scroll = new JScrollPane(tblVentas);
        scroll.setBorder(new EmptyBorder(10, 0, 0, 0));
        add(scroll, BorderLayout.CENTER);

        // Doble clic en fila para mostrar detalles
        tblVentas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblVentas.getSelectedRow() != -1) {
                    int id = Integer.parseInt(modelo.getValueAt(tblVentas.getSelectedRow(), 0).toString());
                    Window parent = SwingUtilities.getWindowAncestor(ReporteVentasPanel.this);
                    DetalleVentaDialog dlg = new DetalleVentaDialog(parent, id);
                    dlg.setLocationRelativeTo(parent);
                    dlg.setVisible(true);
                }
            }
        });
    }

    private void cargarVentas() {
        LocalDate d1 = LocalDate.ofInstant(
                ((SpinnerDateModel) fechaDesde.getModel()).getDate().toInstant(),
                ZoneId.systemDefault());
        LocalDate d2 = LocalDate.ofInstant(
                ((SpinnerDateModel) fechaHasta.getModel()).getDate().toInstant(),
                ZoneId.systemDefault());

        Integer idV = comboVendedor.getSelectedIndex() > 0
                ? ((Vendedor) comboVendedor.getSelectedItem()).getId()
                : null;
        Integer idC = comboCliente.getSelectedIndex() > 0
                ? ((Cliente) comboCliente.getSelectedItem()).getIdCliente()
                : null;

        List<Venta> ventas = dao.obtenerVentas(d1, d2, idV, idC);
        modelo.setRowCount(0);
        for (Venta v : ventas) {
            modelo.addRow(new Object[]{
                    v.getIdVenta(),
                    v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    v.getVendedorNombre(),
                    v.getClienteNombre(),
                    String.format("$%.2f", v.getTotal())
            });
        }
    }
}
