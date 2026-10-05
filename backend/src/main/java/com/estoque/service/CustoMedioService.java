package com.estoque.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

/** Calculo puro (sem estado) do Custo Medio Ponderado. */
@Component
public class CustoMedioService {

	public static final int ESCALA = 4;

	/**
	 * novoCusto = (saldoAtual * custoAtual + qtdEntrada * custoEntrada) / (saldoAtual + qtdEntrada)
	 */
	public BigDecimal novoCustoMedio(long saldoAtual, BigDecimal custoAtual, int qtdEntrada, BigDecimal custoEntrada) {
		if (qtdEntrada <= 0) {
			throw new IllegalArgumentException("Quantidade de entrada deve ser positiva");
		}
		BigDecimal valorAtual = custoAtual.multiply(BigDecimal.valueOf(Math.max(saldoAtual, 0)));
		BigDecimal valorEntrada = custoEntrada.multiply(BigDecimal.valueOf(qtdEntrada));
		BigDecimal novoSaldo = BigDecimal.valueOf(Math.max(saldoAtual, 0) + qtdEntrada);
		return valorAtual.add(valorEntrada).divide(novoSaldo, ESCALA, RoundingMode.HALF_EVEN);
	}
}
