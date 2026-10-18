package br.com.corinthianshub.jogador;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.corinthianshub.jogador.AtualizadorPtax.Boletim;
import br.com.corinthianshub.jogador.AtualizadorPtax.RespostaBcb;

class JogadorPublicoTest {

	@Test
	void converteMultaInternacionalDeEuroParaReal() {
		Jogador j = mock(Jogador.class);
		when(j.getMultaInternacionalEuros()).thenReturn(11_000_000L);
		when(j.getMultaNacionalReais()).thenReturn(500_000_000L);

		JogadorPublico p = JogadorPublico.de(j, new BigDecimal("5.7088"));

		assertThat(p.multaInternacionalReais()).isEqualTo(62_796_800L);
		assertThat(p.multaNacionalReais()).isEqualTo(500_000_000L); // ja esta em real
	}

	@Test
	void semMultaInternacionalContinuaNula() {
		Jogador j = mock(Jogador.class);
		when(j.getMultaInternacionalEuros()).thenReturn(null); // mockito devolve 0L para Long por padrao

		assertThat(JogadorPublico.de(j, new BigDecimal("5.7088")).multaInternacionalReais()).isNull();
	}

	@Test
	void usaOFechamentoMaisRecenteDaPtax() {
		RespostaBcb resposta = new RespostaBcb(List.of(
				new Boletim(new BigDecimal("5.70"), "2026-10-15 13:04:00.000", "Fechamento"),
				new Boletim(new BigDecimal("5.80"), "2026-10-16 10:00:00.000", "Intermediario"),
				new Boletim(new BigDecimal("5.71"), "2026-10-16 13:04:00.000", "Fechamento")));

		assertThat(AtualizadorPtax.fechamentoMaisRecente(resposta)).get()
			.extracting(Boletim::cotacaoVenda)
			.isEqualTo(new BigDecimal("5.71"));
		assertThat(AtualizadorPtax.fechamentoMaisRecente(new RespostaBcb(null))).isEmpty();
	}

}
