package com.example.demo.controlador;

import com.example.demo.repositorio.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TiendaController {

    private final ProductoRepository productoRepository;

    public TiendaController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/")
    public String mostrarTienda(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "index";
    }
}