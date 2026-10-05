package com.estoque.dto;

import com.estoque.domain.Categoria;
import java.time.Instant;

public record CategoriaResponse(
		Long id,
		String nome,
		boolean ativo,
		Instant criadoEm
) {
	public static CategoriaResponse from(Categoria c) {
		return new CategoriaResponse(c.getId(), c.getNome(), c.isAtivo(), c.getCriadoEm());
	}
}
