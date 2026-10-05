package com.estoque.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.estoque.domain.ItemVenda;
import com.estoque.domain.ItemVenda.TipoDesconto;
import com.estoque.domain.Venda;

public record VendaResponse(
		Long id,
		Instant criadoEm,
		BigDecimal subtotal,
		BigDecimal descontoTotal,
		BigDecimal total,
		String status,
		List<ItemVendaResponse> itens
) {
	public static VendaResponse from(Venda v) {
		return new VendaResponse(
				v.getId(),
				v.getCriadoEm(),
				v.getSubtotal(),
				v.getDescontoTotal(),
				v.getTotal(),
				v.getStatus(),
				v.getItens().stream().map(ItemVendaResponse::from).toList()
		);
	}

	public record ItemVendaResponse(
			Long id,
			Long produtoId,
			String produtoNome,
			int quantidade,
			BigDecimal precoVendaPraticado,
			BigDecimal descontoItem,
			TipoDesconto tipoDesconto,
			BigDecimal valorDescontoInformado
	) {
		public static ItemVendaResponse from(ItemVenda i) {
			return new ItemVendaResponse(
					i.getId(),
					i.getProduto().getId(),
					i.getProduto().getNome(),
					i.getQuantidade(),
					i.getPrecoVendaPraticado(),
					i.getDescontoItem(),
					i.getTipoDesconto(),
					i.getValorDescontoInformado()
			);
		}
	}
}
