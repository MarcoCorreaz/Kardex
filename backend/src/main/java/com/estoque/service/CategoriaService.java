package com.estoque.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estoque.domain.Categoria;
import com.estoque.dto.CategoriaRequest;
import com.estoque.dto.CategoriaResponse;
import com.estoque.exception.RecursoNaoEncontradoException;
import com.estoque.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

	private final CategoriaRepository repository;

	@Transactional(readOnly = true)
	public Page<CategoriaResponse> listar(Pageable pageable) {
		return repository.findAll(pageable).map(CategoriaResponse::from);
	}

	@Transactional(readOnly = true)
	public CategoriaResponse buscarPorId(Long id) {
		return CategoriaResponse.from(encontrar(id));
	}

	@Transactional
	public CategoriaResponse criar(CategoriaRequest request) {
		Categoria c = new Categoria();
		c.setNome(request.nome());
		return CategoriaResponse.from(repository.save(c));
	}

	@Transactional
	public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
		Categoria c = encontrar(id);
		c.setNome(request.nome());
		return CategoriaResponse.from(repository.save(c));
	}

	@Transactional
	public void inativar(Long id) {
		Categoria c = encontrar(id);
		c.setAtivo(false);
		repository.save(c);
	}

	public Categoria encontrar(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", id));
	}
}
