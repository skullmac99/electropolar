package Datos;

import java.sql.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** DAO para ventas y sus detalles */
public class VentaDAO {

    private Connection conn;

    public VentaDAO(Connection conn) {
        this.conn = conn;
    }

    /** Inserta una venta y devuelve el ID generado. */
    public int registrarVenta(double total, int idUsuario, int idCliente) throws SQLException {
        String sql = "INSERT INTO VENTAS (TOTAL, ID_USUARIO, ID_CLIENTE) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, total);
            ps.setInt   (2, idUsuario);
            ps.setInt   (3, idCliente);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                else throw new SQLException("No se pudo obtener ID_VENTAS.");
            }
        }
    }

    /** Registra todo el detalle de la venta desde la tabla. */
    public void registrarDetalleVenta(JTable tablaVenta, int idVenta) throws SQLException {
        String sql = "INSERT INTO DETALLE_VENTAS "
                   + "(ID_VENTAS, ID_PRODUCTOS, CANTIDAD, PRECIO_UNITARIO, SUBTOTAL) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            DefaultTableModel modelo = (DefaultTableModel) tablaVenta.getModel();
            for (int i = 0; i < modelo.getRowCount(); i++) {
                ps.setInt   (1, idVenta);
                ps.setString(2, modelo.getValueAt(i, 0).toString());
                ps.setInt   (3, Integer.parseInt(modelo.getValueAt(i, 2).toString()));
                ps.setDouble(4, Double.parseDouble(modelo.getValueAt(i, 4).toString()));
                ps.setDouble(5, Double.parseDouble(modelo.getValueAt(i, 6).toString()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /** Devuelve el siguiente folio (AUTO_INCREMENT) para VENTAS. */
    public int obtenerNextFolio() throws SQLException {
        String sql = "SELECT IFNULL(MAX(ID_VENTAS), 0) + 1 AS nextFolio FROM VENTAS";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("nextFolio");
        }
        return 1;
    }

    /** Ajusta el stock de un producto en delta unidades (negativo para restar, positivo para reponer). */
    public boolean actualizarStockDelta(String idProducto, int delta) throws SQLException {
        String sql = "UPDATE PRODUCTOS SET STOCK = STOCK + ? WHERE ID_PRODUCTOS = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, delta);
            ps.setString(2, idProducto);
            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina un detalle completo y devuelve true si lo borró. */
    public boolean eliminarDetalleVenta(int idVenta, String idProducto) throws SQLException {
        String sql = "DELETE FROM DETALLE_VENTAS WHERE ID_VENTAS = ? AND ID_PRODUCTOS = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, idVenta);
            ps.setString(2, idProducto);
            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina una venta completa (detalles + cabecera). */
    public boolean eliminarVentaCompleta(int idVenta) throws SQLException {
        // Primero eliminar detalles
        try (PreparedStatement ps1 = conn.prepareStatement(
                "DELETE FROM DETALLE_VENTAS WHERE ID_VENTAS = ?")) {
            ps1.setInt(1, idVenta);
            ps1.executeUpdate();
        }
        // Luego la cabecera
        try (PreparedStatement ps2 = conn.prepareStatement(
                "DELETE FROM VENTAS WHERE ID_VENTAS = ?")) {
            ps2.setInt(1, idVenta);
            return ps2.executeUpdate() > 0;
        }
    }

    /**
     * Decrementa en 1 la cantidad de un detalle y actualiza su subtotal;
     * devuelve true si la operación tuvo efecto.
     */
    public boolean decrementarUnidadDetalle(int idVenta, String idProducto) throws SQLException {
        // 1) Leer cantidad actual y precio unitario
        int cantidad;
        double precio;
        String sqlGet = "SELECT CANTIDAD, PRECIO_UNITARIO FROM DETALLE_VENTAS "
                      + "WHERE ID_VENTAS = ? AND ID_PRODUCTOS = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlGet)) {
            ps.setInt(1, idVenta);
            ps.setString(2, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                cantidad = rs.getInt("CANTIDAD");
                precio   = rs.getDouble("PRECIO_UNITARIO");
            }
        }
        // 2) Si queda más de 1, solo actualiza; si no, lo borra
        if (cantidad > 1) {
            String sqlUpd = "UPDATE DETALLE_VENTAS SET CANTIDAD = ?, SUBTOTAL = ? "
                          + "WHERE ID_VENTAS = ? AND ID_PRODUCTOS = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpd)) {
                ps.setInt   (1, cantidad - 1);
                ps.setDouble(2, (cantidad - 1) * precio);
                ps.setInt   (3, idVenta);
                ps.setString(4, idProducto);
                return ps.executeUpdate() > 0;
            }
        } else {
            // queda cero, eliminar por completo
            return eliminarDetalleVenta(idVenta, idProducto);
        }
    }
}
