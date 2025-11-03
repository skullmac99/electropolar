package com.electropolar;

public class Cliente {

    private int idCliente;
    private String nombreCliente;
    private String rfc;
    private String correo;
    private String telefono;
    private String calle;
    private int noExt;
    private int noInt;
    private int cp;
    private String colonia;
    private String municipio;
    private String estado;
    private String pais;
    private String cfdi;

    public Cliente(int idCliente, String nombreCliente, String rfc, String correo, String telefono, String calle,
           String colonia, int noExt, int noInt, int cp,  String municipio, String estado, String pais,
            String cfdi) {

        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.rfc = rfc;
        this.correo = correo;
        this.telefono = telefono;
        this.calle = calle;
        this.colonia = colonia;
        this.noExt = noExt;
        this.noInt = noInt;
        this.cp = cp;
        this.municipio = municipio;
        this.estado = estado;
        this.pais = pais;
        this.cfdi = cfdi;

    }

    public Cliente(String nombreCliente, String rfc, String correo, String telefono, String calle, String colonia,
            int noExt, int noInt, int cp, String municipio, String estado, String pais, String cfdi) {

        this.nombreCliente = nombreCliente;
        this.rfc = rfc;
        this.correo = correo;
        this.telefono = telefono;
        this.calle = calle;
        this.noExt = noExt;
        this.noInt = noInt;
        this.cp = cp;
        this.colonia = colonia;
        this.municipio = municipio;
        this.estado = estado;
        this.pais = pais;
        this.cfdi = cfdi;

    }

    public Cliente(int idCliente, String nombreCliente) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
    }

    public Cliente() {
        // Constructor vacío necesario para inicializar sin parámetros
    }

    @Override
    public String toString() {
        return idCliente + " - " + nombreCliente;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public int getNoExt() {
        return noExt;
    }

    public void setNoExt(int noExt) {
        this.noExt = noExt;
    }

    public int getNoInt() {
        return noInt;
    }

    public void setNoInt(int noInt) {
        this.noInt = noInt;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCfdi() {
        return cfdi;
    }

    public void setCfdi(String cfdi) {
        this.cfdi = cfdi;
    }

  }
