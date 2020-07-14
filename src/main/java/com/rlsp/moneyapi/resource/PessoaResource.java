package com.rlsp.moneyapi.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rlsp.moneyapi.event.RecursoCriadoEvent;
import com.rlsp.moneyapi.model.Pessoa;
import com.rlsp.moneyapi.repository.PessoaRepository;
import com.rlsp.moneyapi.service.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private PessoaService pessoaService;
	
	/**
	 * Injeta o Evento de Aplicacao (listener) para entao enviar para "RecursoCriadoEvent" e CRIAR a URI para LOCATION
	 */
	@Autowired
	private ApplicationEventPublisher publisher;
	
	
	@GetMapping//==> mapeamento do GET
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA')")
	public List<Pessoa> listar(){
		List<Pessoa> categorias = pessoaRepository.findAll();
		return categorias;
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA')")
	public ResponseEntity<Pessoa> salvar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response){
		
		Pessoa pessoaSalva = pessoaRepository.save(pessoa);
		
		// Construindo a LOCATION, chamando o "RecursoCriadoEvent"
		// this ==> sera a funcao/metodo que chamou o EVENTO
		publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getCodigo()));
	
	
		// Devolvendo a categoria criada ou salva
		//return ResponseEntity.created(uri).body(categoriaSalva);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
	}
	
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') ")
	public ResponseEntity<Pessoa> buscarPessoaPeloCodigo(@PathVariable Long codigo){{
		
	}
		
		// Utilizando MAP
		return this.pessoaRepository.findById(codigo)
					   .map(categoria -> ResponseEntity.ok(categoria))
					   .orElse(ResponseEntity.notFound().build());
				
		//Utilizando isPresent()
		/* Optional<Pessoa> pessoa = this.pessoaRepository.findById(codigo);
		    return pessoaRepository.isPresent() ? ResponseEntity.ok(pessoa.get()) : ResponseEntity.notFound().build();
		*/			
		
	}
	
	
	/**
	 * Metodo para DELETAR
	 */
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // retorna o codigo 204 (Deu certo + nao tem nada para retornar
	@PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA')")
	public void remover(@PathVariable Long codigo) {
		this.pessoaRepository.deleteById(codigo);
	}
	
	/**
	 * Versao Antiga de deletar
	 *  @DeleteMapping("/{codigo}")
	 *	@ResponseStatus(HttpStatus.NO_CONTENT)
	 *	public void remover(@PathVariable Long codigo) {
	 *	    Pessoa pessoa = new Pessoa();
	 *	    pessoa.setCodigo(codigo);
	 *	    this.pessoaRepository.delete(pessoa);
	 *	 }
	 */
	
	
	/**
	 * Metodo para Atualizar
	 * @param codigo
	 * @return
	 */		
	@PutMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA')")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa) {
		Pessoa pessoaSalva = pessoaService.atualizar(codigo, pessoa);
		return ResponseEntity.ok(pessoaSalva);
	}
	
	/**
	 * Atualzia parcialmente a propriedade ATIVO de PESSOA
	 * @param codigo
	 * @param ativo
	 */
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA')")
	private void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		pessoaService.atualizarPropriedadeAtivo(codigo, ativo);
		
	}
	
	
	
}
