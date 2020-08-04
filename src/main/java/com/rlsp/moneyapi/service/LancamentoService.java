package com.rlsp.moneyapi.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rlsp.moneyapi.model.Lancamento;
import com.rlsp.moneyapi.model.Pessoa;
import com.rlsp.moneyapi.repository.LancamentoRepository;
import com.rlsp.moneyapi.repository.PessoaRepository;
import com.rlsp.moneyapi.service.exception.PessoaInexistenteOuInativaException;


@Service
public class LancamentoService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	public Lancamento salvar(Lancamento lancamento) {
		
		/**
		 * Verifica se a PESSOA : EXISTE e esta ATIVA
		 */
		Optional<Pessoa> pessoaSalva = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		if(!pessoaSalva.isPresent() || !pessoaSalva.get().getAtivo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		return lancamentoRepository.save(lancamento);
		
		
	}
	
	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		
		Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
		
		if(!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}
		
		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo"); // Faz a copia do lancamento ==> lancamentoSalvo, EXCETO "codigo"

		return lancamentoRepository.save(lancamentoSalvo);
	}

	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = null;
		
		if(lancamento.getPessoa().getCodigo() != null) {
			pessoa = pessoaRepository.getOne(lancamento.getPessoa().getCodigo());		
		}
		
		if (pessoa == null  || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		
	}

	private Lancamento buscarLancamentoExistente(Long codigo) {
		
		//Lancamento lancamentoSalvo = lancamentoRepository.findById(codigo).orElse(null);
		Optional<Lancamento> lancamentoSalvo = lancamentoRepository.findById(codigo);
		
		/*
		if (lancamentoSalvo == null ) {
			throw new IllegalArgumentException();
		} */
		
		if (!lancamentoSalvo.isPresent() ) {
			throw new IllegalArgumentException();
		}
		
		return lancamentoSalvo.get();
	}

}
