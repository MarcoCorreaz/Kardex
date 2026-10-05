package com.estoque.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CustoMedioServiceTest {

	private final CustoMedioService svc = new CustoMedioService();

	@Test
	void primeiraEntradaDefineOCusto() {
		BigDecimal r = svc.novoCustoMedio(0, BigDecimal.ZERO, 10, new BigDecimal("5.00"));
		assertEquals(new BigDecimal("5.0000"), r);
	}

	@Test
	void entradaPonderaPeloSaldo() {
		// 10 un a 5,00 + 30 un a 9,00 => (50 + 270) / 40 = 8,00
		BigDecimal r = svc.novoCustoMedio(10, new BigDecimal("5.0000"), 30, new BigDecimal("9.00"));
		assertEquals(new BigDecimal("8.0000"), r);
	}

	@Test
	void arredondaEmQuatroCasas() {
		// (1*10 + 2*10) / 3 = 10 ; (1*1 + 2*2)/3 = 1,6667
		BigDecimal r = svc.novoCustoMedio(1, new BigDecimal("1.0000"), 2, new BigDecimal("2.00"));
		assertEquals(new BigDecimal("1.6667"), r);
	}

	@Test
	void rejeitaQuantidadeInvalida() {
		assertThrows(IllegalArgumentException.class,
				() -> svc.novoCustoMedio(5, BigDecimal.ONE, 0, BigDecimal.ONE));
	}
}
