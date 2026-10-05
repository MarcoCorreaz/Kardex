package com.estoque.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Loja = tenant. Todos os dados de negocio pertencem a uma loja. */
@Entity
@Table(name = "loja")
@Getter
@Setter
public class Loja {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String nome;

	@Column(nullable = false, length = 60)
	private String nicho = "OUTROS";

	@Column(length = 20)
	private String documento;

	@Column(length = 20)
	private String telefone;

	@Column(length = 80)
	private String cidade;

	@org.hibernate.annotations.JdbcTypeCode(java.sql.Types.CHAR)
	@Column(length = 2)
	private String uf;

	@Column(nullable = false)
	private boolean ativo = true;

	@Column(name = "criado_em", nullable = false, updatable = false)
	private Instant criadoEm;

	@PrePersist
	void aoCriar() {
		criadoEm = Instant.now();
	}
}
