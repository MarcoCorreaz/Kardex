package com.estoque.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.estoque.domain.MovimentacaoEstoque;
import com.estoque.domain.OrigemMovimentacao;
import com.estoque.domain.Produto;
import com.estoque.domain.TipoMovimentacao;
import com.estoque.domain.Venda;
import com.estoque.exception.EstoqueInsuficienteException;
import com.estoque.exception.RecursoNaoEncontradoException;
import com.estoque.exception.RegraNegocioException;
import com.estoque.repository.MovimentacaoEstoqueRepository;
import com.estoque.repository.ProdutoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Unico ponto que escreve no Kardex. Todos os metodos participam da transacao
 * do chamador (MANDATORY/REQUIRED), de modo que a venda e a baixa de estoque
 * sofrem commit/rollback juntas.
 */
@Service
@RequiredArgsConstructor
public class EstoqueService {

	private final ProdutoRepository produtos;
	private final MovimentacaoEstoqueRepository movimentacoes;
	private final CustoMedioService custoMedio;

	@Transactional(readOnly = true)
	public long saldo(Long produtoId) {
		return movimentacoes.saldo(produtoId);
	}

	/** Trava a linha do produto (FOR UPDATE) e exige que esteja ativo. */
	@Transactional(propagation = Propagation.MANDATORY)
	public Produto travarProdutoAtivo(Long produtoId) {
		Produto p = produtos.findByIdForUpdate(produtoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto", produtoId));
		if (!p.isAtivo()) {
			throw new RegraNegocioException("Produto inativo: " + p.getSku());
		}
		return p;
	}

	/** Entrada de compra: recalcula o custo medio ponderado. */
	@Transactional
	public MovimentacaoEstoque registrarEntrada(Long produtoId, int quantidade, BigDecimal custoAquisicao, String motivo) {
		if (quantidade <= 0) {
			throw new RegraNegocioException("Quantidade da entrada deve ser maior que zero.");
		}
		if (custoAquisicao == null || custoAquisicao.signum() < 0) {
			throw new RegraNegocioException("Custo de aquisicao invalido.");
		}
		Produto p = travarProdutoAtivo(produtoId);
		long saldo = movimentacoes.saldo(produtoId);
		BigDecimal novoCusto = custoMedio.novoCustoMedio(saldo, p.getCustoMedio(), quantidade, custoAquisicao);
		p.setCustoMedio(novoCusto);

		return gravar(p, TipoMovimentacao.ENTRADA, OrigemMovimentacao.COMPRA, quantidade,
				custoAquisicao, novoCusto, saldo + quantidade, null, motivo);
	}

	/** Ajuste de inventario fisico (+/-). Nao altera o custo medio. */
	@Transactional
	public MovimentacaoEstoque registrarAjuste(Long produtoId, int quantidadeComSinal, String motivo) {
		if (quantidadeComSinal == 0) {
			throw new RegraNegocioException("Ajuste deve ter quantidade diferente de zero.");
		}
		if (motivo == null || motivo.isBlank()) {
			throw new RegraNegocioException("Motivo do ajuste e obrigatorio.");
		}
		Produto p = travarProdutoAtivo(produtoId);
		long saldo = movimentacoes.saldo(produtoId);
		long novoSaldo = saldo + quantidadeComSinal;
		if (novoSaldo < 0) {
			throw new EstoqueInsuficienteException(p.getSku(), saldo, -(long) quantidadeComSinal);
		}
		return gravar(p, TipoMovimentacao.AJUSTE, OrigemMovimentacao.AJUSTE_INVENTARIO, quantidadeComSinal,
				p.getCustoMedio(), p.getCustoMedio(), novoSaldo, null, motivo);
	}

	/** Saida por venda. Deve ser chamada dentro da transacao da venda, com o produto ja travado. */
	@Transactional(propagation = Propagation.MANDATORY)
	public MovimentacaoEstoque registrarSaidaVenda(Produto produtoTravado, int quantidade, Venda venda) {
		long saldo = movimentacoes.saldo(produtoTravado.getId());
		if (saldo < quantidade) {
			throw new EstoqueInsuficienteException(produtoTravado.getSku(), saldo, quantidade);
		}
		return gravar(produtoTravado, TipoMovimentacao.SAIDA, OrigemMovimentacao.VENDA, -quantidade,
				produtoTravado.getCustoMedio(), produtoTravado.getCustoMedio(), saldo - quantidade, venda, null);
	}

	private MovimentacaoEstoque gravar(Produto p, TipoMovimentacao tipo, OrigemMovimentacao origem, int qtd,
			BigDecimal custoUnit, BigDecimal custoMedioApos, long saldoApos, Venda venda, String motivo) {
		MovimentacaoEstoque m = new MovimentacaoEstoque();
		m.setProduto(p);
		m.setTipo(tipo);
		m.setOrigem(origem);
		m.setQuantidade(qtd);
		m.setCustoUnitario(custoUnit);
		m.setCustoMedioApos(custoMedioApos);
		m.setSaldoApos((int) saldoApos);
		m.setVenda(venda);
		m.setMotivo(motivo);
		return movimentacoes.save(m);
	}
}
