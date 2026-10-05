package com.estoque.dto;

import java.math.BigDecimal;

public record ResumoRelatorio(
		BigDecimal faturamentoBruto,
		BigDecimal lucroLiquidoReal,
		BigDecimal margemMediaPercentual
) {}
