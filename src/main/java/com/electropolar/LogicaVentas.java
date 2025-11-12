package com.electropolar;

// [CAMBIO]: Imports añadidos para que todo funcione
import java.awt.Component;
import java.awt.Frame; // <-- Importante para el constructor
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Datos.ValidacionesBD;
import com.electropolar.Producto; // <-- Importante
import com.electropolar.Cliente;
import com.electropolar.Vendedor;
// (Asegúrate de importar tus clases Venta, DetalleVenta, TicketPDFGenerator)

public class LogicaVentas {
    private final JTextField txtClave, txtNombre, txtDescripcion, txtExistencia, txtUnidad,
            txtCantidad, txtDcto, txtPrecioUnit, txtTotalPagar;
    private final JTable tablaVenta;
    private final JFrame parentFrame; // Tu variable original (está perfecto)
    private final List<DetalleVenta> detallesEnMemoria = new ArrayList<>();

    // [CAMBIO]: Variable declarada para guardar el producto
    private Producto productoSeleccionado;

    public LogicaVentas(JFrame parent, JTextField txtClave, JTextField txtNombre, JTextField txtDescripcion,
            JTextField txtExistencia, JTextField txtUnidad, JTextField txtCantidad,
            JTextField txtDcto, JTextField txtPrecioUnit, JTextField txtTotalPagar, JTable tablaVenta) {

        this.parentFrame = parent; // Tu asignación original (perfecto)
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
     * [MODIFICADO]
     * Busca un producto, CALCULA EL PRECIO DE VENTA y llena los campos.
     */
    public void buscarYMostrarProducto() {
        String clave = txtClave.getText().trim().toUpperCase();
        Producto productoEncontrado = new ValidacionesBD().buscarProductoPorClave(clave);

        if (productoEncontrado != null) {

            // --- ¡AQUÍ ESTÁ LA LÓGICA FALTANTE! ---

            // 1. Obtenemos el COSTO de la BD
            double costo = productoEncontrado.getCostoPromedio();

            // 2. CALCULAMOS el precio de venta (ej. con 50% de ganancia)
            // ¡¡AJUSTA ESTE NÚMERO (1.50) A TU MARGEN DE GANANCIA!!
            double precioDeVenta = costo * 1.50;

            // 3. Guardamos el precio de venta en el objeto
            productoEncontrado.setPrecioVenta(precioDeVenta);

            // 4. Guardamos el producto completo para usarlo después
            this.productoSeleccionado = productoEncontrado;

            // 5. Llama a cargarDatos (que ahora SÍ tiene un precio de venta)
            cargarDatosProducto(productoEncontrado);
            txtCantidad.requestFocus();

        } else {
            // Usa 'parentFrame' que SÍ existe
            new presentacion.ErrorClaveInvalida(parentFrame).setVisible(true);
            limpiarCamposProducto();
            // this.productoSeleccionado = null; // Se limpia en limpiarCamposProducto()
        }
    }

    /**
     * * [MODIFICADO]
     * Procesa la cantidad y usa el 'productoSeleccionado' que ya tenemos.
     */
    public void procesarCantidad() {
        try {
            // Validar que haya un producto seleccionado
            if (this.productoSeleccionado == null || !this.productoSeleccionado.getId().equals(txtClave.getText())) {
                JOptionPane.showMessageDialog(parentFrame, "Error: Producto no válido. Vuelva a buscarlo.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int cantidad = Integer.parseInt(txtCantidad.getText());
            double precioUnitario = Double.parseDouble(txtPrecioUnit.getText());
            double descuento = Double.parseDouble(txtDcto.getText());
            int existencia = this.productoSeleccionado.getStock(); // <-- Saca la existencia del objeto

            if (cantidad <= 0) {
                limpiarCamposProducto();
                return;
            }

            if (cantidad > existencia) {
                JOptionPane.showMessageDialog(parentFrame, "Cantidad excede el stock disponible.",
                        "Stock insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Lógica de mayoreo (del código original)
            // [NOTA]: Esto aplicará el 20% de descuento sobre el PRECIO DE VENTA
            if (cantidad >= 10) {
                precioUnitario *= 0.8;
            }

            double totalProducto = (precioUnitario * cantidad) * (1 - descuento / 100.0);

            // Pasamos el producto completo al método de agregar
            agregarProductoATabla(this.productoSeleccionado, cantidad, precioUnitario, descuento, totalProducto);

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
        // ... (Este método estaba bien)
        double total = 0.0;
        DefaultTableModel modelo = (DefaultTableModel) tablaVenta.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            total += Double.parseDouble(modelo.getValueAt(i, 6).toString());
        }
        txtTotalPagar.setText(String.format("%.2f", total));
    }

    /**
     * * [MODIFICADO]
     * Limpia los campos de producto y el producto guardado.
     */
    public void limpiarCamposProducto() {
        // ... (Este método estaba bien)
        txtClave.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtExistencia.setText("");
        txtUnidad.setText("");
        txtCantidad.setText("1");
        txtDcto.setText("0");
        txtPrecioUnit.setText("");
        this.productoSeleccionado = null; // <-- Limpia el producto guardado
    }

    /**
     * * [MODIFICADO]
     * Carga los datos de un producto a los campos.
     * Ahora usa 'getPrecioVenta()', que ya fue calculado.
     */
    public void cargarDatosProducto(Producto producto) {
        txtClave.setText(producto.getId());
        txtNombre.setText(producto.getNombre());
        txtDescripcion.setText(producto.getDescripcion());
        txtExistencia.setText(String.valueOf(producto.getStock()));
        txtUnidad.setText(producto.getUnidad());
        txtCantidad.setText("1");
        txtDcto.setText("0");
        // [CORRECTO]: Esto ahora mostrará el precio de venta calculado
        txtPrecioUnit.setText(String.format("%.2f", producto.getPrecioVenta()));
    }

    /**
     * * [MODIFICADO]
     * Agrega el producto a la tabla usando el objeto 'Producto',
     * en lugar de reconstruir uno que da error.
     */
    private void agregarProductoATabla(Producto producto, int cantidad, double precioUnitario, double descuento,
            double totalProducto) {

        // 1) Crear el DetalleVenta
        double subtotal = (precioUnitario * cantidad) * (1 - descuento / 100.0);
        DetalleVenta detalle = new DetalleVenta(
                0,
                0,
                producto.getId(), // <-- Usa el ID del producto real
                cantidad,
                precioUnitario);
        detalle.setSubtotal(subtotal); // O 'totalProducto', que es lo mismo

        // 2) Agregar una fila a la tabla de venta
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

        // 3) Guardar en memoria
        detallesEnMemoria.add(detalle);
    }

    /** Confirma con el usuario y finaliza la venta. */
    public void confirmarYFinalizarVenta(Cliente cliente, Vendedor vendedor) {
        // ... (Este método estaba bien)
        int opt = JOptionPane.showConfirmDialog(parentFrame,
                "¿Deseas finalizar la venta y generar el ticket?", "Confirmar venta", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            finalizarVenta(cliente, vendedor);
        }
    }

    /** Finaliza la venta: guarda en BD, genera ticket PDF y limpia. */
    private void finalizarVenta(Cliente cliente, Vendedor vendedor) {
        // ... (Este método estaba bien, asumiendo que tus otras clases existen)
        if (detallesEnMemoria.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "No hay productos para vender.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double totalVenta = Double.parseDouble(txtTotalPagar.getText());
            int idUsuario = vendedor.getId();
            int idCliente = cliente.getIdCliente();

            ValidacionesBD dao = new ValidacionesBD();
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

            // Actualizar folio después de cada venta
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
     * [MODIFICADO]
     * Agrega un listener de doble clic que busca el producto en la BD
     * para obtener el objeto completo y calcular su precio de venta.
     */
    public void agregarListenerDobleClickTabla(JTable tablaBuscarProducto) {
        tablaBuscarProducto.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tablaBuscarProducto.getSelectedRow() != -1) {
                    int fila = tablaBuscarProducto.getSelectedRow();
                    String clave = tablaBuscarProducto.getValueAt(fila, 0).toString().trim();

                    if (clave.isEmpty()) {
                        return;
                    }

                    // --- [MODIFICACIÓN] ---
                    // No construimos un producto desde la tabla, lo buscamos en la BD
                    Producto productoEncontrado = new ValidacionesBD().buscarProductoPorClave(clave);

                    if (productoEncontrado != null) {
                        // Aplicamos la misma lógica de cálculo de precio
                        double costo = productoEncontrado.getCostoPromedio();
                        double precioDeVenta = costo * 1.50; // ¡AJUSTA TU GANANCIA!
                        productoEncontrado.setPrecioVenta(precioDeVenta);

                        // Guardamos y cargamos
                        LogicaVentas.this.productoSeleccionado = productoEncontrado;
                        cargarDatosProducto(productoEncontrado);

                        // Cerrar el diálogo
                        SwingUtilities.getWindowAncestor(tablaBuscarProducto).dispose();
                        txtCantidad.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, "Error, producto no encontrado en la BD.");
                    }
                }
            }
        });
    }

    public void cancelarVentaEnCurso() {
        // ... (Este método estaba bien)
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