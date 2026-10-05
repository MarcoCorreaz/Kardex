package com.estoque.dto;

import com.estoque.domain.Loja;
import com.estoque.domain.Usuario;
import com.estoque.domain.Usuario.Papel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** DTOs de autenticacao, loja e equipe. */
public final class AuthDtos {

	private AuthDtos() {
	}

	public record RegistroRequest(
			@NotBlank @Size(max = 150) String nomeLoja,
			@NotBlank @Size(max = 60) String nicho,
			@Size(max = 20) String documento,
			@Size(max = 20) String telefone,
			@Size(max = 80) String cidade,
			@Pattern(regexp = "^[A-Za-z]{2}$", message = "UF deve ter 2 letras") String uf,
			@NotBlank @Size(max = 120) String nomeUsuario,
			@NotBlank @Email @Size(max = 160) String email,
			@NotBlank @Size(min = 8, max = 72, message = "A senha deve ter de 8 a 72 caracteres") String senha) {
	}

	public record LoginRequest(@NotBlank @Email String email, @NotBlank String senha) {
	}

	public record LojaRequest(
			@NotBlank @Size(max = 150) String nome,
			@NotBlank @Size(max = 60) String nicho,
			@Size(max = 20) String documento,
			@Size(max = 20) String telefone,
			@Size(max = 80) String cidade,
			@Pattern(regexp = "^([A-Za-z]{2})?$", message = "UF deve ter 2 letras") String uf) {
	}

	public record UsuarioRequest(
			@NotBlank @Size(max = 120) String nome,
			@NotBlank @Email @Size(max = 160) String email,
			@NotBlank @Size(min = 8, max = 72, message = "A senha deve ter de 8 a 72 caracteres") String senha,
			@NotNull Papel papel) {
	}

	public record LojaResponse(Long id, String nome, String nicho, String documento, String telefone,
			String cidade, String uf) {
		public static LojaResponse from(Loja l) {
			return new LojaResponse(l.getId(), l.getNome(), l.getNicho(), l.getDocumento(), l.getTelefone(),
					l.getCidade(), l.getUf());
		}
	}

	public record UsuarioResponse(Long id, String nome, String email, Papel papel, boolean ativo) {
		public static UsuarioResponse from(Usuario u) {
			return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPapel(), u.isAtivo());
		}
	}

	public record SessaoResponse(String token, UsuarioResponse usuario, LojaResponse loja) {
	}
}
