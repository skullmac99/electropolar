package presentacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

import com.electropolar.DetalleVenta;
import Datos.ValidacionesBD;

public class CancelarTicket extends JPanel {
    private final JTextField txtFolio;
    private final JTable tabla;
    private final DefaultTableModel modelo;
    private final JButton btnCargar, btnAccion;

    public CancelarTicket() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FFFFFF"));

        // ─── PANEL SUPERIOR ────────────────────────────────────────
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.decode("#B5EFE3"));

        JLabel titulo = new JLabel("Cancelar venta", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelSuperior.add(titulo, BorderLayout.NORTH);

        JPanel filaFolio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaFolio.setBackground(Color.decode("#B5EFE3"));
        filaFolio.add(new JLabel("Folio de la venta:"));
        txtFolio = new JTextField(10);
        txtFolio.setBackground(Color.decode("#A4C5E1"));
        filaFolio.add(txtFolio);

        btnCargar = new JButton("Cargar");
        btnCargar.setBackground(Color.decode("#A4C5E1"));
        filaFolio.add(btnCargar);
        panelSuperior.add(filaFolio, BorderLayout.WEST);

        // logo
        ImageIcon ico = new ImageIcon("logo.png");
        Image img = ico.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        panelSuperior.add(new JLabel(new ImageIcon(img)), BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // ─── TABLA DE DETALLES ────────────────────────────────────
        // Columnas ocultas para ID_PRODUCTOS
        String[] cols = { "ID_PRODUCTO", "Nombre", "Cantidad", "Precio Un", "Importe" };
        modelo = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setBackground(Color.decode("#7CBBBB"));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabla.setBackground(Color.decode("#FFFFFF"));

        // Ocultar columna de ID_PRODUCTO
        TableColumn colId = tabla.getColumnModel().getColumn(0);
        colId.setMinWidth(0);
        colId.setMaxWidth(0);
        colId.setPreferredWidth(0);

        JScrollPane scroll = new JScrollPane(tabla);
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.decode("#FFFFFF"));
        panelTabla.add(scroll, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        // ─── BOTONES INFERIORES ───────────────────────────────────
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotones.setBackground(Color.decode("#FFFFFF"));
        btnAccion = new JButton("Cancelar / Ajustar");
        btnAccion.setBackground(Color.decode("#A4C5E1"));
        panelBotones.add(btnAccion);

        add(panelBotones, BorderLayout.SOUTH);

        // ─── LISTENERS ────────────────────────────────────────────
        //btnCargar.addActionListener(e -> cargarDetalles());
        btnAccion.addActionListener(e -> ejecutarCancelacion());
    }

    /*private void cargarDetalles() {
        modelo.setRowCount(0);
        String folio = txtFolio.getText().trim();
        int idVenta;
        try {
            idVenta = Integer.parseInt(folio);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Folio inválido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ValidacionesBD val = new ValidacionesBD();
        List<DetalleVenta> lista = val.obtenerDetallesVenta(idVenta);
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron detalles para ese folio.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (DetalleVenta d : lista) {
            modelo.addRow(new Object[] {
                    d.getIdProducto(),
                    // asumiendo que buscarProductoPorId devuelve el nombre:
                    val.buscarProductoPorId(d.getIdProducto()).getNombre(),
                    d.getCantidad(),
                    String.format("%.2f", d.getPrecioUnitario()),
                    String.format("%.2f", d.getSubtotal())
            });
        }
    }*/

    private void ejecutarCancelacion() {
        String folio = txtFolio.getText().trim();
        int idVenta;
        try {
            idVenta = Integer.parseInt(folio);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Folio inválido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String[] opciones = { "Venta completa", "Quitar producto", "Quitar unidad" };
        int sel = JOptionPane.showOptionDialog(this,
                "¿Qué operación deseas realizar?",
                "Cancelar / Ajustar venta",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);
        ValidacionesBD val = new ValidacionesBD();

        /*switch (sel) {
            case 0: // venta completa
                if (val.cancelarVentaCompleta(idVenta)) {
                    JOptionPane.showMessageDialog(this, "Venta completamentecancelada.");
                    modelo.setRowCount(0);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al cancelar la venta.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;

            case 1: // quitar producto completo
                int fila = tabla.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(this, "Selecciona un producto en la tabla.", "Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String idProd = modelo.getValueAt(fila, 0).toString();
                if (val.cancelarUnProducto(idVenta, idProd)) {
                    JOptionPane.showMessageDialog(this, "Producto eliminado de la venta.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar producto.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                cargarDetalles();
                break;

            case 2: // quitar una unidad
                fila = tabla.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(this, "Selecciona un producto en la tabla.", "Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                idProd = modelo.getValueAt(fila, 0).toString();
                if (val.decrementarUnaUnidad(idVenta, idProd)) {
                    JOptionPane.showMessageDialog(this, "Se ha removido una unidad.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al remover unidad.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                cargarDetalles();
                break;

            default:
                // nada
        }*/
    }

    // Getters por si los necesitas
    public JTextField getTxtFolio() {
        return txtFolio;
    }

    public JTable getTabla() {
        return tabla;
    }
}
