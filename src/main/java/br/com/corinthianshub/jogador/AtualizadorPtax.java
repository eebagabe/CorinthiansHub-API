package br.com.corinthianshub.jogador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

/** Atualiza a cotacao do euro com a PTAX de venda (fechamento) do Banco Central. */
@Component
public class AtualizadorPtax implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(AtualizadorPtax.class);
	private static final ZoneId BRASILIA = ZoneId.of("America/Sao_Paulo");
	private static final DateTimeFormatter DATA_BCB = DateTimeFormatter.ofPattern("MM-dd-yyyy");

	record RespostaBcb(List<Boletim> value) {
	}

	record Boletim(BigDecimal cotacaoVenda, String dataHoraCotacao, String tipoBoletim) {
	}

	private final CotacaoRepository cotacoes;
	private final RestClient bcb;

	public AtualizadorPtax(CotacaoRepository cotacoes) {
		this.cotacoes = cotacoes;
		this.bcb = RestClient.create("https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata");
	}

	@Override
	public void run(ApplicationArguments args) {
		atualizar();
	}

	// PTAX de fechamento sai por volta das 13h em dias uteis
	@Scheduled(cron = "0 30 13,18 * * MON-FRI", zone = "America/Sao_Paulo")
	@Transactional
	public void atualizar() {
		try {
			LocalDate hoje = LocalDate.now(BRASILIA);
			RespostaBcb resposta = bcb.get()
				.uri("/CotacaoMoedaPeriodo(moeda=@moeda,dataInicial=@dataInicial,dataFinalCotacao=@dataFinalCotacao)"
						+ "?@moeda='EUR'&@dataInicial='{inicio}'&@dataFinalCotacao='{fim}'&$format=json",
						hoje.minusDays(10).format(DATA_BCB), hoje.format(DATA_BCB))
				.retrieve()
				.body(RespostaBcb.class);
			fechamentoMaisRecente(resposta).ifPresent(b -> {
				LocalDate data = LocalDate.parse(b.dataHoraCotacao().substring(0, 10));
				cotacoes.findById("EUR").ifPresentOrElse(c -> c.atualizar(b.cotacaoVenda(), data),
						() -> cotacoes.save(new Cotacao("EUR", b.cotacaoVenda(), data)));
				log.info("PTAX EUR atualizada: {} em {}", b.cotacaoVenda(), data);
			});
		}
		catch (RuntimeException e) {
			// sem cotacao nova, o site segue com a ultima salva
			log.warn("Falha ao atualizar PTAX EUR: {}", e.getMessage());
		}
	}

	static Optional<Boletim> fechamentoMaisRecente(RespostaBcb resposta) {
		if (resposta == null || resposta.value() == null) {
			return Optional.empty();
		}
		return resposta.value()
			.stream()
			.filter(b -> "Fechamento".equals(b.tipoBoletim()))
			.max(Comparator.comparing(Boletim::dataHoraCotacao));
	}

}
