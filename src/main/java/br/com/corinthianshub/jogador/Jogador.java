package br.com.corinthianshub.jogador;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "jogadores")
public class Jogador {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int numero;

	private String nome;

	private String posicao;

	private String nacionalidade;

	private String fotoUrl;

	private String nomeCompleto;

	private LocalDate nascimento;

	private Integer jogos;

	private Integer titular;

	private Integer jogosSemSofrerGols;

	private Integer jogosSelecao;

	private Long valorMercadoEuros;

	private LocalDate valorMercadoAtualizadoEm;

	private LocalDate fimContrato;

	@Column(name = "salario_anual_estimado_usd")
	private long salarioAnualEstimadoUsd;

	private LocalDate atualizadoEm;

	// poucos titulos por jogador: carregar junto evita LazyInitializationException fora da transacao
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "jogador_titulos", joinColumns = @JoinColumn(name = "jogador_id"))
	@OrderColumn(name = "ordem")
	@Column(name = "titulo")
	private List<String> titulos;

	protected Jogador() {
	}

	public int getNumero() {
		return numero;
	}

	public String getNome() {
		return nome;
	}

	public String getPosicao() {
		return posicao;
	}

	public String getNacionalidade() {
		return nacionalidade;
	}

	public String getFotoUrl() {
		return fotoUrl;
	}

	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public LocalDate getNascimento() {
		return nascimento;
	}

	public Integer getJogos() {
		return jogos;
	}

	public Integer getTitular() {
		return titular;
	}

	public Integer getJogosSemSofrerGols() {
		return jogosSemSofrerGols;
	}

	public Integer getJogosSelecao() {
		return jogosSelecao;
	}

	public Long getValorMercadoEuros() {
		return valorMercadoEuros;
	}

	public LocalDate getValorMercadoAtualizadoEm() {
		return valorMercadoAtualizadoEm;
	}

	public LocalDate getFimContrato() {
		return fimContrato;
	}

	public long getSalarioAnualEstimadoUsd() {
		return salarioAnualEstimadoUsd;
	}

	public LocalDate getAtualizadoEm() {
		return atualizadoEm;
	}

	public List<String> getTitulos() {
		return titulos;
	}

}
