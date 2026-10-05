package com.estoque.service;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estoque.domain.Categoria;
import com.estoque.domain.Produto;
import com.estoque.dto.ProdutoRequest;
import com.estoque.dto.ProdutoResponse;
import com.estoque.dto.MovimentacaoEntradaRequest;
import com.estoque.exception.RecursoNaoEncontradoException;
import com.estoque.exception.RegraNegocioException;
import com.estoque.repository.ProdutoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoService {

	private final ProdutoRepository repository;
	private final CategoriaService categoriaService;
	private final EstoqueService estoqueService;

	@Transactional(readOnly = true)
	public Page<ProdutoResponse> listar(Pageable pageable) {
		return repository.findAll(pageable).map(p -> ProdutoResponse.from(p, estoqueService.saldo(p.getId())));
	}

	@Transactional(readOnly = true)
	public ProdutoResponse buscarPorId(Long id) {
		Produto p = encontrar(id);
		return ProdutoResponse.from(p, estoqueService.saldo(p.getId()));
	}

	@Transactional
	public ProdutoResponse criar(ProdutoRequest request) {
		if (repository.existsBySkuIgnoreCase(request.sku())) {
			throw new RegraNegocioException("SKU já cadastrado.");
		}
		
		Categoria cat = categoriaService.encontrar(request.categoriaId());
		
		Produto p = new Produto();
		p.setNome(request.nome());
		p.setSku(request.sku());
		p.setCategoria(cat);
		p.setPrecoVenda(request.precoVenda());
		p.setEstoqueMinimo(request.estoqueMinimo());
		
		Produto salvo = repository.save(p);
		long saldoFinal = 0L;
		
		if (request.quantidadeInicial() != null && request.quantidadeInicial() > 0) {
			BigDecimal custo = request.custoInicial() != null ? request.custoInicial() : BigDecimal.ZERO;
			estoqueService.registrarEntrada(
				new MovimentacaoEntradaRequest(salvo.getId(), request.quantidadeInicial(), custo, "Estoque Inicial")
			);
			saldoFinal = request.quantidadeInicial();
		}
		
		return ProdutoResponse.from(salvo, saldoFinal);
	}

	@Transactional
	public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
		if (repository.existsBySkuIgnoreCaseAndIdNot(request.sku(), id)) {
			throw new RegraNegocioException("SKU já cadastrado em outro produto.");
		}
		
		Produto p = encontrar(id);
		Categoria cat = categoriaService.encontrar(request.categoriaId());
		
		p.setNome(request.nome());
		p.setSku(request.sku());
		p.setCategoria(cat);
		p.setPrecoVenda(request.precoVenda());
		p.setEstoqueMinimo(request.estoqueMinimo());
		
		Produto salvo = repository.save(p);
		return ProdutoResponse.from(salvo, estoqueService.saldo(p.getId()));
	}

	@Transactional
	public void inativar(Long id) {
		Produto p = encontrar(id);
		p.setAtivo(false);
		repository.save(p);
	}

	public Produto encontrar(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
	}
}
