package com.estoque.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estoque.dto.RankingProduto;
import com.estoque.dto.ResumoRelatorio;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RelatorioService {

	private final EntityManager em;

	@Transactional(readOnly = true)
	public ResumoRelatorio buscarResumo(Instant inicio, Instant fim) {
		String sql = """
			SELECT 
				COALESCE(SUM(i.quantidade * i.preco_venda_praticado), 0) as faturamento_bruto,
				COALESCE(SUM( (i.quantidade * (i.preco_venda_praticado - i.custo_medio_snapshot)) - i.desconto_item ), 0) as lucro_liquido
			FROM item_venda i
			JOIN venda v ON v.id = i.venda_id
			WHERE v.status = 'CONCLUIDA' AND v.loja_id = :lojaId 
			  AND v.criado_em >= :inicio 
			  AND v.criado_em < :fim
		""";

		Query query = em.createNativeQuery(sql);
		query.setParameter("lojaId", com.estoque.security.TenantContext.lojaId());
		query.setParameter("inicio", inicio);
		query.setParameter("fim", fim);

		Object[] result = (Object[]) query.getSingleResult();
		BigDecimal faturamentoBruto = (BigDecimal) result[0];
		BigDecimal lucroLiquido = (BigDecimal) result[1];
		BigDecimal margemPercentual = BigDecimal.ZERO;

		if (faturamentoBruto.compareTo(BigDecimal.ZERO) > 0) {
			margemPercentual = lucroLiquido.divide(faturamentoBruto, 4, RoundingMode.HALF_EVEN)
					.multiply(BigDecimal.valueOf(100));
		}

		return new ResumoRelatorio(faturamentoBruto, lucroLiquido, margemPercentual);
	}

	@Transactional(readOnly = true)
	public List<RankingProduto> ranking(Instant inicio, Instant fim, String orderBy, int limite) {
		String sql = """
			SELECT 
				p.id as produto_id,
				p.nome as produto_nome,
				p.sku as sku,
				SUM(i.quantidade) as quantidade_vendida,
				SUM(i.quantidade * i.preco_venda_praticado) as faturamento,
				SUM( (i.quantidade * (i.preco_venda_praticado - i.custo_medio_snapshot)) - i.desconto_item ) as lucro
			FROM item_venda i
			JOIN venda v ON v.id = i.venda_id
			JOIN produto p ON p.id = i.produto_id
			WHERE v.status = 'CONCLUIDA' AND v.loja_id = :lojaId 
			  AND v.criado_em >= :inicio 
			  AND v.criado_em < :fim
			GROUP BY p.id, p.nome, p.sku
		""";

		if ("lucro".equalsIgnoreCase(orderBy)) {
			sql += " ORDER BY lucro DESC";
		} else {
			sql += " ORDER BY quantidade_vendida DESC";
		}
		
		sql += " LIMIT :limite";

		Query query = em.createNativeQuery(sql);
		query.setParameter("lojaId", com.estoque.security.TenantContext.lojaId());
		query.setParameter("inicio", inicio);
		query.setParameter("fim", fim);
		query.setParameter("limite", limite);

		@SuppressWarnings("unchecked")
		List<Object[]> resultados = query.getResultList();
		List<RankingProduto> lista = new ArrayList<>();

		for (Object[] row : resultados) {
			Long pId = ((Number) row[0]).longValue();
			String pNome = (String) row[1];
			String sku = (String) row[2];
			int qtd = ((Number) row[3]).intValue();
			BigDecimal faturamento = (BigDecimal) row[4];
			BigDecimal lucro = (BigDecimal) row[5];
			
			BigDecimal margem = BigDecimal.ZERO;
			if (faturamento.compareTo(BigDecimal.ZERO) > 0) {
				margem = lucro.divide(faturamento, 4, RoundingMode.HALF_EVEN)
						.multiply(BigDecimal.valueOf(100));
			}
			
			lista.add(new RankingProduto(pId, pNome, sku, qtd, faturamento, lucro, margem));
		}

		return lista;
	}
}
