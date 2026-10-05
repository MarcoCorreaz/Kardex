package com.estoque.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estoque.domain.Loja;
import com.estoque.domain.Usuario;
import com.estoque.domain.Usuario.Papel;
import com.estoque.dto.AuthDtos.LoginRequest;
import com.estoque.dto.AuthDtos.LojaResponse;
import com.estoque.dto.AuthDtos.RegistroRequest;
import com.estoque.dto.AuthDtos.SessaoResponse;
import com.estoque.dto.AuthDtos.UsuarioResponse;
import com.estoque.exception.NaoAutenticadoException;
import com.estoque.exception.RegraNegocioException;
import com.estoque.repository.LojaRepository;
import com.estoque.repository.UsuarioRepository;
import com.estoque.security.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	/** Categorias iniciais sugeridas por nicho. */
	private static final Map<String, List<String>> CATEGORIAS_POR_NICHO = Map.of(
			"MODA", List.of("Camisetas", "Calcas", "Calcados", "Acessorios"),
			"ALIMENTOS", List.of("Bebidas", "Mercearia", "Padaria", "Frios"),
			"ELETRONICOS", List.of("Celulares", "Acessorios", "Audio", "Informatica"),
			"BELEZA", List.of("Cabelo", "Pele", "Maquiagem", "Perfumaria"),
			"CASA", List.of("Decoracao", "Cozinha", "Cama e Banho", "Utilidades"),
			"PETSHOP", List.of("Racao", "Higiene", "Brinquedos", "Acessorios"));

	private final LojaRepository lojaRepository;
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtEncoder jwtEncoder;
	private final JdbcTemplate jdbc;

	@Value("${app.jwt.horas-validade:12}")
	private long horasValidade;

	@Transactional
	public SessaoResponse registrar(RegistroRequest r) {
		if (usuarioRepository.existsByEmailIgnoreCase(r.email())) {
			throw new RegraNegocioException("E-mail ja cadastrado.");
		}

		Loja loja = new Loja();
		loja.setNome(r.nomeLoja().trim());
		loja.setNicho(r.nicho().trim().toUpperCase());
		loja.setDocumento(r.documento());
		loja.setTelefone(r.telefone());
		loja.setCidade(r.cidade());
		loja.setUf(r.uf() != null && !r.uf().isBlank() ? r.uf().toUpperCase() : null);
		loja = lojaRepository.save(loja);

		Usuario dono = novoUsuario(loja, r.nomeUsuario(), r.email(), r.senha(), Papel.DONO);

		// Categorias iniciais por nicho (JDBC: o Hibernate ainda nao tem tenant nesta requisicao).
		for (String nome : CATEGORIAS_POR_NICHO.getOrDefault(loja.getNicho(), List.of("Geral"))) {
			jdbc.update("INSERT INTO categoria (loja_id, nome) VALUES (?, ?)", loja.getId(), nome);
		}

		return sessao(dono);
	}

	@Transactional(readOnly = true)
	public SessaoResponse login(LoginRequest r) {
		Usuario u = usuarioRepository.findByEmailIgnoreCase(r.email().trim())
				.orElseThrow(() -> new NaoAutenticadoException("E-mail ou senha invalidos."));
		if (!passwordEncoder.matches(r.senha(), u.getSenhaHash())) {
			throw new NaoAutenticadoException("E-mail ou senha invalidos.");
		}
		if (!u.isAtivo() || !u.getLoja().isAtivo()) {
			throw new NaoAutenticadoException("Conta desativada. Fale com o dono da loja.");
		}
		return sessao(u);
	}

	@Transactional(readOnly = true)
	public SessaoResponse me() {
		Usuario u = usuarioRepository.findById(TenantContext.usuarioId())
				.orElseThrow(() -> new NaoAutenticadoException("Sessao invalida."));
		if (!u.isAtivo()) {
			throw new NaoAutenticadoException("Conta desativada.");
		}
		return sessao(u);
	}

	public Usuario novoUsuario(Loja loja, String nome, String email, String senha, Papel papel) {
		Usuario u = new Usuario();
		u.setLoja(loja);
		u.setNome(nome.trim());
		u.setEmail(email.trim().toLowerCase());
		u.setSenhaHash(passwordEncoder.encode(senha));
		u.setPapel(papel);
		return usuarioRepository.save(u);
	}

	private SessaoResponse sessao(Usuario u) {
		Instant agora = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.subject(String.valueOf(u.getId()))
				.issuedAt(agora)
				.expiresAt(agora.plus(Duration.ofHours(horasValidade)))
				.claim("lojaId", u.getLoja().getId())
				.claim("papel", u.getPapel().name())
				.build();
		String token = jwtEncoder
				.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
				.getTokenValue();
		return new SessaoResponse(token, UsuarioResponse.from(u), LojaResponse.from(u.getLoja()));
	}
}
