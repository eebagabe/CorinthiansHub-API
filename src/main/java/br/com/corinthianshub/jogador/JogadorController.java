package br.com.corinthianshub.jogador;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jogadores")
public class JogadorController {

	private final JogadorRepository jogadores;

	public JogadorController(JogadorRepository jogadores) {
		this.jogadores = jogadores;
	}

	@GetMapping
	public List<Jogador> listar() {
		return jogadores.findAllByOrderByNumero();
	}

}
