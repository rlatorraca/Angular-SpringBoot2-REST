package com.rlsp.moneyapi.dto;

import java.math.BigDecimal;

import com.rlsp.moneyapi.model.Pessoa;
import com.rlsp.moneyapi.model.TipoLancamento;

public class LancamentosEstatisticaPessoa {
	
	private TipoLancamento tipo;
	private Pessoa pessoa;
	private BigDecimal valor;
	
	public LancamentosEstatisticaPessoa(TipoLancamento tipo, Pessoa pessoa, BigDecimal valor) {
	
		this.tipo = tipo;
		this.pessoa = pessoa;
		this.valor = valor;
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

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	
	
	
	

}
