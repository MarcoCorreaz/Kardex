package com.estoque.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.estoque.domain.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
