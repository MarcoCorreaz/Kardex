package com.estoque.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.estoque.domain.Loja;

public interface LojaRepository extends JpaRepository<Loja, Long> {
}
