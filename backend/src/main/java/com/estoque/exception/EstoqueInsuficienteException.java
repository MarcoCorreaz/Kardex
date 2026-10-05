package com.estoque.exception;

public class EstoqueInsuficienteException extends RegraNegocioException {
	public EstoqueInsuficienteException(String sku, long disponivel, long solicitado) {
		super("Estoque insuficiente para o SKU " + sku + ": disponivel " + disponivel
				+ ", solicitado " + solicitado + ".");
	}
}
