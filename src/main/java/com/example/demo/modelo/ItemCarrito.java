package com.example.demo.modelo;

public class ItemCarrito {
    
    private Producto producto;
    private int cantidad;
    private double subtotal;

    public ItemCarrito(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public void calcularSubtotal() {
        if (this.producto != null) {
            this.subtotal = this.producto.getPrecio() * this.cantidad;
        }
    }

    // Getters y Setters
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { 
        this.cantidad = cantidad; 
        calcularSubtotal(); // Recalcula si cambia la cantidad
    }
    public double getSubtotal() { return subtotal; }
}