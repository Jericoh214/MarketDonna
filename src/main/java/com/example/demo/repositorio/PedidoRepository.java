package com.example.demo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.modelo.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}