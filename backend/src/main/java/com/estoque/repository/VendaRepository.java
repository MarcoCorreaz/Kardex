package com.estoque.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.estoque.domain.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long> {
}
