package com.rlsp.moneyapi.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rlsp.moneyapi.dto.LancamentosEstatisticaCategoria;
import com.rlsp.moneyapi.dto.LancamentosEstatisticaPessoa;
import com.rlsp.moneyapi.dto.LancamentosEstatisticaPorDia;
import com.rlsp.moneyapi.filter.LancamentoFilter;
import com.rlsp.moneyapi.model.Lancamento;
import com.rlsp.moneyapi.repository.projection.ResumoLancamento;

public interface LancamentoRepositoryQuery {
	
	public List<LancamentosEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim);
	public List<LancamentosEstatisticaCategoria> porCategoria(LocalDate mesReferencia);
	public List<LancamentosEstatisticaPorDia> porDia(LocalDate mesReferencia);	

	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
	
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);
}
