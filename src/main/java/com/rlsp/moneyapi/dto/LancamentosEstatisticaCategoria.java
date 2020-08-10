package com.rlsp.moneyapi.dto;

import java.math.BigDecimal;

import com.rlsp.moneyapi.model.Categoria;

/**
 * Retorna um somatorio de gastos por Categoria
 * @author rlatorraca
 *
 */
public class LancamentosEstatisticaCategoria {

	private Categoria categoria;
	private BigDecimal total;
	
	public LancamentosEstatisticaCategoria(Categoria categoria, BigDecimal total) {
		super();
		this.categoria = categoria;
		this.total = total;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
	
	
	
}
