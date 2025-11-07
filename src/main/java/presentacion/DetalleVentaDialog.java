// presentacion/DetalleVentaDialog.java
package presentacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.electropolar.DetalleVenta;
import Datos.ValidacionesBD;

public class DetalleVentaDialog extends JDialog {
    public DetalleVentaDialog(Window owner, int idVenta) {
        super(owner, "Detalle Venta #" + idVenta, ModalityType.APPLICATION_MODAL);
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(5, 5));

        // Encabezado
        JPanel hdr = new JPanel(new GridLayout(2, 2, 5, 5));
        hdr.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        hdr.add(new JLabel("Folio: " + idVenta));
        hdr.add(new JLabel("Fecha: ")); // podrías obtener fecha completa si quieres
        hdr.add(new JLabel("Cliente: ")); // idem
        hdr.add(new JLabel("Vendedor:"));
        add(hdr, BorderLayout.NORTH);

        // Tabla de detalle
        String[] cols = { "Clave", "Cantidad", "P.Unit", "Subtotal" };
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable tbl = new JTable(m);
        List<DetalleVenta> dets = new ValidacionesBD().obtenerDetalleVenta(idVenta);
        dets.forEach(d -> m.addRow(new Object[] {
                d.getIdProducto(),
                d.getCantidad(),
                String.format("%.2f", d.getPrecioUnitario()),
                String.format("%.2f", d.getSubtotal())
        }));
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        // Pie con total
        double total = dets.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        JLabel lblTotal = new JLabel("TOTAL: $" + String.format("%.2f", total), SwingConstants.RIGHT);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        add(lblTotal, BorderLayout.SOUTH);
    }
}
