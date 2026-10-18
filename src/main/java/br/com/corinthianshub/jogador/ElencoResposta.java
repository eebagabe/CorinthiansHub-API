package br.com.corinthianshub.jogador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/** Resposta de GET /api/jogadores, com valores ja convertidos para real. */
public record ElencoResposta(List<Cotacao> cotacoes, List<JogadorResposta> jogadores) {

	public record JogadorResposta(int numero, String nome, String posicao, String fotoUrl, String nomeCompleto,
			LocalDate nascimento, Integer jogos, Integer titular, Integer jogosSemSofrerGols, Integer jogosSelecao,
			Long valorMercadoReais, long salarioMensalEstimadoReais, LocalDate fimContrato, List<String> titulos,
			LocalDate atualizadoEm) {

		static JogadorResposta de(Jogador j, Cotacao euro, Cotacao dolar) {
			return new JogadorResposta(j.getNumero(), j.getNome(), j.getPosicao(), j.getFotoUrl(), j.getNomeCompleto(),
					j.getNascimento(), j.getJogos(), j.getTitular(), j.getJogosSemSofrerGols(), j.getJogosSelecao(),
					j.getValorMercadoEuros() == null ? null : reais(j.getValorMercadoEuros(), euro, 1),
					reais(j.getSalarioAnualEstimadoUsd(), dolar, 12), j.getFimContrato(), j.getTitulos(),
					j.getAtualizadoEm());
		}

		private static long reais(long valor, Cotacao cotacao, int divisor) {
			return BigDecimal.valueOf(valor)
				.multiply(cotacao.getValorBrl())
				.divide(BigDecimal.valueOf(divisor), 0, RoundingMode.HALF_UP)
				.longValueExact();
		}

	}

}
