package com.electropolar;

public class Usuarios {

    private int idUsuario;
    private String nombre;
    private String apellidoPat;
    private String apellidoMat;
    private String rolUsuario;

    public Usuarios(int idUsuario, String nombre, String apellidoPat, String apellidoMat, String rolUsuario) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidoPat = apellidoPat;
        this.apellidoMat = apellidoMat;
        this.rolUsuario = rolUsuario;
    }

    public Usuarios( String nombre, String apellidoPat, String apellidoMat, String rolUsuario) {
        this.nombre = nombre;
        this.apellidoPat = apellidoPat;
        this.apellidoMat = apellidoMat;
        this.rolUsuario = rolUsuario;
    }

    public Usuarios(){
        
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPat() {
        return apellidoPat;
    }

    public void setApellidoPat(String apellidoPat) {
        this.apellidoPat = apellidoPat;
    }

    public String getApellidoMat() {
        return apellidoMat;
    }

    public void setApellidoMat(String apellidoMat) {
        this.apellidoMat = apellidoMat;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }
}
