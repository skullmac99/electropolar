package com.electropolar;

public class Producto {
    private String id;
    private String nombre;
    private String descripcion;
    private String unidad;
    private double precio;
    private int stock;
    private int idproveedor;

    //Para busqueda de productos
    public Producto(String id, String nombre, String descripcion, String unidad, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidad = unidad;
        this.precio = precio;
        this.stock = stock;
    }

    //Alta de productos
    public Producto(String id, String nombre, String descripcion, String unidad, double precio, int stock, int idproveedor) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidad = unidad;
        this.precio = precio;
        this.stock = stock;
        this.idproveedor = idproveedor;
    }
     public Producto(){
        
     }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getUnidad() { return unidad; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public int getIdproveedor() {return idproveedor;}
    
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
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setIdproveedor(int idproveedor) {
        this.idproveedor = idproveedor;
    }

}

