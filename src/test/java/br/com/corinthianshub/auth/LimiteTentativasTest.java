package br.com.corinthianshub.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

class LimiteTentativasTest {

	private Instant agora = Instant.parse("2026-10-18T12:00:00Z");

	private final LimiteTentativas limite = new LimiteTentativas(new Clock() {
		@Override
		public Instant instant() {
			return agora;
		}

		@Override
		public ZoneOffset getZone() {
			return ZoneOffset.UTC;
		}

		@Override
		public Clock withZone(java.time.ZoneId zone) {
			return this;
		}
	});

	@Test
	void bloqueiaAposCincoFalhasEDesbloqueiaDepoisDaJanela() {
		for (int i = 0; i < LimiteTentativas.MAXIMO - 1; i++) {
			limite.registrarFalha("a@a.com");
		}
		assertThat(limite.bloqueado("a@a.com")).isFalse();

		limite.registrarFalha("a@a.com");
		assertThat(limite.bloqueado("a@a.com")).isTrue();
		assertThat(limite.bloqueado("outro@a.com")).isFalse();

		agora = agora.plus(LimiteTentativas.JANELA).plusSeconds(1);
		assertThat(limite.bloqueado("a@a.com")).isFalse();
	}

	@Test
	void loginCertoZeraContagem() {
		for (int i = 0; i < LimiteTentativas.MAXIMO; i++) {
			limite.registrarFalha("a@a.com");
		}
		limite.limpar("a@a.com");
		assertThat(limite.bloqueado("a@a.com")).isFalse();
	}

}
