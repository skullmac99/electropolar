package presentacion;

import Datos.ValidacionesBD;
//import com.electropolar.LogicaVentas;
//import com.electropolar.Producto;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.electropolar.LogicaVentas;

import java.awt.*;
import java.util.List;

public class BuscarProducto extends JPanel {

    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modelo;
    private ValidacionesBD validaciones = new ValidacionesBD();
    //private LogicaVentas logicaVentas;

    public BuscarProducto(LogicaVentas logicaVentas) {
       

        setLayout(new BorderLayout());
        setBackground(Color.decode("#FFFFFF"));

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout());
        panelBusqueda.setBackground(Color.decode("#B5EFE3"));
        panelBusqueda.setBorder(new LineBorder(Color.BLACK, 1, true));

        JLabel titulo = new JLabel("Buscar Producto", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelBusqueda.add(titulo, BorderLayout.NORTH);

        JPanel filaBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER));
        filaBusqueda.setBackground(Color.decode("#B5EFE3"));
        filaBusqueda.add(new JLabel("Nombre del producto"));
        txtBuscar = new JTextField(25);
        txtBuscar.setBackground(Color.decode("#A4C5E1"));
        filaBusqueda.add(txtBuscar);
        panelBusqueda.add(filaBusqueda, BorderLayout.CENTER);

        ImageIcon imageIcon = new ImageIcon("electropolar\\src\\main\\java\\presentacion\\logo.png");
        Image imagenEscalada = imageIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(imagenEscalada));
        panelBusqueda.add(imageLabel, BorderLayout.EAST);

        add(panelBusqueda, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Clave", "Nombre", "Desc", "Unidad", "Stock", "Precio Un", "Precio Myo"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setBackground(Color.decode("#7CBBBB"));
        tabla.setBackground(Color.decode("#FFFFFF"));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(tabla);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.decode("#FFFFFF"));
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        // Evento de búsqueda dinámica
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarTabla(); }
            public void removeUpdate(DocumentEvent e) { actualizarTabla(); }
            public void changedUpdate(DocumentEvent e) { actualizarTabla(); }
        });

        // conectamos el listener de doble clic desde LogicaVentas
        logicaVentas.agregarListenerDobleClickTabla(tabla);

        txtBuscar.setBackground(Color.decode("#A4C5E1"));
        txtBuscar.setBorder(new LineBorder(Color.BLACK, 1, true));
    }

    private void actualizarTabla() {
        String texto = txtBuscar.getText().trim();
        modelo.setRowCount(0); // limpia la tabla
        List<Object[]> productos = validaciones.buscarProductos(texto);

        if (productos.isEmpty()) {
            modelo.addRow(new Object[]{"", "No se encontraron productos con ese criterio", "", "", "", ""});
        } else {
            for (Object[] fila : productos) {
                modelo.addRow(fila);
            }
        }
    }
}
