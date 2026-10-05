package com.estoque.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.estoque.domain.MovimentacaoEstoque;
import com.estoque.domain.OrigemMovimentacao;
import com.estoque.domain.TipoMovimentacao;

public record MovimentacaoResponse(
		Long id,
		Long produtoId,
		TipoMovimentacao tipo,
		OrigemMovimentacao origem,
		int quantidade,
		BigDecimal custoUnitario,
		BigDecimal custoMedioApos,
		int saldoApos,
		Long vendaId,
		String motivo,
		Instant criadoEm
) {
	public static MovimentacaoResponse from(MovimentacaoEstoque m) {
		return new MovimentacaoResponse(
				m.getId(),
				m.getProduto().getId(),
				m.getTipo(),
				m.getOrigem(),
				m.getQuantidade(),
				m.getCustoUnitario(),
				m.getCustoMedioApos(),
				m.getSaldoApos(),
				m.getVenda() != null ? m.getVenda().getId() : null,
				m.getMotivo(),
				m.getCriadoEm()
		);
	}
}
