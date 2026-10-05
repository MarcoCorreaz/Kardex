package com.estoque.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estoque.domain.Loja;
import com.estoque.domain.Usuario;
import com.estoque.domain.Usuario.Papel;
import com.estoque.dto.AuthDtos.LojaRequest;
import com.estoque.dto.AuthDtos.LojaResponse;
import com.estoque.dto.AuthDtos.UsuarioRequest;
import com.estoque.dto.AuthDtos.UsuarioResponse;
import com.estoque.exception.RecursoNaoEncontradoException;
import com.estoque.exception.RegraNegocioException;
import com.estoque.repository.LojaRepository;
import com.estoque.repository.UsuarioRepository;
import com.estoque.security.TenantContext;

import lombok.RequiredArgsConstructor;

/** Dados da loja logada e gestao da equipe (somente DONO escreve). */
@Service
@RequiredArgsConstructor
public class LojaService {

	private final LojaRepository lojaRepository;
	private final UsuarioRepository usuarioRepository;
	private final AuthService authService;

	@Transactional(readOnly = true)
	public LojaResponse minhaLoja() {
		return LojaResponse.from(loja());
	}

	@Transactional
	public LojaResponse atualizar(LojaRequest r) {
		Loja l = loja();
		l.setNome(r.nome().trim());
		l.setNicho(r.nicho().trim().toUpperCase());
		l.setDocumento(r.documento());
		l.setTelefone(r.telefone());
		l.setCidade(r.cidade());
		l.setUf(r.uf() != null && !r.uf().isBlank() ? r.uf().toUpperCase() : null);
		return LojaResponse.from(lojaRepository.save(l));
	}

	@Transactional(readOnly = true)
	public List<UsuarioResponse> equipe() {
		return usuarioRepository.findByLojaIdOrderByNomeAsc(TenantContext.lojaId()).stream()
				.map(UsuarioResponse::from).toList();
	}

	@Transactional
	public UsuarioResponse adicionarMembro(UsuarioRequest r) {
		if (usuarioRepository.existsByEmailIgnoreCase(r.email())) {
			throw new RegraNegocioException("E-mail ja cadastrado.");
		}
		Usuario u = authService.novoUsuario(loja(), r.nome(), r.email(), r.senha(), r.papel());
		return UsuarioResponse.from(u);
	}

	@Transactional
	public UsuarioResponse alterarAtivo(Long id, boolean ativo) {
		Usuario u = usuarioRepository.findByIdAndLojaId(id, TenantContext.lojaId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuario", id));
		if (u.getId().equals(TenantContext.usuarioId())) {
			throw new RegraNegocioException("Voce nao pode desativar a si mesmo.");
		}
		if (u.getPapel() == Papel.DONO && !ativo) {
			throw new RegraNegocioException("O dono da loja nao pode ser desativado.");
		}
		u.setAtivo(ativo);
		return UsuarioResponse.from(usuarioRepository.save(u));
	}

	private Loja loja() {
		return lojaRepository.findById(TenantContext.lojaId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Loja", TenantContext.lojaId()));
	}
}
