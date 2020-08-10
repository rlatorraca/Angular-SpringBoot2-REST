package com.rlsp.moneyapi.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Classe que configuracao do AMBIENTE de PRoducao (Local / PRoducao)
 * @author rlatorraca
 *
 */
@ConfigurationProperties("rlspmoney")
public class RlspMoneyApiProperty {
	
	//private String originPermitida = "https://rlspmoney-angular10.herokuapp.com";
	private String originPermitida = "http://localhost:4200";

	private final Seguranca seguranca = new Seguranca();
	
	
	public String getOriginPermitida() {
		return originPermitida;
	}

	public void setOriginPermitida(String originPermitida) {
		this.originPermitida = originPermitida;
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
