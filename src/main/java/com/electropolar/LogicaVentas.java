package com.electropolar;

import java.awt.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Datos.ValidacionesBD;

public class LogicaVentas {
    private final JTextField txtClave, txtNombre, txtDescripcion, txtExistencia, txtUnidad,
            txtCantidad, txtDcto, txtPrecioUnit, txtTotalPagar;
    private final JTable tablaVenta;
    private final JFrame parentFrame;
    private final List<DetalleVenta> detallesEnMemoria = new ArrayList<>();

    public LogicaVentas(JFrame parent, JTextField txtClave, JTextField txtNombre, JTextField txtDescripcion,
            JTextField txtExistencia, JTextField txtUnidad, JTextField txtCantidad,
            JTextField txtDcto, JTextField txtPrecioUnit, JTextField txtTotalPagar, JTable tablaVenta) {
        this.parentFrame = parent;
        this.txtClave = txtClave;
        this.txtNombre = txtNombre;
        this.txtDescripcion = txtDescripcion;
        this.txtExistencia = txtExistencia;
        this.txtUnidad = txtUnidad;
        this.txtCantidad = txtCantidad;
        this.txtDcto = txtDcto;
        this.txtPrecioUnit = txtPrecioUnit;
        this.txtTotalPagar = txtTotalPagar;
        this.tablaVenta = tablaVenta;
    }

    /**
     * Busca un producto en base a la clave proporcionada y llena los campos si se
     * encuentra.
     */
    public void buscarYMostrarProducto() {
        String clave = txtClave.getText().trim().toUpperCase();
        Producto producto = new ValidacionesBD().buscarProductoPorClave(clave);

        if (producto != null) {
            cargarDatosProducto(producto);
            txtCantidad.requestFocus();
        } else {
            new presentacion.ErrorClaveInvalida(parentFrame).setVisible(true);
            limpiarCamposProducto();
        }
    }

    /** Procesa la cantidad ingresada y agrega el producto a la tabla de venta. */
    public void procesarCantidad() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText());
            double precioUnitario = Double.parseDouble(txtPrecioUnit.getText());
            double descuento = Double.parseDouble(txtDcto.getText());
            int existencia = Integer.parseInt(txtExistencia.getText());

            if (cantidad <= 0) {
                limpiarCamposProducto();
                return;
            }

            if (cantidad > existencia) {
                JOptionPane.showMessageDialog(parentFrame, "Cantidad excede el stock disponible.",
                        "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cantidad >= 10) {
                precioUnitario *= 0.8; // Aplicar precio de mayoreo
            }

            double totalProducto = (precioUnitario * cantidad) * (1 - descuento / 100.0);

            agregarProductoATabla(cantidad, precioUnitario, descuento, totalProducto);

            actualizarTotalVenta();
            limpiarCamposProducto();
            txtClave.requestFocus();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(parentFrame, "Error en los datos. Verifica la cantidad o el descuento.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Actualiza el total acumulado de la venta. */
    public void actualizarTotalVenta() {
        double total = 0.0;
        DefaultTableModel modelo = (DefaultTableModel) tablaVenta.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            total += Double.parseDouble(modelo.getValueAt(i, 6).toString());
        }
        txtTotalPagar.setText(String.format("%.2f", total));
    }

    /** Limpia los campos de producto (excepto el total acumulado). */
    public void limpiarCamposProducto() {
        txtClave.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtExistencia.setText("");
        txtUnidad.setText("");
        txtCantidad.setText("1");
        txtDcto.setText("0");
        txtPrecioUnit.setText("");
    }

    /** Carga los datos de un producto a los campos correspondientes. */
    public void cargarDatosProducto(Producto producto) {
        txtClave.setText(producto.getId());
        txtNombre.setText(producto.getNombre());
        txtDescripcion.setText(producto.getDescripcion());
        txtExistencia.setText(String.valueOf(producto.getStock()));
        txtUnidad.setText(producto.getUnidad());
        txtCantidad.setText("1");
        txtDcto.setText("0");
        txtPrecioUnit.setText(String.format("%.2f", producto.getPrecio()));
    }

    /** Agrega un producto procesado a la tabla de venta. */
    private void agregarProductoATabla(int cantidad, double precioUnitario, double descuento, double totalProducto) {
        // 1) Reconstruir el producto a partir de los campos de texto
        Producto producto = new Producto(
                txtClave.getText(),
                txtNombre.getText(),
                txtDescripcion.getText(),
                txtUnidad.getText(),
                Double.parseDouble(txtPrecioUnit.getText()),
                Integer.parseInt(txtExistencia.getText()));

        // 2) Crear el DetalleVenta usando el idProducto como String
        double subtotal = (precioUnitario * cantidad) * (1 - descuento / 100.0);
        DetalleVenta detalle = new DetalleVenta(
                0,
                0,
                producto.getId(),
                cantidad,
                precioUnitario);
        detalle.setSubtotal(subtotal);

        // 3) Agregar una fila a la tabla de venta
        DefaultTableModel modelo = (DefaultTableModel) tablaVenta.getModel();
        modelo.addRow(new Object[] {
                producto.getId(),
                producto.getNombre(),
                cantidad,
                producto.getUnidad(),
                String.format("%.2f", precioUnitario),
                descuento,
                String.format("%.2f", detalle.getSubtotal())
        });

        // 4) (Opcional) Si mantienes una lista de DetalleVenta en memoria,
        // agrégalo ahí para luego registrar la venta completa:
        detallesEnMemoria.add(detalle);
    }

    /** Confirma con el usuario y finaliza la venta. */
    public void confirmarYFinalizarVenta(Cliente cliente, Vendedor vendedor) {
        int opt = JOptionPane.showConfirmDialog(parentFrame,
                "¿Deseas finalizar la venta y generar el ticket?", "Confirmar venta", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            finalizarVenta(cliente, vendedor);
        }
    }

    /** Finaliza la venta: guarda en BD, genera ticket PDF y limpia. */
    private void finalizarVenta(Cliente cliente, Vendedor vendedor) {
    if (detallesEnMemoria.isEmpty()) {
        JOptionPane.showMessageDialog(parentFrame, "No hay productos para vender.", "Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }
    try {
        double totalVenta = Double.parseDouble(txtTotalPagar.getText());
        int idUsuario = vendedor.getId();
        int idCliente = cliente.getIdCliente();

        ValidacionesBD  dao = new ValidacionesBD();
        int idVentaGen = dao.finalizarVenta(totalVenta, idUsuario, idCliente, detallesEnMemoria);
        if (idVentaGen <= 0)
            throw new RuntimeException("Error al insertar venta.");

        Venta venta = new Venta(idVentaGen, idCliente, LocalDateTime.now(), totalVenta, detallesEnMemoria);
        String ruta = "tickets/Ticket_Venta_" + idVentaGen + ".pdf";
        TicketPDFGenerator.generarPDF(venta, cliente, vendedor, ruta);

        JOptionPane.showMessageDialog(parentFrame, "Venta exitosa. Ticket: " + ruta, "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

        // Limpiar tabla y campos
        ((DefaultTableModel) tablaVenta.getModel()).setRowCount(0);
        detallesEnMemoria.clear();
        txtTotalPagar.setText("0.00");

        //Actualizar folio después de cada venta
        try {
            int siguienteFolio = dao.obtenerNextFolio();
            // Busca si en la ventana existe un campo txtFolio
            Component[] componentes = parentFrame.getContentPane().getComponents();
            for (Component comp : componentes) {
                if (comp instanceof JTextField && ((JTextField) comp).getName() != null
                        && ((JTextField) comp).getName().equals("txtFolio")) {
                    ((JTextField) comp).setText(String.valueOf(siguienteFolio));
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println("No se pudo actualizar el folio: " + ex.getMessage());
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(parentFrame,
                "Error al finalizar la venta: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    /**
     * Agrega un listener de doble clic en una tabla de productos para seleccionar
     * uno.
     */
    public void agregarListenerDobleClickTabla(JTable tablaBuscarProducto) {
        tablaBuscarProducto.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tablaBuscarProducto.getSelectedRow() != -1) {
                    int fila = tablaBuscarProducto.getSelectedRow();
                    String clave = tablaBuscarProducto.getValueAt(fila, 0).toString().trim();

                    // Si clave está vacía, es la fila de "No se encontraron..." → ignorar
                    if (clave.isEmpty()) {
                        return;
                    }

                    String nombre = tablaBuscarProducto.getValueAt(fila, 1).toString();
                    String descripcion = tablaBuscarProducto.getValueAt(fila, 2).toString();
                    String unidad = tablaBuscarProducto.getValueAt(fila, 3).toString();
                    int stock = Integer.parseInt(tablaBuscarProducto.getValueAt(fila, 4).toString());

                    String precioStr = tablaBuscarProducto.getValueAt(fila, 5).toString()
                            .replace("$", "").replace(",", "").trim();
                    double precio = Double.parseDouble(precioStr);

                    Producto producto = new Producto(clave, nombre, descripcion, unidad, precio, stock);
                    cargarDatosProducto(producto);

                    // Cerrar el diálogo o panel de búsqueda
                    SwingUtilities.getWindowAncestor(tablaBuscarProducto).dispose();
                    txtCantidad.requestFocus();
                }
            }
        });
    }

    public void cancelarVentaEnCurso() {
        int filas = tablaVenta.getRowCount();
        if (filas == 0) {
            JOptionPane.showMessageDialog(parentFrame, "No hay productos en la venta para cancelar.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Limpiar tabla y datos
        ((DefaultTableModel) tablaVenta.getModel()).setRowCount(0);
        detallesEnMemoria.clear();
        txtTotalPagar.setText("0.00");
        limpiarCamposProducto();

        JOptionPane.showMessageDialog(parentFrame, "Venta cancelada con éxito.",
                "Venta cancelada", JOptionPane.INFORMATION_MESSAGE);
    }

}
