package com.estoque.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.estoque.domain.Produto;

public record ProdutoResponse(
		Long id,
		String nome,
		String sku,
		CategoriaResponse categoria,
		BigDecimal custoMedio,
		BigDecimal precoVenda,
		int estoqueMinimo,
		boolean ativo,
		Instant criadoEm,
		Instant atualizadoEm,
		long saldoEstoque
) {
	public static ProdutoResponse from(Produto p, long saldoEstoque) {
		return new ProdutoResponse(
				p.getId(),
				p.getNome(),
				p.getSku(),
				p.getCategoria() != null ? CategoriaResponse.from(p.getCategoria()) : null,
				p.getCustoMedio(),
				p.getPrecoVenda(),
				p.getEstoqueMinimo(),
				p.isAtivo(),
				p.getCriadoEm(),
				p.getAtualizadoEm(),
				saldoEstoque
		);
	}
}
