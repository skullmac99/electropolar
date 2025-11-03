package Datos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import com.electropolar.Cliente;
import com.electropolar.Producto;
import com.electropolar.Vendedor;
import com.electropolar.Proveedor;

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
        String sql = "SELECT * FROM PRODUCTOS WHERE ID_PRODUCTOS = ?";
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
                            rs.getInt("STOCK"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
            stmt.setDouble(5, producto.getPrecio());
            stmt.setInt(6, producto.getStock());
            stmt.setInt(7, producto.getIdproveedor());

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
}
