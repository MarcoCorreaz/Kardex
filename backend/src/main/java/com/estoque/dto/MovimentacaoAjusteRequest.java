package com.estoque.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MovimentacaoAjusteRequest(
		@NotNull(message = "Produto é obrigatório")
		Long produtoId,

		@NotNull(message = "Quantidade é obrigatória (pode ser negativa)")
		Integer quantidade,

		@NotBlank(message = "Motivo do ajuste é obrigatório")
		String motivo
) {}
