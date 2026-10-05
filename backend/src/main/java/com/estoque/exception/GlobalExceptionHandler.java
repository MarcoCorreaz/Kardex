package com.estoque.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ProblemDetail handleNotFound(RecursoNaoEncontradoException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		pd.setTitle("Recurso não encontrado");
		return pd;
	}

	@ExceptionHandler(RegraNegocioException.class)
	public ProblemDetail handleRegraNegocio(RegraNegocioException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
		pd.setTitle("Erro de regra de negócio");
		return pd;
	}

	@ExceptionHandler(NaoAutenticadoException.class)
	public ProblemDetail handleNaoAutenticado(NaoAutenticadoException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
		pd.setTitle("Não autenticado");
		return pd;
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ProblemDetail handleAcessoNegado(AccessDeniedException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
				"Você não tem permissão para esta ação.");
		pd.setTitle("Acesso negado");
		return pd;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
		String errors = ex.getBindingResult().getFieldErrors().stream()
				.map(f -> f.getField() + ": " + f.getDefaultMessage())
				.collect(Collectors.joining(", "));

		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
		pd.setTitle("Dados inválidos");
		return pd;
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleGeneric(Exception ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno.");
		pd.setTitle("Erro interno");
		return pd;
	}
}
