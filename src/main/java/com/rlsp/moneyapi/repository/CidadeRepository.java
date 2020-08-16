package com.rlsp.moneyapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rlsp.moneyapi.model.Cidade;



public interface CidadeRepository extends JpaRepository<Cidade, Long> {
	
	List<Cidade> findByEstadoCodigo(Long estadoCodigo);

}