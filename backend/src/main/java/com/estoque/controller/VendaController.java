package com.estoque.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.estoque.dto.VendaRequest;
import com.estoque.dto.VendaResponse;
import com.estoque.service.VendaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {

	private final VendaService service;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public VendaResponse realizarVenda(@RequestBody @Valid VendaRequest req) {
		return service.realizarVenda(req);
	}
}
