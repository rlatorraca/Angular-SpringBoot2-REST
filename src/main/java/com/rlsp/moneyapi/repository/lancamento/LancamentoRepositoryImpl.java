package com.rlsp.moneyapi.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.rlsp.moneyapi.filter.LancamentoFilter;
import com.rlsp.moneyapi.model.Lancamento;
import com.rlsp.moneyapi.model.Lancamento_;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager; // Para poder trabalhar com CONSULTAS
	
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder(); //Construi as CRITERIAS
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class); // Cria uma CRITERIA QUERY
		Root<Lancamento> root = criteria.from(Lancamento.class); // Usado nos FILTROS para pegar os atributos necessarios
		
		//Filtros(restricoes) da Criteria
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		//Query para EXECUCAO da CRITERIA
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageable);
		
		//return query.getResultList();
		return new PageImpl<>(query.getResultList(), pageable, totalElementos(lancamentoFilter));
	}




	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {
		/**
		 * Cria-se um LISTA, pois os predicates sao opcionais(logo variavel) e depois 
		 * entao transforma-se em ARRAY para retornaro ARRAY de predicates
		 */
		List<Predicate> predicates = new ArrayList<>();
		
		//Verifica ANTES se o campo nao esta vazio.
		if(!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {
			// SQL = where descricao like '%xxxxxx%
			predicates
				.add(builder
					.like(builder
							.lower(root.get(Lancamento_.descricao)), 
									"%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
		}
		
		if(!StringUtils.isEmpty(lancamentoFilter.getDataVencimentoDe())) {
			predicates
				.add(builder
					.greaterThanOrEqualTo(
							root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoDe()));	
		}
		
		if(!StringUtils.isEmpty(lancamentoFilter.getDataVencimentoAte())) {
			predicates
				.add(builder
					.lessThanOrEqualTo(
							root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoAte()));
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}

	/** DADOS apra paginacao 
	 */
	private void adicionarRestricoesDePaginacao(TypedQuery<Lancamento> query, Pageable pageable) {
		Integer paginaAtual = pageable.getPageNumber();
		Integer totalRegistroPorCadaPagina = pageable.getPageSize();
		Integer primeiroRegistroDeCadaPagina = paginaAtual * totalRegistroPorCadaPagina;
		
		query.setFirstResult(primeiroRegistroDeCadaPagina);
		query.setMaxResults(totalRegistroPorCadaPagina);	
		
	}
	
	/**
	 * Criteria Builder para Contar o Numero de REGISTROS / ELEMENTOS	 * 
	 */
	private Long totalElementos(LancamentoFilter lancamentoFilter) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder(); // Cria o builder para criteria
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class); // Mostra o tipo de retorna da CRITERIA
		Root<Lancamento> root = criteria.from(Lancamento.class); // Usa a classe Lancamento para a pesquisa
		
		//Filtros
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root)); // Faz a contagem do Numero de Elementos dentro de Lancamento "count *"
		
		
		return manager.createQuery(criteria).getSingleResult(); // A contagem ira retornar apenas 1 resultado
	}
}