package com.estoque.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "venda")
@Getter
@Setter
public class Venda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@org.hibernate.annotations.TenantId
	@Column(name = "loja_id", nullable = false, updatable = false)
	private Long lojaId;

	@Column(name = "criado_em", nullable = false, updatable = false)
	private Instant criadoEm;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal subtotal;

	@Column(name = "desconto_total", nullable = false, precision = 12, scale = 2)
	private BigDecimal descontoTotal;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal total;

	@Column(nullable = false, length = 20)
	private String status = "CONCLUIDA";

	@OneToMany(mappedBy = "venda", cascade = CascadeType.PERSIST)
	private List<ItemVenda> itens = new ArrayList<>();

	@PrePersist
	void aoCriar() {
		criadoEm = Instant.now();
	}

	public void adicionarItem(ItemVenda item) {
		item.setVenda(this);
		itens.add(item);
	}
}
