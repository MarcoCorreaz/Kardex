package com.estoque.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
		@NotBlank(message = "Nome é obrigatório")
		String nome
) {}
