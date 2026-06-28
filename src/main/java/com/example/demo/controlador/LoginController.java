package com.example.demo.controlador;

import com.example.demo.modelo.Usuario;
import com.example.demo.repositorio.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // 1. Muestra la pantalla HTML
    @GetMapping("/login")
    public String mostrarPantallaLogin() {
        return "login";
    }

    // 2. Procesa los datos del formulario manualmente
    @PostMapping("/login")
    public String procesarLoginManual(@RequestParam("username") String email, 
                                      @RequestParam("password") String password, 
                                      HttpSession session, 
                                      Model model) {
                                          
        // Buscamos al usuario en la base de datos
        Usuario usuario = usuarioRepository.findByEmail(email);

        // Validamos si existe y si la contraseña coincide exactamente
        if (usuario != null && usuario.getPassword().equals(password)) {
            
            // ¡Éxito! Guardamos al usuario en la sesión ("memoria" del servidor)
            session.setAttribute("usuarioLogueado", usuario);
            
            // Lo enviamos al carrito
            return "redirect:/carrito"; 
        } else {
            // Falla: Le devolvemos un mensaje de error a la vista
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }
    }
    
    // 3. Método para cerrar sesión
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.removeAttribute("usuarioLogueado"); // Borramos sus datos
        return "redirect:/"; // Lo enviamos a la página principal
    }
}