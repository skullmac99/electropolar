package com.electropolar;

public class Proveedor {

    private int idProveedor;
    private String nombre;
    private String rfc;
    private String correo;
    private String telefono;
    private String estado;
    private String ciudad;
    private String calle;
    private String colonia;
    private int numeroExte;
    private int numeroInt;
    private int codigoPost;
    private String municipio;
    private String pais;
    private String estatus; // ACTIVO o INACTIVO

    /**
     * Constructor completo con todos los atributos.
     * Se usa, por ejemplo, al recuperar los datos desde la base de datos.
     */
    public Proveedor(int idProveedor, String nombre, String rfc, String correo, String telefono,
                     String estado, String ciudad, String calle, String colonia,
                     int numeroExte, int numeroInt, int codigoPost,
                     String municipio, String pais, String estatus) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.rfc = rfc;
        this.correo = correo;
        this.telefono = telefono;
        this.estado = estado;
        this.ciudad = ciudad;
        this.calle = calle;
        this.colonia = colonia;
        this.numeroExte = numeroExte;
        this.numeroInt = numeroInt;
        this.codigoPost = codigoPost;
        this.municipio = municipio;
        this.pais = pais;
        this.estatus = estatus;
    }

    /**
     * Constructor sin ID ni estatus.
     * Ideal para cuando se va a registrar un nuevo proveedor (alta).
     * El ID lo genera la base de datos automáticamente y el estatus por defecto es "ACTIVO".
     */
    public Proveedor(String nombre, String rfc, String correo, String telefono,
                     String estado, String ciudad, String calle, String colonia,
                     int numeroExte, int numeroInt, int codigoPost,
                     String municipio, String pais) {
        this.nombre = nombre;
        this.rfc = rfc;
        this.correo = correo;
        this.telefono = telefono;
        this.estado = estado;
        this.ciudad = ciudad;
        this.calle = calle;
        this.colonia = colonia;
        this.numeroExte = numeroExte;
        this.numeroInt = numeroInt;
        this.codigoPost = codigoPost;
        this.municipio = municipio;
        this.pais = pais;
        this.estatus = "ACTIVO"; // valor por defecto
    }

    /**
     * Constructor simple con ID y nombre.
     * Útil para mostrar en combos o listados donde solo se requiere identificar al proveedor.
     */
    public Proveedor(int idProveedor, String nombre) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
    }

    /**
     * Constructor vacío.
     * Necesario para inicializar sin parámetros (por ejemplo, frameworks o beans).
     */
    public Proveedor() {
    }

    @Override
    public String toString() {
        return idProveedor + " - " + nombre;
    }

    // ---------------- Getters y Setters ----------------

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public int getNumeroExte() {
        return numeroExte;
    }

    public void setNumeroExte(int numeroExte) {
        this.numeroExte = numeroExte;
    }

    public int getNumeroInt() {
        return numeroInt;
    }

    public void setNumeroInt(int numeroInt) {
        this.numeroInt = numeroInt;
    }

    public int getCodigoPost() {
        return codigoPost;
    }

    public void setCodigoPost(int codigoPost) {
        this.codigoPost = codigoPost;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
