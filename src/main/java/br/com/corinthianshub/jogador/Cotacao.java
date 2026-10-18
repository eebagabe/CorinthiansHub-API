package br.com.corinthianshub.jogador;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cotacoes")
public class Cotacao {

	@Id
	private String moeda;

	private BigDecimal valorBrl;

	private LocalDate data;

	protected Cotacao() {
	}

	public Cotacao(String moeda, BigDecimal valorBrl, LocalDate data) {
		this.moeda = moeda;
		this.valorBrl = valorBrl;
		this.data = data;
	}

	public void atualizar(BigDecimal valorBrl, LocalDate data) {
		this.valorBrl = valorBrl;
		this.data = data;
	}

	public String getMoeda() {
		return moeda;
	}

	public BigDecimal getValorBrl() {
		return valorBrl;
	}

	public LocalDate getData() {
		return data;
	}

}
