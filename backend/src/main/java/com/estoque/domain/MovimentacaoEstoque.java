package com.estoque.domain;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Linha do Kardex. Append-only (o banco bloqueia UPDATE/DELETE). */
@Entity
@Table(name = "movimentacao_estoque")
@Getter
@Setter
public class MovimentacaoEstoque {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@org.hibernate.annotations.TenantId
	@Column(name = "loja_id", nullable = false, updatable = false)
	private Long lojaId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "produto_id")
	private Produto produto;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private TipoMovimentacao tipo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private OrigemMovimentacao origem;

	/** Com sinal: entrada positiva, saida negativa, ajuste +/-. */
	@Column(nullable = false)
	private int quantidade;

	@Column(name = "custo_unitario", nullable = false, precision = 12, scale = 4)
	private BigDecimal custoUnitario;

	@Column(name = "custo_medio_apos", nullable = false, precision = 12, scale = 4)
	private BigDecimal custoMedioApos;

	@Column(name = "saldo_apos", nullable = false)
	private int saldoApos;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venda_id")
	private Venda venda;

	@Column(length = 255)
	private String motivo;

	@Column(name = "criado_em", nullable = false, updatable = false)
	private Instant criadoEm;

	@PrePersist
	void aoCriar() {
		criadoEm = Instant.now();
	}
}
