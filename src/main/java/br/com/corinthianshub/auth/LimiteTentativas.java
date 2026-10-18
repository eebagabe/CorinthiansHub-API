package br.com.corinthianshub.auth;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/** Bloqueia o login de um email por 15 minutos apos 5 senhas erradas (contra forca bruta). */
@Component
public class LimiteTentativas {

	static final int MAXIMO = 5;
	static final Duration JANELA = Duration.ofMinutes(15);

	private record Falhas(int quantidade, Instant inicio) {
	}

	// shortcut: em memoria, zera ao reiniciar e nao funciona com varias instancias; trocar por banco/Redis se escalar
	private final Map<String, Falhas> falhas = new ConcurrentHashMap<>();
	private final Clock relogio;

	public LimiteTentativas() {
		this(Clock.systemUTC());
	}

	LimiteTentativas(Clock relogio) {
		this.relogio = relogio;
	}

	public boolean bloqueado(String email) {
		Falhas f = falhas.get(email);
		return f != null && f.quantidade() >= MAXIMO && relogio.instant().isBefore(f.inicio().plus(JANELA));
	}

	public void registrarFalha(String email) {
		Instant agora = relogio.instant();
		falhas.compute(email, (k, f) -> f == null || agora.isAfter(f.inicio().plus(JANELA)) ? new Falhas(1, agora)
				: new Falhas(f.quantidade() + 1, f.inicio()));
	}

	public void limpar(String email) {
		falhas.remove(email);
	}

}
