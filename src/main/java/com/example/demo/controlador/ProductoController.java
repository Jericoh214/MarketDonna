package com.example.demo.controlador;

import com.example.demo.modelo.Producto;
import com.example.demo.repositorio.CategoriaRepository;
import com.example.demo.repositorio.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoController(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping("/inventario")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "inventario";
    }

    @GetMapping("/inventario/nuevo")
    public String nuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "formulario_producto";
    }

    @PostMapping("/inventario/guardar")
    public String guardarProducto(@ModelAttribute Producto producto) {
        productoRepository.save(producto);
        return "redirect:/inventario";
    }

    @PostMapping("/inventario/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
        return "redirect:/inventario";
    }
}