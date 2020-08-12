package com.rlsp.moneyapi.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import com.rlsp.moneyapi.RlspmoneyApiApplication;
import com.rlsp.moneyapi.model.Lancamento;
import com.rlsp.moneyapi.storage.S3;

public class LancamentoAnexoListener {

	@PostLoad
	public void postLoad(Lancamento lancamento) {
		if (StringUtils.hasText(lancamento.getAnexo())) {
			S3 s3 = RlspmoneyApiApplication.getBean(S3.class); // Chama o getBean dentro da Classe do SpringBooter retorna um instancia de S3
			lancamento.setUrlAnexo(s3.configurarUrl(lancamento.getAnexo()));
		}
	}

}
