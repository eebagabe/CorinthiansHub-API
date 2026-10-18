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

	private String fonte;

	protected Cotacao() {
	}

	public Cotacao(String moeda, BigDecimal valorBrl, LocalDate data, String fonte) {
		this.moeda = moeda;
		this.valorBrl = valorBrl;
		this.data = data;
		this.fonte = fonte;
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

	public String getFonte() {
		return fonte;
	}

}
