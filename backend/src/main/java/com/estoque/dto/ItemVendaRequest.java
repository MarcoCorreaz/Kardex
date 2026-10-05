package com.estoque.dto;

import java.math.BigDecimal;

import com.estoque.domain.ItemVenda.TipoDesconto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ItemVendaRequest(
		@NotNull(message = "Produto é obrigatório")
		Long produtoId,

		@Positive(message = "Quantidade deve ser maior que zero")
		int quantidade,

		@NotNull(message = "Desconto é obrigatório")
		@PositiveOrZero(message = "Desconto não pode ser negativo")
		BigDecimal desconto,

		@NotNull(message = "Tipo de desconto é obrigatório")
		TipoDesconto tipoDesconto
) {}
