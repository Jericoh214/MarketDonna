package com.example.demo.servicio;

import com.example.demo.modelo.Usuario;
import com.example.demo.repositorio.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // RADAR: Esto se imprimirá en tu consola cuando presiones "INGRESAR"
        System.out.println("====== INTENTO DE LOGIN CON EL CORREO: " + email + " ======");

        Usuario usuario = usuarioRepository.findByEmail(email);
        
        if (usuario == null) {
            System.out.println("====== ERROR: USUARIO NO ENCONTRADO EN LA BD ======");
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        
        System.out.println("====== ÉXITO: USUARIO ENCONTRADO, VERIFICANDO CONTRASEÑA... ======");
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword()) 
                .roles(usuario.getRol()) 
                .build();
    }
}