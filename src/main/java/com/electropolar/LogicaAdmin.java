package com.electropolar;

import java.awt.Container;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import Datos.ConexionBD;
import Datos.ValidacionesBD;
import presentacion.AdministarUsuario;
import presentacion.AdministrarCliente;
import presentacion.ventaAdmin;

public class LogicaAdmin {

    private final ValidacionesBD bd = new ValidacionesBD();

    public boolean existeProducto(String idProducto) {
        return bd.existeProducto(idProducto);
    }

    public boolean guardarProducto(Producto producto) {
        if (bd.existeProducto(producto.getId())) {
            JOptionPane.showMessageDialog(null,
                    "Ya existe un producto con la clave " + producto.getId(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean exito = bd.insertarProducto(producto);
        if (!exito) {
            JOptionPane.showMessageDialog(null,
                    "Ocurrió un error al registrar el producto.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return exito;
    }

    public void limpiarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
        }
    }

    public void procesarGuardarProducto(JTextField txtId,
            JTextField txtNombre,
            JTextField txtDescripcion,
            JComboBox comboBox,
            JTextField txtPrecio,
            JTextField txtStock,
            JComboBox<Proveedor> comboproveedor) {

        try {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
             String unidad = comboBox.getSelectedItem() != null ? comboBox.getSelectedItem().toString().trim() : "";
            String precioStr = txtPrecio.getText().trim();
            String stockStr = txtStock.getText().trim();

            // Validar campos vacíos
            if (id.isEmpty() || nombre.isEmpty() || descripcion.isEmpty() ||
                    unidad.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Por favor completa todos los campos.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar longitud del ID (máx 8 caracteres)
            if (id.length() > 8) {
                JOptionPane.showMessageDialog(null,
                        "El ID del producto no puede tener más de 8 caracteres.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar formato del precio
            if (!precioStr.matches("\\d+(\\.\\d{1,2})?")) {
                JOptionPane.showMessageDialog(null,
                        "El campo 'Precio' solo debe contener números y un punto decimal válido (ejemplo: 123.45).",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar formato del stock
            if (!stockStr.matches("\\d+")) {
                JOptionPane.showMessageDialog(null,
                        "El campo 'Stock' solo debe contener números enteros positivos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double costoDeCompra= Double.parseDouble(precioStr);
            int stockDeCompra = Integer.parseInt(stockStr);
            // Validar proveedor seleccionado
            Proveedor proveedorSeleccionado = (Proveedor) comboproveedor.getSelectedItem();

            if (proveedorSeleccionado == null) {
                JOptionPane.showMessageDialog(null,
                        "Debe seleccionar un proveedor válido.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idproveedor = proveedorSeleccionado.getIdProveedor();

            // Crear producto y guardar
            //Producto nuevo = new Producto(id, nombre, descripcion, unidad, precio, stock, idproveedor);
            Producto productoExistente=bd.buscarProductoPorClave(id);

  if (productoExistente == null) {
                // --- [CASO 1: Producto NUEVO] ---
                double costoPromedioInicial = costoDeCompra;
                
                // Usamos el constructor de 7 argumentos
                Producto nuevo = new Producto(id, nombre, descripcion, unidad,
                        costoPromedioInicial, stockDeCompra, idproveedor);

                // Llamamos a tu método guardarProducto (que ya valida si existe)
                if (guardarProducto(nuevo)) { 
                    JOptionPane.showMessageDialog(null, "Producto NUEVO registrado con éxito.");
                    limpiarCampos(txtId, txtNombre, txtDescripcion, txtPrecio, txtStock);
                    // Deberías limpiar el JComboBox también
                    // comboBox.setSelectedIndex(0); 
                }

            } else {
                // --- [CASO 2: Producto EXISTENTE (Calcular Promedio)] ---

                // [CORRECCIÓN]: Usamos getCostoPromedio()
                double costoPromedioActual = productoExistente.getCostoPromedio();
                int stockActual = productoExistente.getStock();

                double valorInventarioActual = stockActual * costoPromedioActual;
                double valorNuevaCompra = stockDeCompra * costoDeCompra;

                int stockTotal = stockActual + stockDeCompra;
                double valorTotal = valorInventarioActual + valorNuevaCompra;

                double nuevoCostoPromedio = (stockTotal > 0) ? (valorTotal / stockTotal) : 0.0;

                // Actualizamos el objeto existente
                productoExistente.setNombre(nombre);
                productoExistente.setDescripcion(descripcion);
                productoExistente.setUnidad(unidad); // Actualizamos la unidad
                productoExistente.setIdproveedor(idproveedor);
                productoExistente.setStock(stockTotal);
                
                // [CORRECCIÓN]: Usamos setCostoPromedio()
                productoExistente.setCostoPromedio(nuevoCostoPromedio); 

                // Llama al método de ACTUALIZAR
                if (bd.actualizarProducto(productoExistente)) {
                    DecimalFormat df = new DecimalFormat("#.00");
                    JOptionPane.showMessageDialog(null,
                            "Stock de producto actualizado con éxito.\n" +
                            "Nuevo costo promedio: " + df.format(nuevoCostoPromedio),
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos(txtId, txtNombre, txtDescripcion, txtPrecio, txtStock);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Error al ACTUALIZAR el producto.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "El campo 'Precio' debe ser numérico y 'Stock' debe ser un número entero.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error inesperado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void iniciarEdicionProducto(JTable tabla) {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null,
                    "Selecciona un Producto para modificar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ProductosTableModel modelo = (ProductosTableModel) tabla.getModel();
        modelo.setEditableRow(row);
        // Abrir edición en la primera columna editable (columna 1)
        tabla.editCellAt(row, 0);
        tabla.requestFocus();
    }

    public void eliminarProductoSeleccionado(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un producto para eliminar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String clave = tabla.getValueAt(fila, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Estás seguro de que deseas eliminar el producto con clave: " + clave + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM PRODUCTOS WHERE ID_PRODUCTOS = ?")) {

            ps.setString(1, clave);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ((DefaultTableModel) tabla.getModel()).removeRow(fila);
                JOptionPane.showMessageDialog(null, "Producto eliminado correctamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el producto o no se pudo eliminar.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al eliminar el producto: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void confirmarODescartarEdicionProd(JTable tabla) {
        ProductosTableModel modelo = (ProductosTableModel) tabla.getModel();
        int row = modelo.getEditableRow();
        if (row < 0)
            return; // no estamos en modo edición

        // Si hay un editor activo, ciérralo
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }

        int opt = JOptionPane.showConfirmDialog(null,
                "¿Guardar cambios?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (opt == JOptionPane.YES_OPTION) {
            try {
                Producto p = new Producto();
                p.setId(modelo.getValueAt(row, 0).toString());
                p.setNombre(modelo.getValueAt(row, 1).toString());
                p.setDescripcion(modelo.getValueAt(row, 2).toString());
                p.setUnidad(modelo.getValueAt(row, 3).toString());

                // Validación y conversión segura del precio
                String precioStr = modelo.getValueAt(row, 4).toString()
                        .replace("$", "")
                        .replace(",", "")
                        .trim();
                p.setCostoPromedio(Double.parseDouble(precioStr));

                // Validación de stock
                p.setStock(Integer.parseInt(modelo.getValueAt(row, 5).toString()));

                // ----- Manejo de la columna de Proveedor -----
                Object proveedorObj = modelo.getValueAt(row, 6);
                if (proveedorObj instanceof Proveedor) {
                    Proveedor proveedor = (Proveedor) proveedorObj;
                    p.setIdproveedor(proveedor.getIdProveedor());
                } else {
                    try {
                        // Si viene como "ID - Nombre"
                        String[] partes = proveedorObj.toString().split(" ");
                        p.setIdproveedor(Integer.parseInt(partes[0]));
                    } catch (Exception ex) {
                        p.setIdproveedor(0);
                    }
                }

                // ----- Guardar cambios en BD -----
                if (bd.actualizarProducto(p)) {
                    JOptionPane.showMessageDialog(null,
                            "Producto actualizado correctamente.",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Error al guardar los cambios.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Ocurrió un error al procesar la actualización: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Salimos del modo edición y recargamos la tabla
        modelo.clearEditableRow();
        SwingUtilities.invokeLater(() -> {
            Container anc = SwingUtilities.getAncestorOfClass(ventaAdmin.class, tabla);
            if (anc instanceof ventaAdmin) {
                ((ventaAdmin) anc).cargarProductos();
            }
        });
    }

    public void procesarGuardarUsuario(JTextField txtNombre,
            JTextField txtApellidoPat,
            JTextField txtApellidoMat,
            JComboBox comboBox) {
        try {
            String nombre = txtNombre.getText().trim();
            String apPat = txtApellidoPat.getText().trim();
            String apMat = txtApellidoMat.getText().trim();
            String rol = comboBox.getSelectedItem() != null ? comboBox.getSelectedItem().toString().trim() : "";

            // Validar campos vacíos
            if (nombre.isEmpty() || apPat.isEmpty() || apMat.isEmpty() || rol.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Por favor completa todos los campos del usuario.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar solo letras en nombre y apellidos
            if (!nombre.matches("[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s]+") ||
                    !apPat.matches("[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s]+") ||
                    !apMat.matches("[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s]+")) {
                JOptionPane.showMessageDialog(null,
                        "El nombre y los apellidos solo deben contener letras.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear objeto usuario
            Usuarios nuevo = new Usuarios(0, nombre, apPat, apMat, rol);

            // Intentar guardar en la base de datos
            if (bd.insertarUsuario(nuevo)) {
                JOptionPane.showMessageDialog(null,
                        "Usuario registrado con éxito.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos(txtNombre, txtApellidoPat, txtApellidoMat);
            } else {
                JOptionPane.showMessageDialog(null,
                        "No se pudo registrar el usuario.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Ocurrió un error al guardar el usuario: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void procesarGuardarCliente(
            JTextField txtNombre, JTextField txtRfc, JTextField txtCorreo, JTextField txtTelefono,
            JTextField txtCalle, JTextField txtColonia, JTextField txtNoExt, JTextField txtNoInt,
            JTextField txtCp, JTextField txtMunicipio, JTextField txtEstado, JTextField txtPais,
            JTextField txtCfdi) {

        try {
            String nombre = txtNombre.getText().trim();
            String rfc = txtRfc.getText().trim();
            String correo = txtCorreo.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String calle = txtCalle.getText().trim();
            String colonia = txtColonia.getText().trim();
            String noExtStr = txtNoExt.getText().trim();
            String noIntStr = txtNoInt.getText().trim();
            String cpStr = txtCp.getText().trim();
            String municipio = txtMunicipio.getText().trim();
            String estado = txtEstado.getText().trim();
            String pais = txtPais.getText().trim();
            String cfdi = txtCfdi.getText().trim();

            // Debug
            System.out.println("noExtStr = '" + noExtStr + "'");
            System.out.println("noIntStr = '" + noIntStr + "'");
            System.out.println("cpStr    = '" + cpStr + "'");

            if (nombre.isEmpty() || rfc.isEmpty() || correo.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Por favor completa todos los campos obligatorios.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validación numérica
            if (!noExtStr.matches("\\d+") || !noIntStr.matches("\\d+") || !cpStr.matches("\\d+")) {
                JOptionPane.showMessageDialog(null,
                        "Los campos No. Ext, No. Int y Código Postal deben contener solo números enteros.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int noExt = Integer.parseInt(noExtStr);
            int noInt = Integer.parseInt(noIntStr);
            int cp = Integer.parseInt(cpStr);

            Cliente nuevo = new Cliente(nombre, rfc, correo, telefono, calle, colonia,
                    noExt, noInt, cp, municipio, estado, pais, cfdi);

            boolean exito = new ValidacionesBD().insertarCliente(nuevo);

            if (exito) {
                JOptionPane.showMessageDialog(null, "Cliente registrado con éxito.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos(txtNombre, txtRfc, txtCorreo, txtTelefono, txtCalle,
                        txtColonia, txtNoExt, txtNoInt, txtCp, txtMunicipio,
                        txtEstado, txtPais, txtCfdi);
            } else {
                JOptionPane.showMessageDialog(null, "Error al registrar el cliente.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "Número exterior, interior o código postal inválidos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void iniciarEdicionCliente(JTable tabla) {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null,
                    "Selecciona un cliente para modificar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ClienteTableModel modelo = (ClienteTableModel) tabla.getModel();
        modelo.setEditableRow(row);
        // Abrir edición en la primera columna editable (columna 1)
        tabla.editCellAt(row, 1);
        tabla.requestFocus();
    }

    /**
     * Detiene la edición, pide confirmación y guarda o descarta los cambios.
     * Debes haber instalado esta acción sobre la tecla ENTER en tu JTable.
     */
    public void confirmarODescartarEdicion(JTable tabla) {
        ClienteTableModel modelo = (ClienteTableModel) tabla.getModel();
        int row = modelo.getEditableRow();
        if (row < 0)
            return; // no estamos en modo edición

        // si hay un editor abierto, ciérralo
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }

        int opt = JOptionPane.showConfirmDialog(null,
                "¿Guardar cambios?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            // reconstruir objeto Cliente desde la fila
            Cliente c = new Cliente();
            c.setIdCliente(Integer.parseInt(modelo.getValueAt(row, 0).toString()));
            c.setNombreCliente(modelo.getValueAt(row, 1).toString());
            c.setRfc(modelo.getValueAt(row, 2).toString());
            c.setCorreo(modelo.getValueAt(row, 3).toString());
            c.setTelefono(modelo.getValueAt(row, 4).toString());
            c.setCalle(modelo.getValueAt(row, 5).toString());
            c.setColonia(modelo.getValueAt(row, 6).toString());
            c.setNoExt(Integer.parseInt(modelo.getValueAt(row, 7).toString()));
            c.setNoInt(Integer.parseInt(modelo.getValueAt(row, 8).toString()));
            c.setCp(Integer.parseInt(modelo.getValueAt(row, 9).toString()));
            c.setMunicipio(modelo.getValueAt(row, 10).toString());
            c.setEstado(modelo.getValueAt(row, 11).toString());
            c.setPais(modelo.getValueAt(row, 12).toString());
            c.setCfdi(modelo.getValueAt(row, 13).toString());

            if (bd.actualizarCliente(c)) {
                JOptionPane.showMessageDialog(null,
                        "Cliente actualizado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Error al guardar los cambios.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // salimos del modo edición (tanto si guardamos como si no)
        modelo.clearEditableRow();
        // recarga la tabla para descartar edición en caso de NO
        // Busca la instancia de AdministrarCliente en la jerarquía de contenedores
        SwingUtilities.invokeLater(() -> {
            Container anc = SwingUtilities.getAncestorOfClass(AdministrarCliente.class, tabla);
            if (anc instanceof AdministrarCliente) {
                ((AdministrarCliente) anc).cargarClientes();
            }
        });

    }

    public void eliminarClienteDesdeTabla(JTable tablaClientes) {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un cliente para eliminar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ahora usamos el ID real desde la columna 0
        int idCliente = Integer.parseInt(tablaClientes.getValueAt(fila, 0).toString());
        String nombre = tablaClientes.getValueAt(fila, 1).toString(); // solo para mostrar

        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Estás seguro de eliminar al cliente \"" + nombre + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ValidacionesBD dao = new ValidacionesBD();
            if (dao.eliminarClientePorId(idCliente)) {
                JOptionPane.showMessageDialog(null, "Cliente eliminado con éxito.");
                ((DefaultTableModel) tablaClientes.getModel()).removeRow(fila);
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar el cliente.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void iniciarEdicionUsurio(JTable tabla) {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null,
                    "Selecciona un Usuario para modificar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        UsuarioTableModel modelo = (UsuarioTableModel) tabla.getModel();
        modelo.setEditableRow(row);
        // Abrir edición en la primera columna editable (columna 1)
        tabla.editCellAt(row, 1);
        tabla.requestFocus();
    }

    public void confirmarODescartarEdicionUs(JTable tabla) {
        UsuarioTableModel modelo = (UsuarioTableModel) tabla.getModel();
        int row = modelo.getEditableRow();
        if (row < 0)
            return; // no estamos en modo edición

        // si hay un editor abierto, ciérralo
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }

        int opt = JOptionPane.showConfirmDialog(null,
                "¿Guardar cambios?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            // reconstruir objeto Cliente desde la fila
            Usuarios u = new Usuarios();
            u.setIdUsuario(Integer.parseInt(modelo.getValueAt(row, 0).toString()));
            u.setNombre(modelo.getValueAt(row, 1).toString());
            u.setApellidoPat(modelo.getValueAt(row, 2).toString());
            u.setApellidoMat(modelo.getValueAt(row, 3).toString());
            u.setRolUsuario(modelo.getValueAt(row, 4).toString());

            if (bd.actualizarUsuario(u)) {
                JOptionPane.showMessageDialog(null,
                        "Usuario actualizado correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Error al guardar los cambios.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // salimos del modo edición (tanto si guardamos como si no)
        modelo.clearEditableRow();
        // recarga la tabla para descartar edición en caso de NO
        // Busca la instancia de AdministrarCliente en la jerarquía de contenedores
        SwingUtilities.invokeLater(() -> {
            Container anc = SwingUtilities.getAncestorOfClass(AdministarUsuario.class, tabla);
            if (anc instanceof AdministarUsuario) {
                ((AdministarUsuario) anc).cargarUsuarios();
            }
        });

    }

    // ELIMINAR USUARIOS
    public void eliminarUsuarioDesdeTabla(JTable tablaUsuarios) {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un usuario para eliminar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ahora usamos el ID real desde la columna 0
        int idUsuario = Integer.parseInt(tablaUsuarios.getValueAt(fila, 0).toString());
        String nombre = tablaUsuarios.getValueAt(fila, 1).toString(); // solo para mostrar

        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Estás seguro de eliminar al usuario \"" + nombre + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ValidacionesBD dao = new ValidacionesBD();
            if (dao.eliminarUsuarioPorId(idUsuario)) {
                JOptionPane.showMessageDialog(null, "Usuario eliminado con éxito.");
                ((DefaultTableModel) tablaUsuarios.getModel()).removeRow(fila);
            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar el usuario.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static class ProductosTableModel extends DefaultTableModel {
        private int editableRow = -1;

        public ProductosTableModel(Object[] columns, int rows) {
            super(columns, rows);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return row == editableRow && col > 0;
        }

        public void setEditableRow(int row) {
            this.editableRow = row;
        }

        public int getEditableRow() {
            return editableRow;
        }

        public void clearEditableRow() {
            this.editableRow = -1;
        }
    }

    public static class ClienteTableModel extends DefaultTableModel {
        private int editableRow = -1;

        public ClienteTableModel(Object[] columns, int rows) {
            super(columns, rows);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return row == editableRow && col > 0;
        }

        public void setEditableRow(int row) {
            this.editableRow = row;
        }

        public int getEditableRow() {
            return editableRow;
        }

        public void clearEditableRow() {
            this.editableRow = -1;
        }
    }

    public static class UsuarioTableModel extends DefaultTableModel {
        private int editableRow = -1;

        public UsuarioTableModel(Object[] columns, int rows) {
            super(columns, rows);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return row == editableRow && col > 0;
        }

        public void setEditableRow(int row) {
            this.editableRow = row;
        }

        public int getEditableRow() {
            return editableRow;
        }

        public void clearEditableRow() {
            this.editableRow = -1;
        }
    }

}
