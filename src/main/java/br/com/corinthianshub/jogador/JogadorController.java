package br.com.corinthianshub.jogador;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.corinthianshub.jogador.ElencoResposta.JogadorResposta;

@RestController
@RequestMapping("/api/jogadores")
public class JogadorController {

	private final JogadorRepository jogadores;
	private final CotacaoRepository cotacoes;

	public JogadorController(JogadorRepository jogadores, CotacaoRepository cotacoes) {
		this.jogadores = jogadores;
		this.cotacoes = cotacoes;
	}

	@GetMapping
	public ElencoResposta listar() {
		List<Cotacao> todas = cotacoes.findAll();
		Map<String, Cotacao> porMoeda = todas.stream().collect(Collectors.toMap(Cotacao::getMoeda, Function.identity()));
		Cotacao euro = porMoeda.get("EUR");
		Cotacao dolar = porMoeda.get("USD");
		if (euro == null || dolar == null) {
			throw new IllegalStateException("Cotacoes EUR e USD precisam existir na tabela cotacoes");
		}
		return new ElencoResposta(todas,
				jogadores.findAllByOrderByNumero().stream().map(j -> JogadorResposta.de(j, euro, dolar)).toList());
	}

}
