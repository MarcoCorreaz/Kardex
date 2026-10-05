package com.estoque.security;

import java.util.Map;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

/**
 * Entrega ao Hibernate o tenant (loja) da requisicao atual, lido do JWT.
 * Entidades com @TenantId passam a ser filtradas/gravadas automaticamente por loja.
 * Sem autenticacao (login/registro) devolve um tenant sentinela que nao existe.
 */
@Component
public class TenantResolver implements CurrentTenantIdentifierResolver<Long>, HibernatePropertiesCustomizer {

	public static final Long SEM_TENANT = 0L;

	@Override
	public Long resolveCurrentTenantIdentifier() {
		Long id = TenantContext.lojaIdOuNulo();
		return id != null ? id : SEM_TENANT;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return false;
	}

	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
	}
}
