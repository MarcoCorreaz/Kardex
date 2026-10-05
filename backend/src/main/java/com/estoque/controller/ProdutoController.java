package com.estoque.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.estoque.dto.ProdutoRequest;
import com.estoque.dto.ProdutoResponse;
import com.estoque.service.ProdutoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

	private final ProdutoService service;

	@GetMapping
	public Page<ProdutoResponse> listar(Pageable pageable) {
		// MVP: listando todos com suporte a paginacao
		return service.listar(pageable);
	}

	@GetMapping("/{id}")
	public ProdutoResponse buscar(@PathVariable Long id) {
		return service.buscarPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProdutoResponse criar(@RequestBody @Valid ProdutoRequest request) {
		return service.criar(request);
	}

	@PutMapping("/{id}")
	public ProdutoResponse atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoRequest request) {
		return service.atualizar(id, request);
	}

	@PatchMapping("/{id}/status")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inativar(@PathVariable Long id) {
		service.inativar(id);
	}
}
