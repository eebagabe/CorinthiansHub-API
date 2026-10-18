package br.com.corinthianshub.jogador;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.corinthianshub.jogador.ElencoResposta.JogadorResposta;

class JogadorControllerTest {

	private final JogadorRepository jogadores = mock(JogadorRepository.class);
	private final CotacaoRepository cotacoes = mock(CotacaoRepository.class);
	private final JogadorController controller = new JogadorController(jogadores, cotacoes);

	private static final LocalDate DATA = LocalDate.of(2026, 10, 16);

	private static Jogador jogador(Long valorMercadoEuros, long salarioAnualUsd) {
		Jogador j = mock(Jogador.class);
		when(j.getValorMercadoEuros()).thenReturn(valorMercadoEuros);
		when(j.getSalarioAnualEstimadoUsd()).thenReturn(salarioAnualUsd);
		return j;
	}

	@Test
	void converteMercadoDeEuroESalarioAnualEmDolarParaReaisPorMes() {
		when(cotacoes.findAll()).thenReturn(List.of(new Cotacao("EUR", new BigDecimal("5.7088"), DATA, "PTAX"),
				new Cotacao("USD", new BigDecimal("5.0826"), DATA, "PTAX")));
		List<Jogador> lista = List.of(jogador(12_000_000L, 636_272), jogador(null, 0));
		when(jogadores.findAllByOrderByNumero()).thenReturn(lista);

		List<JogadorResposta> resposta = controller.listar().jogadores();

		assertThat(resposta.get(0).valorMercadoReais()).isEqualTo(68_505_600L);
		assertThat(resposta.get(0).salarioMensalEstimadoReais()).isEqualTo(269_493L); // 636272 * 5.0826 / 12
		assertThat(resposta.get(1).valorMercadoReais()).isNull();
		assertThat(resposta.get(1).salarioMensalEstimadoReais()).isZero();
	}

	@Test
	void falhaSemCotacao() {
		when(cotacoes.findAll()).thenReturn(List.of());

		assertThatThrownBy(controller::listar).isInstanceOf(IllegalStateException.class);
	}

}
