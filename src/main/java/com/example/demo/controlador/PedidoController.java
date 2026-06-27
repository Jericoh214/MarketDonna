package com.example.demo.controlador;

import com.example.demo.modelo.Pedido;
import com.example.demo.repositorio.PedidoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List; 

@Controller
public class PedidoController {

    private final PedidoRepository pedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/admin/pedidos")
    public String bandejaPedidos(Model model) {
        List<Pedido> listaPedidos = pedidoRepository.findAll();
        
        model.addAttribute("pedidos", listaPedidos);
        return "bandeja_pedidos";
    }
}