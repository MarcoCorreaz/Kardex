package com.estoque.domain;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Item de venda: preco, custo medio e desconto ficam CONGELADOS no instante da venda. */
@Entity
@Table(name = "item_venda")
@Getter
@Setter
public class ItemVenda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "venda_id")
	private Venda venda;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "produto_id")
	private Produto produto;

	@Column(nullable = false)
	private int quantidade;

	@Column(name = "preco_venda_praticado", nullable = false, precision = 12, scale = 2)
	private BigDecimal precoVendaPraticado;

	@Column(name = "custo_medio_snapshot", nullable = false, precision = 12, scale = 4)
	private BigDecimal custoMedioSnapshot;

	/** Desconto total da linha em R$. */
	@Column(name = "desconto_item", nullable = false, precision = 12, scale = 2)
	private BigDecimal descontoItem;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_desconto", nullable = false, length = 10)
	private TipoDesconto tipoDesconto = TipoDesconto.VALOR;

	@Column(name = "valor_desconto_informado", nullable = false, precision = 12, scale = 2)
	private BigDecimal valorDescontoInformado = BigDecimal.ZERO;

	public enum TipoDesconto {
		VALOR, PERCENTUAL
	}
}
