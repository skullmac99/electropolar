package com.electropolar;

import java.time.LocalDateTime;
import java.util.List;

public class Venta {

    private int idVenta;
    private int idCliente; // opcional si se desea ligar ventas a clientes
    private LocalDateTime fecha;
    String clienteNombre;
    String vendedorNombre;
    private double total;
    private List<DetalleVenta> detalles; // relación con los productos vendidos

    // Constructor vacío
    public Venta() {}

    // Constructor con parámetros (sin detalles)
    public Venta(int idVenta, int idCliente, LocalDateTime fecha, double total) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = total;
    }

    // Constructor con lista de detalles (opcional)
    public Venta(int idVenta, int idCliente, LocalDateTime fecha, double total, List<DetalleVenta> detalles) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = total;
        this.detalles = detalles;
    }

    // Getters y Setters
    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "idVenta=" + idVenta +
                ", idCliente=" + idCliente +
                ", fecha=" + fecha +
                ", total=" + total +
                '}';
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getVendedorNombre() {
        return vendedorNombre;
    }

    public void setVendedorNombre(String vendedorNombre) {
        this.vendedorNombre = vendedorNombre;
    }
}
