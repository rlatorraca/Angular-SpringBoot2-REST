package com.rlsp.moneyapi.repository.lancamento;

import java.time.LocalDate;
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

import com.rlsp.moneyapi.dto.LancamentosEstatisticaCategoria;
import com.rlsp.moneyapi.dto.LancamentosEstatisticaPessoa;
import com.rlsp.moneyapi.dto.LancamentosEstatisticaPorDia;
import com.rlsp.moneyapi.filter.LancamentoFilter;
import com.rlsp.moneyapi.model.Categoria_;
import com.rlsp.moneyapi.model.Lancamento;
import com.rlsp.moneyapi.model.Lancamento_;
import com.rlsp.moneyapi.model.Pessoa_;
import com.rlsp.moneyapi.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager; // Para poder trabalhar com CONSULTAS
	
	@Override
	public List<LancamentosEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		
		CriteriaQuery<LancamentosEstatisticaPessoa> criteriaQuery = criteriaBuilder.
				createQuery(LancamentosEstatisticaPessoa.class);
		
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);
		
		criteriaQuery.select(criteriaBuilder.construct(LancamentosEstatisticaPessoa.class, 
				root.get(Lancamento_.tipo),
				root.get(Lancamento_.pessoa),
				criteriaBuilder.sum(root.get(Lancamento_.valor))));
		
		criteriaQuery.where(
				criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						inicio),
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						fim));
		
		criteriaQuery.groupBy(root.get(Lancamento_.tipo), 
				root.get(Lancamento_.pessoa));
		
		TypedQuery<LancamentosEstatisticaPessoa> typedQuery = manager
				.createQuery(criteriaQuery);
		
		return typedQuery.getResultList();
	}
	
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
	
	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable){
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class); // Retorna uma Criteria Builder para "ResumoLancamento"
		Root<Lancamento> root = criteria.from(Lancamento.class); // faz a a consulta na Entidade Lancamento
		
		criteria.select(builder.construct(ResumoLancamento.class
				,root.get(Lancamento_.codigo)
				,root.get(Lancamento_.descricao)
				,root.get(Lancamento_.dataVencimento)
				,root.get(Lancamento_.dataPagamento)
				,root.get(Lancamento_.valor)
				,root.get(Lancamento_.tipo)
				,root.get(Lancamento_.categoria).get(Categoria_.nome)
				,root.get(Lancamento_.pessoa).get(Pessoa_.nome))
				);
		
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		
		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageable);
		
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
	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
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

	@Override
	public List<LancamentosEstatisticaCategoria> porCategoria(LocalDate mesReferencia) {
		
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		
		CriteriaQuery<LancamentosEstatisticaCategoria> criteriaQuery = criteriaBuilder.createQuery(LancamentosEstatisticaCategoria.class);
		
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);
		
		criteriaQuery.select(criteriaBuilder.construct(LancamentosEstatisticaCategoria.class, 
				root.get(Lancamento_.categoria),
				criteriaBuilder.sum(root.get(Lancamento_.valor))));
		
		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
		
		criteriaQuery.where(
				criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						primeiroDia),
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						ultimoDia));
		
		criteriaQuery.groupBy(root.get(Lancamento_.categoria));
		
		TypedQuery<LancamentosEstatisticaCategoria> typedQuery = manager
				.createQuery(criteriaQuery);
		
		return typedQuery.getResultList();		
	}
	
	@Override
	public List<LancamentosEstatisticaPorDia> porDia(LocalDate mesReferencia) {
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		
		CriteriaQuery<LancamentosEstatisticaPorDia> criteriaQuery = criteriaBuilder.
				createQuery(LancamentosEstatisticaPorDia.class);
		
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);
		
		criteriaQuery.select(criteriaBuilder.construct(LancamentosEstatisticaPorDia.class, 
				root.get(Lancamento_.tipo),
				root.get(Lancamento_.dataVencimento),
				criteriaBuilder.sum(root.get(Lancamento_.valor))));
		
		LocalDate primeiroDia = mesReferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());
		
		criteriaQuery.where(
				criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						primeiroDia),
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), 
						ultimoDia));
		
		criteriaQuery.groupBy(root.get(Lancamento_.tipo), 
				root.get(Lancamento_.dataVencimento));
		
		TypedQuery<LancamentosEstatisticaPorDia> typedQuery = manager
				.createQuery(criteriaQuery);
		
		return typedQuery.getResultList();
	}
}
