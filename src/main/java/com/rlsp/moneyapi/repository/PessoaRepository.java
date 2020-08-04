package com.rlsp.moneyapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rlsp.moneyapi.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>{

	public Page<Pessoa> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
	public Page<Pessoa> findAll(Pageable pageable);
}
