package com.rlsp.moneyapi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rlsp.moneyapi.model.Lancamento;
import com.rlsp.moneyapi.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery{
	
	List<Lancamento> findByDataVencimentoLessThanEqualAndDataPagamentoNull(LocalDate data);

}
