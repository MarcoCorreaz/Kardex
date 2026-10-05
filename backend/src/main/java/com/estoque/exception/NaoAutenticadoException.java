package com.estoque.exception;

/** Credenciais invalidas / conta desativada (HTTP 401). */
public class NaoAutenticadoException extends RuntimeException {
	public NaoAutenticadoException(String message) {
		super(message);
	}
}
