package com.rlsp.moneyapi.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Classe que configuracao do AMBIENTE de PRoducao (Local / PRoducao)
 * @author rlatorraca
 *
 */
@ConfigurationProperties("rlspmoney")
public class RlspMoneyApiProperty {
	
	private String origemPermitida = "http://localhost:8000";

	private final Seguranca seguranca = new Seguranca();
	
	public void setOrigemPermitida(String origemPermitida) {
		this.origemPermitida = origemPermitida;
	}

	public String getOrigemPermitida() {
		return origemPermitida;
	}

	public Seguranca getSeguranca() {
		return seguranca;
	}
	
	public static class Seguranca {

		private boolean enableHttps;

		public boolean isEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}

	}
}
