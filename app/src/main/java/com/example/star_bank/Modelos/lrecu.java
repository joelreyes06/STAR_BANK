package com.example.star_bank.Modelos;

public class lrecu {
    String id_usuarios;
    String user;
    String pass;
    String email;
    String nombre;

    public lrecu() {
        //
    }

    public lrecu(String id_usuarios, String user, String pass, String email, String nombre) {
        this.id_usuarios = id_usuarios;
        this.user = user;
        this.pass = pass;
        this.email = email;
        this.nombre = nombre;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
