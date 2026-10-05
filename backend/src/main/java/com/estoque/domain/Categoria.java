package com.estoque.domain;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categoria")
@Getter
@Setter
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@org.hibernate.annotations.TenantId
	@Column(name = "loja_id", nullable = false, updatable = false)
	private Long lojaId;

	@Column(nullable = false, length = 100)
	private String nome;

	@Column(nullable = false)
	private boolean ativo = true;

	@Column(name = "criado_em", nullable = false, updatable = false)
	private Instant criadoEm;

	@PrePersist
	void aoCriar() {
		criadoEm = Instant.now();
	}
}
