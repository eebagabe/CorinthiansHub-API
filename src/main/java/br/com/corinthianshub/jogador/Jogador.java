package br.com.corinthianshub.jogador;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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

	private Integer gols;

	private Integer assistencias;

	// so goleiros
	private Integer penaltisDefendidos;

	private Long multaNacionalReais;

	private Long multaInternacionalEuros;

	// 0 = chegou sem custo; null = nao informado
	private Long valorAquisicaoReais;

	private LocalDate fimContrato;

	// estimativa (o clube nao divulga salarios); 0 = nao encontrado
	private long salarioMensalReais;

	private LocalDate atualizadoEm;

	private String fontes;

	// poucos titulos por jogador: carregar junto evita LazyInitializationException fora da transacao
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "jogador_titulos", joinColumns = @JoinColumn(name = "jogador_id"))
	@OrderColumn(name = "ordem")
	@Column(name = "titulo")
	private List<String> titulos;

	protected Jogador() {
	}

	public Jogador(JogadorForm form) {
		this.titulos = new ArrayList<>();
		aplicar(form);
	}

	public void aplicar(JogadorForm f) {
		numero = f.numero();
		nome = f.nome().trim();
		posicao = f.posicao();
		nacionalidade = f.nacionalidade();
		nomeCompleto = f.nomeCompleto();
		nascimento = f.nascimento();
		jogos = f.jogos();
		titular = f.titular();
		jogosSemSofrerGols = f.jogosSemSofrerGols();
		jogosSelecao = f.jogosSelecao();
		gols = f.gols();
		assistencias = f.assistencias();
		penaltisDefendidos = "Goleiro".equals(posicao) ? f.penaltisDefendidos() : null;
		multaNacionalReais = f.multaNacionalReais();
		multaInternacionalEuros = f.multaInternacionalEuros();
		valorAquisicaoReais = f.valorAquisicaoReais();
		salarioMensalReais = f.salarioMensalReais();
		fimContrato = f.fimContrato();
		fontes = f.fontes();
		titulos.clear();
		titulos.addAll(f.titulos());
		atualizadoEm = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
	}

	public void trocarFoto(String url) {
		fotoUrl = url;
		atualizadoEm = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
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

	public Integer getGols() {
		return gols;
	}

	public Integer getAssistencias() {
		return assistencias;
	}

	public Integer getPenaltisDefendidos() {
		return penaltisDefendidos;
	}

	public Long getMultaNacionalReais() {
		return multaNacionalReais;
	}

	public Long getMultaInternacionalEuros() {
		return multaInternacionalEuros;
	}

	public Long getValorAquisicaoReais() {
		return valorAquisicaoReais;
	}

	public LocalDate getFimContrato() {
		return fimContrato;
	}

	public long getSalarioMensalReais() {
		return salarioMensalReais;
	}

	public LocalDate getAtualizadoEm() {
		return atualizadoEm;
	}

	public String getFontes() {
		return fontes;
	}

	public Long getId() {
		return id;
	}

	public List<String> getTitulos() {
		return titulos;
	}

}
