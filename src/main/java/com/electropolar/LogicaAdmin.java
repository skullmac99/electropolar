package com.electropolar;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
//librreria nueva
import java.text.DecimalFormat;

import Datos.ValidacionesBD;

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

////////////METODO MODIFICADO 
    public void procesarGuardarProducto(JTextField txtId,
            JTextField txtNombre,
            JTextField txtDescripcion,
            JTextField txtUnidad,
            JTextField txtPrecio,
            JTextField txtStock,
            JComboBox<Proveedor> comboproveedor) {

        try {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String unidad = txtUnidad.getText().trim();
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

            double costoDeCompra = Double.parseDouble(precioStr);
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

            Producto productoExistente=buscarProductoPorId(id);

            if(productoExistente==null){
                //producto nuevo
                double CostoPromedioInicial=costoDeCompra;
                Producto nuevo=new Producto(id,nombre,descripcion,unidad,CostoPromedioInicial,stockDeCompra,idproveedor);

                if (guardarProducto(nuevo)) {
                    JOptionPane.showMessageDialog(null, "producto nuevo registrado",
                    "exito",JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos(txtId,txtNombre,txtDescripcion,txtUnidad,txtPrecio,txtStock);
                    
                }


            }else{
                //producto existente calcular promedio

                double CostoPromedioActual=productoExistente.getPrecio();
                int stockActual=productoExistente.getStock();

                double valorInventarioActual=stockActual*CostoPromedioActual;
                double valorNuevaCompra=stockDeCompra*costoDeCompra;

                int stockTotal=stockActual + stockDeCompra;
                double valorTotal=valorInventarioActual+valorNuevaCompra;

                //calcular nuevo costo promedio
                double nuevoCostoPromedio=0.0;
                if (stockTotal>0) {
                    nuevoCostoPromedio=valorTotal/stockTotal;
                    
                }
                productoExistente.setNombre(nombre);
                productoExistente.setDescripcion(descripcion);
                productoExistente.setUnidad(unidad);
                productoExistente.setIdproveedor(idproveedor);

                productoExistente.setStock(stockTotal);
                productoExistente.setPrecio(nuevoCostoPromedio);

                if (bd.actualizarProducto(productoExistente)) {
                    DecimalFormat df=new DecimalFormat("#.00");
                    JOptionPane.showMessageDialog(null,
                    "stock de producto actualizdo con exito \n"+
                    "nuevo costo promedio"+df.format(nuevoCostoPromedio),
                    "exito",JOptionPane.INFORMATION_MESSAGE);

                    limpiarCampos(txtId,txtNombre,txtDescripcion,txtUnidad,txtPrecio,txtStock);
                }else{
                    JOptionPane.showMessageDialog(null, 
                    "error al actualizar producto",
                    "error",JOptionPane.ERROR_MESSAGE);

                }
        }
    }catch(NumberFormatException ex){
        JOptionPane.showMessageDialog(null,
        "el campo 'precio' debe de ser numerico  y 'stock' debe de ser un numero entero",
        "error",JOptionPane.ERROR_MESSAGE);

    }catch(Exception e ){
        JOptionPane.showMessageDialog(null,
        "error inesperado: "+e.getMessage(),
        "error",JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();

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

}
