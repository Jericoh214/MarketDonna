package com.example.demo.servicio;

import com.example.demo.modelo.ItemCarrito;
import com.example.demo.modelo.Producto;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CarritoService {
    
    private List<ItemCarrito> items = new ArrayList<>();

    public void agregarProducto(Producto producto, int cantidad) {
        for (ItemCarrito item : items) {
            if (item.getProducto().getId().equals(producto.getId())) {
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        items.add(new ItemCarrito(producto, cantidad));
    }

    public List<ItemCarrito> obtenerItems() {
        return items;
    }

    public double calcularTotal() {
        double total = 0;
        for (ItemCarrito item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void limpiarCarrito() {
        items.clear();
    }
    public void restarProducto(Long idProducto) {
        items.removeIf(item -> {
            if (item.getProducto().getId().equals(idProducto)) {
                item.setCantidad(item.getCantidad() - 1);
                return item.getCantidad() <= 0; 
            }
            return false;
        });
    }

    public void eliminarProducto(Long idProducto) {
        items.removeIf(item -> item.getProducto().getId().equals(idProducto));
    }
}