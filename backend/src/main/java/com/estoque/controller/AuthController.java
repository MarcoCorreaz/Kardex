package com.estoque.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.estoque.dto.AuthDtos.LoginRequest;
import com.estoque.dto.AuthDtos.LojaRequest;
import com.estoque.dto.AuthDtos.LojaResponse;
import com.estoque.dto.AuthDtos.RegistroRequest;
import com.estoque.dto.AuthDtos.SessaoResponse;
import com.estoque.dto.AuthDtos.UsuarioRequest;
import com.estoque.dto.AuthDtos.UsuarioResponse;
import com.estoque.service.AuthService;
import com.estoque.service.LojaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final LojaService lojaService;

	@PostMapping("/auth/registrar")
	@ResponseStatus(HttpStatus.CREATED)
	public SessaoResponse registrar(@Valid @RequestBody RegistroRequest request) {
		return authService.registrar(request);
	}

	@PostMapping("/auth/login")
	public SessaoResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@GetMapping("/auth/me")
	public SessaoResponse me() {
		return authService.me();
	}

	@GetMapping("/loja")
	public LojaResponse loja() {
		return lojaService.minhaLoja();
	}

	@PutMapping("/loja")
	@PreAuthorize("hasRole('DONO')")
	public LojaResponse atualizarLoja(@Valid @RequestBody LojaRequest request) {
		return lojaService.atualizar(request);
	}

	@GetMapping("/equipe")
	@PreAuthorize("hasRole('DONO')")
	public List<UsuarioResponse> equipe() {
		return lojaService.equipe();
	}

	@PostMapping("/equipe")
	@PreAuthorize("hasRole('DONO')")
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResponse adicionarMembro(@Valid @RequestBody UsuarioRequest request) {
		return lojaService.adicionarMembro(request);
	}

	@PatchMapping("/equipe/{id}/ativo")
	@PreAuthorize("hasRole('DONO')")
	public UsuarioResponse alterarAtivo(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
		return lojaService.alterarAtivo(id, Boolean.TRUE.equals(body.get("ativo")));
	}
}
