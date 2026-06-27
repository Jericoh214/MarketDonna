package com.example.demo.controlador;

import com.example.demo.modelo.Usuario;
import com.example.demo.repositorio.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // inyectamos el repositorio y el encriptador
    public LoginController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; 
    }
    
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro"; 
    }

    @GetMapping("/recuperar-password")
    public String mostrarRecuperarPassword() {
        return "recuperar_password"; 
    }


    @PostMapping("/registro/guardar")
    public String registrarUsuario(@ModelAttribute Usuario usuario) {
        
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        
        usuario.setRol("CLIENTE");
        
        usuarioRepository.save(usuario);
        
        return "redirect:/login?exito";
    }
}