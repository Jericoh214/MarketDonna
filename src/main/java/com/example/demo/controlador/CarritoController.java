package com.example.demo.controlador;

import com.example.demo.modelo.*;
import com.example.demo.repositorio.DetallePedidoRepository;
import com.example.demo.repositorio.PedidoRepository;
import com.example.demo.repositorio.ProductoRepository;
import com.example.demo.repositorio.UsuarioRepository;
import com.example.demo.servicio.CarritoService;
import com.example.demo.servicio.PdfService;

import jakarta.servlet.http.HttpSession; // Importación clave para mantener el PDF en memoria temporal
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

    // 1. NUEVO FLUJO: PROCESAR COMPRA Y REDIRIGIR
    @PostMapping("/carrito/confirmar")
    @Transactional
    public String confirmarPedido(HttpSession session) {
        try {
            if (carritoService.obtenerItems().isEmpty()) {
                return "redirect:/carrito"; // Si está vacío, lo regresamos al carrito
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

            // Generamos el PDF ANTES de limpiar el carrito y lo guardamos en la sesión temporal
            ByteArrayOutputStream pdfStream = pdfService.generarBoletaPdf(
                    carritoService.obtenerItems(), 
                    carritoService.calcularTotal()
            );
            
            session.setAttribute("ultimoPdfGenerado", pdfStream.toByteArray());
            session.setAttribute("ultimoPedidoId", pedidoNuevo.getId());

            // Limpiamos el carrito
            carritoService.limpiarCarrito();

            // Redirigimos a la nueva vista de éxito
            return "redirect:/pedido/exito";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/carrito?error=true";
        }
    }

    // 2. NUEVA RUTA: MOSTRAR LA PANTALLA DE ÉXITO
    @GetMapping("/pedido/exito")
    public String mostrarPantallaExito(HttpSession session, Model model) {
        Long pedidoId = (Long) session.getAttribute("ultimoPedidoId");
        
        // Le pasamos el ID del pedido a la vista HTML para que lo muestre dinámicamente
        if (pedidoId != null) {
            model.addAttribute("numeroPedido", "MD-00" + pedidoId);
        } else {
            model.addAttribute("numeroPedido", "MD-00000");
        }
        
        return "exito_compra";
    }

    // 3. NUEVA RUTA: DESCARGAR EL PDF GUARDADO
    @GetMapping("/comprobante")
    public ResponseEntity<byte[]> descargarComprobante(HttpSession session) {
        byte[] pdfBoleta = (byte[]) session.getAttribute("ultimoPdfGenerado");
        Long pedidoId = (Long) session.getAttribute("ultimoPedidoId");

        // Si por alguna razón no hay PDF en memoria (ej. el usuario entró directo a la URL), damos error 404
        if (pdfBoleta == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Boleta_MarketDonna_ORD-" + pedidoId + ".pdf");
        
        return new ResponseEntity<>(pdfBoleta, headers, HttpStatus.OK);
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