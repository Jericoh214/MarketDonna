package com.example.demo.repositorio;

import com.example.demo.modelo.Usuario; // ¡Esta importación es vital para evitar errores!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Método exacto que busca en la columna "email" de la base de datos
    Usuario findByEmail(String email);
    
}