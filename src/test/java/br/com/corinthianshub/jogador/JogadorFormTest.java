package br.com.corinthianshub.jogador;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class JogadorFormTest {

	private final Validator validador = Validation.buildDefaultValidatorFactory().getValidator();

	private static JogadorForm form(int numero, String posicao, long salario, String fotoUrl) {
		return new JogadorForm(numero, "Hugo Souza", posicao, "BRA", fotoUrl, null, LocalDate.of(1999, 1, 31), 139,
				139, 57, 2, 11_000_000L, salario, LocalDate.of(2028, 12, 31), List.of("Campeao Paulista"), null);
	}

	private List<String> camposInvalidos(JogadorForm f) {
		return validador.validate(f).stream().map(ConstraintViolation::getPropertyPath).map(Object::toString).toList();
	}

	@Test
	void aceitaJogadorValido() {
		assertThat(camposInvalidos(form(1, "Goleiro", 375_000, "https://exemplo.com/foto.webp"))).isEmpty();
	}

	@Test
	void rejeitaPosicaoDesconhecidaSalarioNegativoEFotoSemHttps() {
		assertThat(camposInvalidos(form(0, "Lateral", -1, "http://exemplo.com/foto.webp")))
			.containsExactlyInAnyOrder("numero", "posicao", "salarioMensalReais", "fotoUrl");
	}

	@Test
	void aplicaFormNaEntidade() {
		Jogador j = new Jogador(form(1, "Goleiro", 375_000, null));

		assertThat(j.getValorMercadoEuros()).isEqualTo(11_000_000L);
		assertThat(j.getTitulos()).containsExactly("Campeao Paulista");
		assertThat(j.getAtualizadoEm()).isNotNull();
	}

}
