package com.estoque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.estoque.domain.MovimentacaoEstoque;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

	/** Estoque real = somatorio do Kardex. */
	@Query("select coalesce(sum(m.quantidade), 0) from MovimentacaoEstoque m where m.produto.id = :produtoId")
	long saldo(@Param("produtoId") Long produtoId);
}
