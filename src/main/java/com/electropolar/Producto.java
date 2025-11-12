package com.electropolar;

public class Producto {
    private String id;
    private String nombre;
    private String descripcion;
    private String unidad;
    
    // [CAMBIO 1]: Renombrado de 'precio' a 'costoPromedio'
    private double costoPromedio; // Este es el PRECIO_COMPRA de tu BD
    
    private int stock;
    private int idproveedor;

    // [CAMBIO 2]: Campos adicionales que vimos en tus consultas
    private double precioVenta;
    private double utilidades;

    /**
     * Constructor vacío
     */
    public Producto() {
    }

    /**
     * [CAMBIO 3]: Constructor para lógica de Admin (Altas y Búsquedas por Clave)
     * Contiene los 7 campos esenciales para la lógica de costo promedio.
     */
    public Producto(String id, String nombre, String descripcion, String unidad, 
                    double costoPromedio, int stock, int idproveedor) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidad = unidad;
        this.costoPromedio = costoPromedio; // Asignado al campo correcto
        this.stock = stock;
        this.idproveedor = idproveedor;
    }

    /**
     * [CAMBIO 4]: Constructor COMPLETO.
     * Ideal para ser usado por 'obtenerTodosProductos'.
     */
    public Producto(String id, String nombre, String descripcion, String unidad, 
                    double costoPromedio, double precioVenta, double utilidades, 
                    int stock, int idproveedor) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidad = unidad;
        this.costoPromedio = costoPromedio;
        this.precioVenta = precioVenta;
        this.utilidades = utilidades;
        this.stock = stock;
        this.idproveedor = idproveedor;
    }

    // --- Getters ---
    
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getUnidad() { return unidad; }
    public int getStock() { return stock; }
    public int getIdproveedor() { return idproveedor; }

    // [CAMBIO 1]: Getter renombrado
    public double getCostoPromedio() { return costoPromedio; } 

    // [CAMBIO 2]: Getters nuevos
    public double getPrecioVenta() { return precioVenta; }
    public double getUtilidades() { return utilidades; }

    // --- Setters ---
    
    public void setId(String id) {
        this.id = id;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public void setIdproveedor(int idproveedor) {
        this.idproveedor = idproveedor;
    }
    
    // [CAMBIO 1]: Setter renombrado
    public void setCostoPromedio(double costoPromedio) { 
        this.costoPromedio = costoPromedio;
    }

    // [CAMBIO 2]: Setters nuevos
    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }
    public void setUtilidades(double utilidades) {
        this.utilidades = utilidades;
    }
}