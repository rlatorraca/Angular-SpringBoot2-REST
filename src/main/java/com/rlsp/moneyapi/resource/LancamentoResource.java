package com.rlsp.moneyapi.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rlsp.moneyapi.event.RecursoCriadoEvent;
import com.rlsp.moneyapi.exceptionhandler.RlspMoneyExceptionHandler.MensagemErro;
import com.rlsp.moneyapi.model.Lancamento;
import com.rlsp.moneyapi.repository.LancamentoRepository;
import com.rlsp.moneyapi.service.LancamentoService;
import com.rlsp.moneyapi.service.exception.PessoaInexistenteOuInativaException;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private MessageSource messageSource; // Pega as MENSAGENS presentes no arquivo "messages.proporties"
	
	
	@GetMapping
	public List<Lancamento> listar(){
		List<Lancamento> lancamentos = lancamentoRepository.findAll();
		return lancamentos;
	}
	
	@GetMapping("/{codigo}")
	private ResponseEntity<Lancamento> buscarLancamentoPeloCodigo(@PathVariable Long codigo){
		
		// Utilizando MAP
		return lancamentoRepository.findById(codigo)
				.map(lancamento -> ResponseEntity.ok(lancamento))
				.orElse(ResponseEntity.notFound().build());
		
		//Utilizando isPresent()
				/* Optional<Lancamento> lancamento = this.pessoaRepository.findById(codigo);
				    return lancamentoRepository.isPresent() ? ResponseEntity.ok(lancamento.get()) : ResponseEntity.notFound().build();
				*/	
	}
	
	@PostMapping
	private ResponseEntity<Lancamento> salvarLancamento(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response){
		//Lancamento lancamentoSalvo = lancamentoRepository.save(lancamento);
		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamento);
	
		// Construindo a LOCATION, chamando o "RecursoCrParcela iadoEvent"
		// this ==> sera a funcao/metodo que chamou o EVENTO
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));
		
		
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
	}
	
	
}
