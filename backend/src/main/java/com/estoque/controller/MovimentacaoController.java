package com.estoque.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.estoque.domain.MovimentacaoEstoque;
import com.estoque.dto.MovimentacaoAjusteRequest;
import com.estoque.dto.MovimentacaoEntradaRequest;
import com.estoque.dto.MovimentacaoResponse;
import com.estoque.service.EstoqueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movimentacoes")
@RequiredArgsConstructor
public class MovimentacaoController {

	private final EstoqueService service;

	@PostMapping("/entrada")
	@ResponseStatus(HttpStatus.CREATED)
	public MovimentacaoResponse entrada(@RequestBody @Valid MovimentacaoEntradaRequest req) {
		MovimentacaoEstoque m = service.registrarEntrada(req.produtoId(), req.quantidade(), req.custoAquisicao(), req.motivo());
		return MovimentacaoResponse.from(m);
	}

	@PostMapping("/ajuste")
	@ResponseStatus(HttpStatus.CREATED)
	public MovimentacaoResponse ajuste(@RequestBody @Valid MovimentacaoAjusteRequest req) {
		MovimentacaoEstoque m = service.registrarAjuste(req.produtoId(), req.quantidade(), req.motivo());
		return MovimentacaoResponse.from(m);
	}
}
