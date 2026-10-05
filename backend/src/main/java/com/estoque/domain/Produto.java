package com.estoque.domain;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "produto")
@Getter
@Setter
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@org.hibernate.annotations.TenantId
	@Column(name = "loja_id", nullable = false, updatable = false)
	private Long lojaId;

	@Column(nullable = false, length = 150)
	private String nome;

	@Column(nullable = false, length = 60)
	private String sku;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;

	/** Calculado automaticamente a cada entrada. Nunca editado pela API de cadastro. */
	@Column(name = "custo_medio", nullable = false, precision = 12, scale = 4)
	private BigDecimal custoMedio = BigDecimal.ZERO.setScale(4);

	@Column(name = "preco_venda", nullable = false, precision = 12, scale = 2)
	private BigDecimal precoVenda;

	@Column(name = "estoque_minimo", nullable = false)
	private int estoqueMinimo;

	@Column(nullable = false)
	private boolean ativo = true;

	@Version
	private long version;

	@Column(name = "criado_em", nullable = false, updatable = false)
	private Instant criadoEm;

	@Column(name = "atualizado_em", nullable = false)
	private Instant atualizadoEm;

	@PrePersist
	void aoCriar() {
		criadoEm = Instant.now();
		atualizadoEm = criadoEm;
	}

	@PreUpdate
	void aoAtualizar() {
		atualizadoEm = Instant.now();
	}
}
