package com.estoque.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.estoque.dto.RankingProduto;
import com.estoque.dto.ResumoRelatorio;
import com.estoque.service.RelatorioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

	private final RelatorioService service;

	@GetMapping("/resumo")
	public ResumoRelatorio resumo(
			@RequestParam(required = false) Instant inicio,
			@RequestParam(required = false) Instant fim) {
		if (inicio == null) inicio = Instant.now().minus(30, ChronoUnit.DAYS);
		if (fim == null) fim = Instant.now().plus(1, ChronoUnit.DAYS);
		return service.buscarResumo(inicio, fim);
	}

	@GetMapping("/ranking/mais-vendidos")
	public List<RankingProduto> rankingMaisVendidos(
			@RequestParam(required = false) Instant inicio,
			@RequestParam(required = false) Instant fim,
			@RequestParam(defaultValue = "10") int limite) {
		if (inicio == null) inicio = Instant.now().minus(30, ChronoUnit.DAYS);
		if (fim == null) fim = Instant.now().plus(1, ChronoUnit.DAYS);
		return service.ranking(inicio, fim, "quantidade", limite);
	}

	@GetMapping("/ranking/mais-lucrativos")
	public List<RankingProduto> rankingMaisLucrativos(
			@RequestParam(required = false) Instant inicio,
			@RequestParam(required = false) Instant fim,
			@RequestParam(defaultValue = "10") int limite) {
		if (inicio == null) inicio = Instant.now().minus(30, ChronoUnit.DAYS);
		if (fim == null) fim = Instant.now().plus(1, ChronoUnit.DAYS);
		return service.ranking(inicio, fim, "lucro", limite);
	}
}
