package com.estoque.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import com.estoque.exception.RegraNegocioException;

/** Acesso ao usuario/loja autenticados na requisicao atual. */
public final class TenantContext {

	private TenantContext() {
	}

	private static Jwt jwt() {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		return a != null && a.getPrincipal() instanceof Jwt j ? j : null;
	}

	public static Long lojaIdOuNulo() {
		Jwt j = jwt();
		if (j == null) {
			return null;
		}
		Number n = j.getClaim("lojaId");
		return n != null ? n.longValue() : null;
	}

	public static Long lojaId() {
		Long id = lojaIdOuNulo();
		if (id == null) {
			throw new RegraNegocioException("Sessao invalida.");
		}
		return id;
	}

	public static Long usuarioId() {
		Jwt j = jwt();
		if (j == null) {
			throw new RegraNegocioException("Sessao invalida.");
		}
		return Long.valueOf(j.getSubject());
	}
}
