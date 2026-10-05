package com.estoque.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record VendaRequest(
		@NotEmpty(message = "A venda deve ter pelo menos um item")
		@Valid
		List<ItemVendaRequest> itens
) {}
