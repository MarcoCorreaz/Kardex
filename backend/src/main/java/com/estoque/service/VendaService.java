package com.estoque.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estoque.domain.ItemVenda;
import com.estoque.domain.ItemVenda.TipoDesconto;
import com.estoque.domain.Produto;
import com.estoque.domain.Venda;
import com.estoque.dto.ItemVendaRequest;
import com.estoque.dto.VendaRequest;
import com.estoque.dto.VendaResponse;
import com.estoque.exception.RegraNegocioException;
import com.estoque.repository.VendaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendaService {

	private final VendaRepository vendaRepository;
	private final EstoqueService estoqueService;

	@Transactional
	public VendaResponse realizarVenda(VendaRequest request) {
		Venda venda = new Venda();
		BigDecimal subtotal = BigDecimal.ZERO;
		BigDecimal descontoTotal = BigDecimal.ZERO;

		// 1. Validacoes e construcao dos itens
		// Ordenar os IDs dos produtos evitaria deadlocks em concorrencia alta, 
		// mas para o MVP vamos travar na ordem da request.
		for (ItemVendaRequest reqItem : request.itens()) {
			// travarProdutoAtivo ja lanca excecao se inativo ou nao encontrar
			Produto produto = estoqueService.travarProdutoAtivo(reqItem.produtoId());
			
			ItemVenda item = new ItemVenda();
			item.setProduto(produto);
			item.setQuantidade(reqItem.quantidade());
			item.setPrecoVendaPraticado(produto.getPrecoVenda());
			item.setCustoMedioSnapshot(produto.getCustoMedio());
			
			// Calcular desconto
			BigDecimal valorTotalSemDesconto = produto.getPrecoVenda().multiply(BigDecimal.valueOf(reqItem.quantidade()));
			BigDecimal valorDescontoEmReais = BigDecimal.ZERO;
			
			if (reqItem.desconto() != null && reqItem.desconto().compareTo(BigDecimal.ZERO) > 0) {
				if (reqItem.tipoDesconto() == TipoDesconto.PERCENTUAL) {
					// desconto = (total * percentual) / 100
					valorDescontoEmReais = valorTotalSemDesconto.multiply(reqItem.desconto())
							.divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_EVEN);
				} else {
					valorDescontoEmReais = reqItem.desconto();
				}
			}
			
			if (valorDescontoEmReais.compareTo(valorTotalSemDesconto) > 0) {
				throw new RegraNegocioException("Desconto do item excede o valor total do produto " + produto.getSku());
			}
			
			item.setDescontoItem(valorDescontoEmReais);
			item.setTipoDesconto(reqItem.tipoDesconto());
			item.setValorDescontoInformado(reqItem.desconto() != null ? reqItem.desconto() : BigDecimal.ZERO);
			
			venda.adicionarItem(item);
			
			subtotal = subtotal.add(valorTotalSemDesconto);
			descontoTotal = descontoTotal.add(valorDescontoEmReais);
			
			// Baixa no estoque
			estoqueService.registrarSaidaVenda(produto, reqItem.quantidade(), venda);
		}
		
		venda.setSubtotal(subtotal);
		venda.setDescontoTotal(descontoTotal);
		venda.setTotal(subtotal.subtract(descontoTotal));
		
		Venda salva = vendaRepository.save(venda);
		
		return VendaResponse.from(salva);
	}
}
