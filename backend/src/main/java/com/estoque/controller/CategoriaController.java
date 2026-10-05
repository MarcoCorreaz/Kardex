package com.estoque.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.estoque.dto.CategoriaRequest;
import com.estoque.dto.CategoriaResponse;
import com.estoque.service.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

	private final CategoriaService service;

	@GetMapping
	public Page<CategoriaResponse> listar(Pageable pageable) {
		return service.listar(pageable);
	}

	@GetMapping("/{id}")
	public CategoriaResponse buscar(@PathVariable Long id) {
		return service.buscarPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoriaResponse criar(@RequestBody @Valid CategoriaRequest request) {
		return service.criar(request);
	}

	@PutMapping("/{id}")
	public CategoriaResponse atualizar(@PathVariable Long id, @RequestBody @Valid CategoriaRequest request) {
		return service.atualizar(id, request);
	}

	@PatchMapping("/{id}/status")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inativar(@PathVariable Long id) {
		service.inativar(id);
	}
}
