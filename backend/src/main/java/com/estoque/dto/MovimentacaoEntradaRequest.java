package com.estoque.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record MovimentacaoEntradaRequest(
		@NotNull(message = "Produto é obrigatório")
		Long produtoId,

		@Positive(message = "Quantidade deve ser maior que zero")
		int quantidade,

		@NotNull(message = "Custo de aquisição é obrigatório")
		@PositiveOrZero(message = "Custo de aquisição não pode ser negativo")
		BigDecimal custoAquisicao,

		String motivo
) {}
