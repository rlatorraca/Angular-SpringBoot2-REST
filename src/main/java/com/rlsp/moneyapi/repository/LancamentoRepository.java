package com.rlsp.moneyapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rlsp.moneyapi.model.Lancamento;
import com.rlsp.moneyapi.model.Pessoa;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

	

}
