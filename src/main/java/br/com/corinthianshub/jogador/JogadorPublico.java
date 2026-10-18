package br.com.corinthianshub.jogador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/** Jogador como o site do cliente ve: valores em real, sem campos internos do backoffice. */
public record JogadorPublico(int numero, String nome, String posicao, String fotoUrl, String nomeCompleto,
		LocalDate nascimento, Integer jogos, Integer titular, Integer jogosSemSofrerGols, Integer jogosSelecao,
		Long valorMercadoReais, long salarioMensalReais, LocalDate fimContrato, List<String> titulos,
		LocalDate atualizadoEm) {

	static JogadorPublico de(Jogador j, BigDecimal euroEmReais) {
		Long mercado = j.getValorMercadoEuros() == null ? null
				: BigDecimal.valueOf(j.getValorMercadoEuros())
					.multiply(euroEmReais)
					.setScale(0, RoundingMode.HALF_UP)
					.longValueExact();
		return new JogadorPublico(j.getNumero(), j.getNome(), j.getPosicao(), j.getFotoUrl(), j.getNomeCompleto(),
				j.getNascimento(), j.getJogos(), j.getTitular(), j.getJogosSemSofrerGols(), j.getJogosSelecao(), mercado,
				j.getSalarioMensalReais(), j.getFimContrato(), j.getTitulos(), j.getAtualizadoEm());
	}

}
