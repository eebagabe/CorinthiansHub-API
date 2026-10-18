package br.com.corinthianshub.jogador;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public List<JogadorPublico> listar() {
		Cotacao euro = cotacoes.findById("EUR").orElseThrow(() -> new IllegalStateException("Cotacao EUR ausente"));
		return jogadores.findAllByOrderByNumero().stream().map(j -> JogadorPublico.de(j, euro.getValorBrl())).toList();
	}

}
