package com.example.demo.controlador;

import com.example.demo.modelo.*;
import com.example.demo.repositorio.DetallePedidoRepository;
import com.example.demo.repositorio.PedidoRepository;
import com.example.demo.repositorio.ProductoRepository;
import com.example.demo.repositorio.UsuarioRepository;
import com.example.demo.servicio.CarritoService;
import com.example.demo.servicio.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PdfService pdfService;

    public CarritoController(CarritoService carritoService, ProductoRepository productoRepository, 
                             PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository, 
                             UsuarioRepository usuarioRepository, PdfService pdfService) {
        this.carritoService = carritoService;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pdfService = pdfService;
    }

    @GetMapping("/carrito")
    public String mostrarCarrito(Model model) {
        model.addAttribute("itemsCarrito", carritoService.obtenerItems());
        model.addAttribute("totalPagar", carritoService.calcularTotal());
        model.addAttribute("sugerencias", productoRepository.findAll()); 
        return "carrito";
    }

    @PostMapping("/carrito/agregar/{id}")
    public String agregarAlCarrito(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null && producto.getStock() > 0) {
            carritoService.agregarProducto(producto, 1);
        }
        return "redirect:/"; 
    }

    @PostMapping("/carrito/confirmar")
    @Transactional
    public ResponseEntity<byte[]> confirmarPedido() {
        try {
            if (carritoService.obtenerItems().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            
            Usuario clienteTemporal = usuarioRepository.findById(2L).orElse(null);

           
            Pedido pedidoNuevo = new Pedido();
            pedidoNuevo.setUsuario(clienteTemporal);
            pedidoNuevo.setFechaPedido(LocalDateTime.now());
            pedidoNuevo.setEstado(EstadoPedido.PENDIENTE);
            pedidoNuevo.setTotal(BigDecimal.valueOf(carritoService.calcularTotal()));
            
            
            pedidoRepository.save(pedidoNuevo);

          
            for (ItemCarrito item : carritoService.obtenerItems()) {
                
               
                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedidoNuevo);
                detalle.setProducto(item.getProducto());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getProducto().getPrecio());
                detalle.setSubtotal(item.getSubtotal());
                detallePedidoRepository.save(detalle);

               
                Producto productoComprado = item.getProducto();
                int nuevoStock = productoComprado.getStock() - item.getCantidad();
                productoComprado.setStock(nuevoStock);
                productoRepository.save(productoComprado);
            }

            ByteArrayOutputStream pdfStream = pdfService.generarBoletaPdf(
                    carritoService.obtenerItems(), 
                    carritoService.calcularTotal()
            );

            carritoService.limpiarCarrito();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Boleta_MarketDonna_ORD-" + pedidoNuevo.getId() + ".pdf");
            
            return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/carrito/sumar/{id}")
    public String sumarItem(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            carritoService.agregarProducto(producto, 1);
        }
        return "redirect:/carrito"; 
    }

    @GetMapping("/carrito/restar/{id}")
    public String restarItem(@PathVariable Long id) {
        carritoService.restarProducto(id);
        return "redirect:/carrito";
    }

    @GetMapping("/carrito/eliminar/{id}")
    public String eliminarItem(@PathVariable Long id) {
        carritoService.eliminarProducto(id);
        return "redirect:/carrito";
    }
}