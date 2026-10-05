package com.estoque.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProdutoRequest(
		@NotBlank(message = "Nome é obrigatório")
		String nome,
		
		@NotBlank(message = "SKU é obrigatório")
		String sku,
		
		@NotNull(message = "Categoria é obrigatória")
		Long categoriaId,
		
		@NotNull(message = "Preço de venda é obrigatório")
		@PositiveOrZero(message = "Preço de venda deve ser maior ou igual a zero")
		BigDecimal precoVenda,
		
		@Min(value = 0, message = "Estoque mínimo deve ser maior ou igual a zero")
		int estoqueMinimo,

		@Min(value = 0, message = "Quantidade inicial deve ser maior ou igual a zero")
		Integer quantidadeInicial,

		BigDecimal custoInicial
) {}
