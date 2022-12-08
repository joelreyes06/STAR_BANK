package com.example.star_bank.Modelos;

public class rlogin {
    String id_usuarios;
    String user;
    String pass;
    String nombre;
    String ncuenta;

    public rlogin() {
        //
    }

    public rlogin(String id_usuarios, String user, String pass, String nombre, String ncuenta) {
        this.id_usuarios = id_usuarios;
        this.user = user;
        this.pass = pass;
        this.nombre = nombre;
        this.ncuenta = ncuenta;
    }

    public String getId_usuarios() {
        return id_usuarios;
    }

    public void setId_usuarios(String id_usuarios) {
        this.id_usuarios = id_usuarios;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNcuenta() {
        return ncuenta;
    }

    public void setNcuenta(String ncuenta) {
        this.ncuenta = ncuenta;
    }
}
