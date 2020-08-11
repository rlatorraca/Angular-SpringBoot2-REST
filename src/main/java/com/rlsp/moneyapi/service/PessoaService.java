package com.rlsp.moneyapi.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.rlsp.moneyapi.model.Lancamento;
import com.rlsp.moneyapi.model.Pessoa;
import com.rlsp.moneyapi.repository.PessoaRepository;

@Service
public class PessoaService {
	
	@Autowired
	private PessoaRepository pessoaRepository;

	public Pessoa atualizar(Long codigo, Pessoa pessoa) {

	  Pessoa pessoaSalva = buscarPessoaSalvaPeloCodigo(codigo);
	  
	  pessoaSalva.getContatos().clear();
	  pessoaSalva.getContatos().addAll(pessoa.getContatos());
	  pessoaSalva.getContatos().forEach(c -> c.setPessoa(pessoaSalva));
		
	  
	  
	  BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo", "contatos");

	  return this.pessoaRepository.save(pessoaSalva);
	}


	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		 
		Pessoa pessoaSalva = buscarPessoaSalvaPeloCodigo(codigo);
		
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva);
		
	}
	
	/*
	private Pessoa buscarPessoaSalvaPeloCodigo(Long codigo) {
		Pessoa pessoaSalva = this.pessoaRepository.findById(codigo)
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
		return pessoaSalva;
	} */
	
	
	private Pessoa buscarPessoaSalvaPeloCodigo(Long codigo) {
		Optional<Pessoa> pessoaSalva = pessoaRepository.findById(codigo);
		
		/*
		if (lancamentoSalvo == null ) {
			throw new IllegalArgumentException();
		} */
		
		if (!pessoaSalva.isPresent() ) {
			throw new IllegalArgumentException();
		}
		
		return pessoaSalva.get();
	}


	public Pessoa salvar(@Valid Pessoa pessoa) {
		pessoa.getContatos().forEach(contato -> contato.setPessoa(pessoa));
		return pessoaRepository.save(pessoa);		
	}
	
	
}
