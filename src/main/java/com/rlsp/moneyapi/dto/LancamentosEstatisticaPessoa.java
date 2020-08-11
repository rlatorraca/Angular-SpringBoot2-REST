package com.rlsp.moneyapi.dto;

import java.math.BigDecimal;

import com.rlsp.moneyapi.model.Pessoa;
import com.rlsp.moneyapi.model.TipoLancamento;

public class LancamentosEstatisticaPessoa {
	
	private TipoLancamento tipo;
	private Pessoa pessoa;
	private BigDecimal total;
	
	public LancamentosEstatisticaPessoa(TipoLancamento tipo, Pessoa pessoa, BigDecimal total) {
	
		this.tipo = tipo;
		this.pessoa = pessoa;
		this.total = total;
	}

	public TipoLancamento getTipo() {
		return tipo;
	}

	public void setTipo(TipoLancamento tipo) {
		this.tipo = tipo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
	
	
	
	

}
