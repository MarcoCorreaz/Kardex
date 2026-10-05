package com.estoque.dto;

import java.math.BigDecimal;

public record RankingProduto(
		Long produtoId,
		String produtoNome,
		String sku,
		int quantidadeVendida,
		BigDecimal faturamento,
		BigDecimal lucro,
		BigDecimal margemPercentual
) {}
