package Datos;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import com.electropolar.Cliente;
import com.electropolar.DetalleVenta;
import com.electropolar.Producto;
import com.electropolar.Vendedor;
import com.electropolar.Venta;
import com.electropolar.Proveedor;
import com.electropolar.Usuarios;

public class ValidacionesBD {

    /** Valida usuario y contraseña contra la tabla ROLES con hash SHA-256. */
    public boolean verificarCredenciales(String nombre, String contrasenaPlana) {
        String sql = "SELECT u.* FROM USUARIOS u JOIN ROLES r ON u.ROL = r.ROL "
                + "WHERE u.NOMBRE = ? AND r.CONTRASENA_HASH = SHA2(?, 256)";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, contrasenaPlana);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String obtenerRol(String nombre, String contrasenaPlana) {
        String sql = "SELECT r.ROL " +
                "FROM USUARIOS u " +
                "JOIN ROLES r ON u.ROL = r.ROL " +
                "WHERE u.NOMBRE = ? AND r.CONTRASENA_HASH = SHA2(?, 256)";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, contrasenaPlana);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ROL");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Carga todos los vendedores (rol = VENDEDOR). */
    public List<Vendedor> cargarVendedores() {
        List<Vendedor> lista = new ArrayList<>();
        String sql = "SELECT ID_USUARIO, NOMBRE FROM USUARIOS WHERE ROL = 'VENDEDOR'";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Vendedor(rs.getInt("ID_USUARIO"), rs.getString("NOMBRE")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar vendedores: " + e.getMessage());
        }
        return lista;
    }

    /** Carga todos los clientes. */
    public List<Cliente> cargarClientes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT ID_CLIENTE, NOMBRE FROM CLIENTES";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(rs.getInt("ID_CLIENTE"), rs.getString("NOMBRE")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar clientes: " + e.getMessage());
        }
        return lista;
    }

    /** Carga todos los PROVEEDORES. */
    public List<Proveedor> cargarProveedores() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT ID_PROVEEDOR, NOMBRE FROM PROVEEDORES";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Proveedor(rs.getInt("ID_PROVEEDOR"), rs.getString("NOMBRE")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar proveedor: " + e.getMessage());
        }
        return lista;
    }

    public List<Proveedor> cargarEstatusProveedor() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT ESTATUS FROM PROVEEDORES";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Proveedor(rs.getString("ESTATUS")));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar proveedor: " + e.getMessage());
        }
        return lista;
    }

    /** Búsqueda dinámica para el panel BuscarProducto. */
    public List<Object[]> buscarProductos(String texto) {
        List<Object[]> productos = new ArrayList<>();
        String sql = "SELECT ID_PRODUCTOS, NOMBRE, DESCRIPCION, UNIDAD, PRECIO, STOCK FROM PRODUCTOS "
                + "WHERE NOMBRE LIKE ? OR DESCRIPCION LIKE ?";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String filtro = "%" + texto + "%";
            stmt.setString(1, filtro);
            stmt.setString(2, filtro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("ID_PRODUCTOS");
                    String nombre = rs.getString("NOMBRE");
                    String desc = rs.getString("DESCRIPCION");
                    String unidad = rs.getString("UNIDAD");
                    int stock = rs.getInt("STOCK");
                    double precio = rs.getDouble("PRECIO");
                    double precioMayo = Math.round(precio * 0.8 * 100.0) / 100.0;
                    productos.add(new Object[] {
                            id,
                            nombre,
                            desc,
                            unidad,
                            stock,
                            String.format("$%.2f", precio),
                            String.format("$%.2f (desde 10 unidades)", precioMayo)
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productos;
    }

    /** Busca un producto por su clave (ID_PRODUCTOS). */
    public Producto buscarProductoPorClave(String clave) {
        String sql = "SELECT ID_PRODUCTOS,NOMBRE,DESCRIPCION, UNIDAD," +
                "PRECIO,STOCK,ID_PROVEEDOR FROM PRODUCTOS WHERE ID_PRODUCTOS = ?";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clave);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                            rs.getString("ID_PRODUCTOS"),
                            rs.getString("NOMBRE"),
                            rs.getString("DESCRIPCION"),
                            rs.getString("UNIDAD"),
                            rs.getDouble("PRECIO"),
                            rs.getInt("STOCK"),
                            rs.getInt("ID_PROVEEDOR"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Igual que buscarProductoPorClave, para claridad en ticket. */
    public Producto buscarProductoPorId(String idProducto) {
        return buscarProductoPorClave(idProducto);
    }

    public int finalizarVenta(double totalVenta, int idUsuario, int idCliente, List<DetalleVenta> detallesVendidos) {
        int idVentaGenerada = -1;
        String sqlVenta = "INSERT INTO VENTAS (TOTAL, ID_USUARIO, ID_CLIENTE) VALUES (?, ?, ?)";
        String sqlDetalle = "INSERT INTO DETALLE_VENTAS (ID_VENTAS, ID_PRODUCTOS, CANTIDAD, PRECIO_UNITARIO, SUBTOTAL) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlActualizarStock = "UPDATE PRODUCTOS SET STOCK = ? WHERE ID_PRODUCTOS = ?";

        Connection conn = null;
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            // 1) Insertar cabecera de venta
            try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psVenta.setDouble(1, totalVenta);
                psVenta.setInt(2, idUsuario);
                psVenta.setInt(3, idCliente);
                psVenta.executeUpdate();

                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) {
                        idVentaGenerada = rs.getInt(1);
                    } else {
                        throw new SQLException("No se obtuvo el ID de la venta.");
                    }
                }
            }

            // 2) Insertar cada detalle de venta
            try (PreparedStatement psDet = conn.prepareStatement(sqlDetalle)) {
                for (DetalleVenta det : detallesVendidos) {
                    psDet.setInt(1, idVentaGenerada);
                    psDet.setString(2, det.getIdProducto());
                    psDet.setInt(3, det.getCantidad());
                    psDet.setDouble(4, det.getPrecioUnitario());
                    psDet.setDouble(5, det.getSubtotal());
                    psDet.addBatch();
                }
                psDet.executeBatch();
            }

            // 3) Actualizar stock por cada producto vendido
            try (PreparedStatement psStock = conn.prepareStatement(sqlActualizarStock)) {
                for (DetalleVenta det : detallesVendidos) {
                    Producto prod = buscarProductoPorId(det.getIdProducto());
                    if (prod != null) {
                        int nuevoStock = prod.getStock() - det.getCantidad();
                        if (nuevoStock < 0)
                            nuevoStock = 0; // evita valores negativos

                        psStock.setInt(1, nuevoStock);
                        psStock.setString(2, det.getIdProducto());
                        psStock.addBatch();
                    }
                }
                psStock.executeBatch();
            }

            // 4) Confirmar todo
            conn.commit();

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            idVentaGenerada = -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return idVentaGenerada;
    }

    public int obtenerNextFolio() {
        String sql = "SELECT IFNULL(MAX(ID_VENTAS), 0) + 1 AS nextFolio FROM VENTAS";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("nextFolio");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Valor por defecto si algo falla
    }

    /** Devuelve los detalles de una venta. */
    public List<DetalleVenta> obtenerDetallesVenta(int idVenta) {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT ID_PRODUCTOS, CANTIDAD, PRECIO_UNITARIO FROM DETALLE_VENTAS WHERE ID_VENTAS = ?";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DetalleVenta(
                            0,
                            idVenta,
                            rs.getString("ID_PRODUCTOS"),
                            rs.getInt("CANTIDAD"),
                            rs.getDouble("PRECIO_UNITARIO")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    /** Cancela una venta completa: repone stock y borra cabecera + detalles. */
    public boolean cancelarVentaCompleta(int idVenta) {
        Connection conn = null;
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            // 1) Reponer stock
            List<DetalleVenta> detalles = obtenerDetallesVenta(idVenta);
            String sqlStock = "UPDATE PRODUCTOS SET STOCK = STOCK + ? WHERE ID_PRODUCTOS = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlStock)) {
                for (DetalleVenta d : detalles) {
                    ps.setInt(1, d.getCantidad());
                    ps.setString(2, d.getIdProducto());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 2) Borrar detalles
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM DETALLE_VENTAS WHERE ID_VENTAS = ?")) {
                ps.setInt(1, idVenta);
                ps.executeUpdate();
            }
            // 3) Borrar cabecera
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM VENTAS WHERE ID_VENTAS = ?")) {
                ps.setInt(1, idVenta);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            return false;
        } finally {
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
    }

    /** Elimina por completo un producto de la venta y repone todo su stock. */
    public boolean cancelarUnProducto(int idVenta, String idProducto) {
        Connection conn = null;
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            // Obtener cantidad vendida
            String sqlGet = "SELECT CANTIDAD FROM DETALLE_VENTAS WHERE ID_VENTAS = ? AND ID_PRODUCTOS = ?";
            int cant;
            try (PreparedStatement ps = conn.prepareStatement(sqlGet)) {
                ps.setInt(1, idVenta);
                ps.setString(2, idProducto);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next())
                        throw new SQLException("Detalle no encontrado");
                    cant = rs.getInt("CANTIDAD");
                }
            }

            // Reponer stock
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE PRODUCTOS SET STOCK = STOCK + ? WHERE ID_PRODUCTOS = ?")) {
                ps.setInt(1, cant);
                ps.setString(2, idProducto);
                ps.executeUpdate();
            }

            // Borrar el detalle
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM DETALLE_VENTAS WHERE ID_VENTAS = ? AND ID_PRODUCTOS = ?")) {
                ps.setInt(1, idVenta);
                ps.setString(2, idProducto);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            return false;
        } finally {
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
    }

    /**
     * Resta una unidad del detalle y repone 1 en stock; si llega a 0, elimina el
     * detalle.
     */
    public boolean decrementarUnaUnidad(int idVenta, String idProducto) {
        Connection conn = null;
        try {
            conn = ConexionBD.conectar();
            conn.setAutoCommit(false);

            // 1) Leer cantidad actual
            int cant;
            String sqlGet = "SELECT CANTIDAD, PRECIO_UNITARIO FROM DETALLE_VENTAS WHERE ID_VENTAS = ? AND ID_PRODUCTOS = ?";
            double precioUnit;
            try (PreparedStatement ps = conn.prepareStatement(sqlGet)) {
                ps.setInt(1, idVenta);
                ps.setString(2, idProducto);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next())
                        throw new SQLException("Detalle no encontrado");
                    cant = rs.getInt("CANTIDAD");
                    precioUnit = rs.getDouble("PRECIO_UNITARIO");
                }
            }

            // 2) Reponer 1 unidad en stock
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE PRODUCTOS SET STOCK = STOCK + 1 WHERE ID_PRODUCTOS = ?")) {
                ps.setString(1, idProducto);
                ps.executeUpdate();
            }

            // 3) Actualizar o eliminar detalle
            if (cant > 1) {
                int nuevaCant = cant - 1;
                double nuevoSub = nuevaCant * precioUnit;
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE DETALLE_VENTAS SET CANTIDAD = ?, SUBTOTAL = ? WHERE ID_VENTAS = ? AND ID_PRODUCTOS = ?")) {
                    ps.setInt(1, nuevaCant);
                    ps.setDouble(2, nuevoSub);
                    ps.setInt(3, idVenta);
                    ps.setString(4, idProducto);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM DETALLE_VENTAS WHERE ID_VENTAS = ? AND ID_PRODUCTOS = ?")) {
                    ps.setInt(1, idVenta);
                    ps.setString(2, idProducto);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            return false;
        } finally {
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
    }

    /**
     * Recupera todos los productos de la base de datos.
     */
    public List<Producto> obtenerTodosProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT ID_PRODUCTOS, NOMBRE, DESCRIPCION, UNIDAD, PRECIO, STOCK, ID_PROVEEDOR FROM PRODUCTOS";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getString("ID_PRODUCTOS"),
                        rs.getString("NOMBRE"),
                        rs.getString("DESCRIPCION"),
                        rs.getString("UNIDAD"),
                        rs.getDouble("PRECIO"),
                        rs.getInt("STOCK"),
                        rs.getInt("ID_PROVEEDOR"));
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al cargar productos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return lista;
    }

    public String obtenerNombreProveedorPorId(int idProveedor) {
        String nombre = "Desconocido";
        String sql = "SELECT NOMBRE FROM PROVEEDORES WHERE ID_PROVEEDOR = ?";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProveedor);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nombre = rs.getString("NOMBRE");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nombre;
    }

    /** Comprueba si existe un producto con la clave dada */
    public boolean existeProducto(String idProducto) {
        String sql = "SELECT 1 FROM PRODUCTOS WHERE ID_PRODUCTOS = ?";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idProducto);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Inserta un producto si no existe aún */
    public boolean insertarProducto(Producto producto) {
        if (existeProducto(producto.getId())) {
            return false; // ya existe, no insertamos
        }

        String sql = "INSERT INTO PRODUCTOS "
                + "(ID_PRODUCTOS, NOMBRE, DESCRIPCION, UNIDAD, PRECIO, STOCK, ID_PROVEEDOR) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getId());
            stmt.setString(2, producto.getNombre());
            stmt.setString(3, producto.getDescripcion());
            stmt.setString(4, producto.getUnidad());
            stmt.setDouble(5, producto.getCostoPromedio());
            stmt.setInt(6, producto.getStock());
            stmt.setInt(7, producto.getIdproveedor());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Inserta un usuario si no existe aún */
    public boolean insertarUsuario(Usuarios usuarios) {

        String sql = "INSERT INTO USUARIOS "
                + "(NOMBRE, APELLIDOPAT, APELLIDOMAT, ROL) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuarios.getNombre());
            stmt.setString(2, usuarios.getApellidoPat());
            stmt.setString(3, usuarios.getApellidoMat());
            stmt.setString(4, usuarios.getRolUsuario());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarCliente(Cliente cliente) {
        String sql = "INSERT INTO CLIENTES (NOMBRE, RFC, CORREO, TELEFONO, CALLE, "
                + "COLONIA, NUMERO_EXTE, NUMERO_INTE, CODIGO_POST, MUNICIPIO, ESTADO, PAIS, CFDI) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNombreCliente());
            stmt.setString(2, cliente.getRfc());
            stmt.setString(3, cliente.getCorreo());
            stmt.setString(4, cliente.getTelefono());
            stmt.setString(5, cliente.getCalle());
            stmt.setString(6, cliente.getColonia());
            stmt.setInt(7, cliente.getNoExt());
            stmt.setInt(8, cliente.getNoInt());
            stmt.setInt(9, cliente.getCp());
            stmt.setString(10, cliente.getMunicipio());
            stmt.setString(11, cliente.getEstado());
            stmt.setString(12, cliente.getPais());
            stmt.setString(13, cliente.getCfdi());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarProveedor(Proveedor proveedor) {
        String sql = "INSERT INTO PROVEEDORES (NOMBRE, RFC, CORREO, TELEFONO, ESTADO, CIUDAD, CALLE, COLONIA, "
                + "NUMERO_EXTE, NUMERO_INT, CODIGO_POST, MUNICIPIO, PAIS, ESTATUS) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proveedor.getNombre());
            stmt.setString(2, proveedor.getRfc());
            stmt.setString(3, proveedor.getCorreo());
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getEstado());
            stmt.setString(6, proveedor.getCiudad());
            stmt.setString(7, proveedor.getCalle());
            stmt.setString(8, proveedor.getColonia());
            stmt.setInt(9, proveedor.getNumeroExte());
            stmt.setInt(10, proveedor.getNumeroInt());
            stmt.setInt(11, proveedor.getCodigoPost());
            stmt.setString(12, proveedor.getMunicipio());
            stmt.setString(13, proveedor.getPais());
            stmt.setString(14, proveedor.getEstatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera todos los productos de la base de datos.
     */
    // En Datos/ValidacionesBD.java
    public List<Usuarios> obtenerTodosUsuarios() {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT ID_USUARIO, NOMBRE, APELLIDOPAT, APELLIDOMAT, ROL FROM USUARIOS";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuarios u = new Usuarios(
                        rs.getInt("ID_USUARIO"),
                        rs.getString("NOMBRE"),
                        rs.getString("APELLIDOPAT"),
                        rs.getString("APELLIDOMAT"),
                        rs.getString("ROL"));
                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Cliente> obtenerTodosClientes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT ID_CLIENTE, NOMBRE, RFC, CORREO, TELEFONO, CALLE, COLONIA, " +
                "NUMERO_EXTE, NUMERO_INTE, CODIGO_POST, MUNICIPIO, ESTADO, PAIS, CFDI FROM CLIENTES";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getInt("ID_CLIENTE"),
                        rs.getString("NOMBRE"),
                        rs.getString("RFC"),
                        rs.getString("CORREO"),
                        rs.getString("TELEFONO"),
                        rs.getString("CALLE"),
                        rs.getString("COLONIA"),
                        rs.getInt("NUMERO_EXTE"),
                        rs.getInt("NUMERO_INTE"),
                        rs.getInt("CODIGO_POST"),
                        rs.getString("MUNICIPIO"),
                        rs.getString("ESTADO"),
                        rs.getString("PAIS"),
                        rs.getString("CFDI"));
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Proveedor> obtenerTodosProveedores() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT ID_PROVEEDOR, NOMBRE, RFC, CORREO, TELEFONO, ESTADO, CIUDAD, CALLE, COLONIA, "
                + "NUMERO_EXTE, NUMERO_INT, CODIGO_POST, MUNICIPIO, PAIS, ESTATUS "
                + "FROM PROVEEDORES";

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Proveedor p = new Proveedor(
                        rs.getInt("ID_PROVEEDOR"),
                        rs.getString("NOMBRE"),
                        rs.getString("RFC"),
                        rs.getString("CORREO"),
                        rs.getString("TELEFONO"),
                        rs.getString("ESTADO"),
                        rs.getString("CIUDAD"),
                        rs.getString("CALLE"),
                        rs.getString("COLONIA"),
                        rs.getInt("NUMERO_EXTE"),
                        rs.getInt("NUMERO_INT"),
                        rs.getInt("CODIGO_POST"),
                        rs.getString("MUNICIPIO"),
                        rs.getString("PAIS"),
                        rs.getString("ESTATUS"));
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean actualizarProducto(Producto p) {
        String sql = """
                UPDATE PRODUCTOS SET
                  NOMBRE       = ?,
                  DESCRIPCION  = ?,
                  UNIDAD       = ?,
                  PRECIO       = ?,
                  STOCK        = ?,
                  ID_PROVEEDOR = ?
                WHERE ID_PRODUCTOS = ?
                """;

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setString(3, p.getUnidad());
            ps.setDouble(4, p.getCostoPromedio());
            ps.setInt(5, p.getStock());
            ps.setInt(6, p.getIdproveedor());
            ps.setString(7, p.getId());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarCliente(Cliente c) {
        String sql = """
                UPDATE CLIENTES SET
                  NOMBRE    = ?,
                  RFC       = ?,
                  CORREO    = ?,
                  TELEFONO  = ?,
                  CALLE     = ?,
                  COLONIA   = ?,
                  NUMERO_EXTE     = ?,
                  NUMERO_INTE     = ?,
                  CODIGO_POST        = ?,
                  MUNICIPIO = ?,
                  ESTADO    = ?,
                  PAIS      = ?,
                  CFDI      = ?
                WHERE ID_CLIENTE = ?
                """;

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombreCliente());
            ps.setString(2, c.getRfc());
            ps.setString(3, c.getCorreo());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getCalle());
            ps.setString(6, c.getColonia());
            ps.setInt(7, c.getNoExt());
            ps.setInt(8, c.getNoInt());
            ps.setInt(9, c.getCp());
            ps.setString(10, c.getMunicipio());
            ps.setString(11, c.getEstado());
            ps.setString(12, c.getPais());
            ps.setString(13, c.getCfdi());
            ps.setInt(14, c.getIdCliente());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarUsuario(Usuarios u) {
        String sql = """
                UPDATE USUARIOS SET
                  NOMBRE       = ?,
                  APELLIDOPAT  = ?,
                  APELLIDOMAT  = ?,
                  ROL          = ?
                  WHERE ID_USUARIO = ?
                """;

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidoPat());
            ps.setString(3, u.getApellidoMat());
            ps.setString(4, u.getRolUsuario());
            ps.setInt(5, u.getIdUsuario()); // ← FALTABA ESTA LÍNEA

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarProveedor(Proveedor p) {
        String sql = """
                UPDATE PROVEEDORES SET
                  NOMBRE    = ?,
                  RFC       = ?,
                  CORREO    = ?,
                  TELEFONO  = ?,
                  ESTADO    = ?,
                  CIUDAD    = ?,
                  CALLE     = ?,
                  COLONIA   = ?,
                  NUMERO_EXTE     = ?,
                  NUMERO_INT     = ?,
                  CODIGO_POST        = ?,
                  MUNICIPIO = ?,
                  PAIS      = ?,
                  ESTATUS      = ?
                WHERE ID_PROVEEDOR = ?
                """;

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getRfc());
            ps.setString(3, p.getCorreo());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getEstado());
            ps.setString(6, p.getCiudad());
            ps.setString(7, p.getCalle());
            ps.setString(8, p.getColonia());
            ps.setInt(9, p.getNumeroExte());
            ps.setInt(10, p.getNumeroInt());
            ps.setInt(11, p.getCodigoPost());
            ps.setString(12, p.getMunicipio());
            ps.setString(13, p.getPais());
            ps.setString(14, p.getEstatus().trim().toUpperCase());
            ps.setInt(15, p.getIdProveedor());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarUsuarioPorId(int idUsuario) {
        String sql = "DELETE FROM USUARIOS WHERE ID_USUARIO = ?";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarClientePorId(int idCliente) {
        String sql = "DELETE FROM CLIENTES WHERE ID_CLIENTE = ?";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarProveedorPorId(int idProveedor) {
        String sql = "DELETE FROM PROVEEDORES WHERE ID_PROVEEDOR = ?";
        try (Connection conn = ConexionBD.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProveedor);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // En Datos/ValidacionesBD.java
    public List<Venta> obtenerVentas(LocalDate desde, LocalDate hasta, Integer idVendedor, Integer idCliente) {
        List<Venta> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                    SELECT V.ID_VENTAS, V.FECHA_VENTA, V.TOTAL, V.ID_USUARIO,
                           U.NOMBRE AS VENDEDOR, C.NOMBRE AS CLIENTE
                    FROM VENTAS V
                    JOIN USUARIOS U ON V.ID_USUARIO = U.ID_USUARIO
                    LEFT JOIN CLIENTES C ON V.ID_CLIENTE = C.ID_CLIENTE
                    WHERE DATE(V.FECHA_VENTA) BETWEEN ? AND ?
                """);

        if (idVendedor != null)
            sql.append(" AND V.ID_USUARIO = ").append(idVendedor);
        if (idCliente != null)
            sql.append(" AND V.ID_CLIENTE = ").append(idCliente);
        // Puedes agregar estado aquí si lo implementas

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setDate(1, java.sql.Date.valueOf(desde));
            ps.setDate(2, java.sql.Date.valueOf(hasta));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Venta v = new Venta();
                    v.setIdVenta(rs.getInt("ID_VENTAS"));
                    v.setFecha(rs.getTimestamp("FECHA_VENTA").toLocalDateTime());
                    v.setTotal(rs.getDouble("TOTAL"));
                    v.setIdCliente(rs.getInt("ID_USUARIO"));

                    // Guarda nombres como propiedades auxiliares si quieres
                    v.setClienteNombre(rs.getString("CLIENTE") != null ? rs.getString("CLIENTE") : "Público");
                    v.setVendedorNombre(rs.getString("VENDEDOR"));

                    lista.add(v);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Obtiene el detalle (líneas) de una venta dada.
     */
    public List<DetalleVenta> obtenerDetalleVenta(int idVenta) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = """
                    SELECT D.ID_DETALLE, D.ID_VENTAS, D.ID_PRODUCTOS, D.CANTIDAD,
                           D.PRECIO_UNITARIO, D.SUBTOTAL
                    FROM DETALLE_VENTAS D
                    WHERE D.ID_VENTAS = ?
                """;

        try (Connection conn = ConexionBD.conectar();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta d = new DetalleVenta();
                    d.setIdDetalle(rs.getInt("ID_DETALLE"));
                    d.setIdVenta(rs.getInt("ID_VENTAS"));
                    d.setIdProducto(rs.getString("ID_PRODUCTOS"));
                    d.setCantidad(rs.getInt("CANTIDAD"));
                    d.setPrecioUnitario(rs.getDouble("PRECIO_UNITARIO"));
                    d.setSubtotal(rs.getDouble("SUBTOTAL"));
                    detalles.add(d);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }

}
