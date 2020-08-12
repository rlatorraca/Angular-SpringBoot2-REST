package com.rlsp.moneyapi.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.rlsp.moneyapi.dto.LancamentosEstatisticaPessoa;
import com.rlsp.moneyapi.mail.Mailer;
import com.rlsp.moneyapi.model.Lancamento;
import com.rlsp.moneyapi.model.Pessoa;
import com.rlsp.moneyapi.model.Usuario;
import com.rlsp.moneyapi.repository.LancamentoRepository;
import com.rlsp.moneyapi.repository.PessoaRepository;
import com.rlsp.moneyapi.repository.UsuarioRepository;
import com.rlsp.moneyapi.service.exception.PessoaInexistenteOuInativaException;
import com.rlsp.moneyapi.storage.S3;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


@Service
public class LancamentoService {
	
	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";
	
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LancamentoService.class); // Log para agendamento do email
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private Mailer mailer;
	
	@Autowired
	private S3 s3;
	
	/**
	 * No caso abaixo sera executado todos os dias as 6 horas da manha
	 */
	@Scheduled(fixedDelay = 1000 * 60 * 30) // 30 minutos
	//@Scheduled(cron = "0 0 6 * * * ") // (cron = " segundos minutos hora dia_do_mes mes dia_da_semana)
	public void avisarSobreLancamentosVencidos() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("Prepararando envio de e-mails para lançamentos recebidos ...");
		}
		
		List<Lancamento> vencidos = lancamentoRepository.findByDataVencimentoLessThanEqualAndDataPagamentoNull(LocalDate.now());
		
		if(vencidos.isEmpty()) {
			logger.debug("Sem itens vencidos para remeter para o e-mail !");
		} else {
			logger.debug("Quantidade de emails: " + vencidos.size());
		}
		List<Usuario> destinatarios = usuarioRepository.findByPermissoesDescricao(DESTINATARIOS);
		
		if(destinatarios.isEmpty()) {
			logger.debug("O sistema não encontrou destinatários para o envio !");
		}
		
		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);
		
		logger.info("Email enviado com SUCESSO !!!");
		
	}
	
	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws Exception {
		List<LancamentosEstatisticaPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);
		
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("DT_INICIO", Date.valueOf(inicio));
		parametros.put("DT_FIM", Date.valueOf(fim));
		parametros.put("REPORT_LOCALE", new Locale("pt", "BR"));
		
		InputStream inputStream = this.getClass().getResourceAsStream(
				"/reports/Lancamentos-por-pessoa.jasper");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros,
				new JRBeanCollectionDataSource(dados));
		
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

	public Lancamento salvar(Lancamento lancamento) {
		
		/**
		 * Verifica se a PESSOA : EXISTE e esta ATIVA
		 */
		Optional<Pessoa> pessoaSalva = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		if(!pessoaSalva.isPresent() || !pessoaSalva.get().getAtivo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		
		if(StringUtils.hasText(lancamento.getAnexo())) {
			s3.salvar(lancamento.getAnexo());
		}
		return lancamentoRepository.save(lancamento);
		
		
	}
	
	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		
		Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
		
		if(!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}
		
		// se  NAO existe ANEXO dentro do Novo Lancamento E se a o Lancamento  Antigo tinha ANEXO, para ENTAO REMOVER
		if (StringUtils.isEmpty(lancamento.getAnexo()) && StringUtils.hasText(lancamentoSalvo.getAnexo())) {
			s3.remover(lancamentoSalvo.getAnexo());
		
		// Se o Lancamento NOVO JA POSSUI um Arquivo E Este eh diferente do Anexo ANTIGO do Lancamento
		} else if (StringUtils.hasText(lancamento.getAnexo()) && !lancamento.getAnexo().equals(lancamentoSalvo.getAnexo())) {
			s3.substituir(lancamentoSalvo.getAnexo(), lancamento.getAnexo());
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
