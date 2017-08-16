package com.android.stocknfc;

/**
 * Created by Desarrollo on 20/06/2017.
 */

public class Articulo {
    private String nombre;
    private int stock;
    private int id;

    public Articulo(){};

    public Articulo(String nombre, int stock, int id) {
        this.nombre = nombre;
        this.stock = stock;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getStock() {
        return stock;
    }

    public int getId() {
        return id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setId(int id) {
        this.id = id;
    }
}
