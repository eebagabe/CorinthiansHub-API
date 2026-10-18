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

	private static JogadorForm form(int numero, String posicao, long salario) {
		return new JogadorForm(numero, "Hugo Souza", posicao, "BRA", null, LocalDate.of(1999, 1, 31), 139,
				139, 57, 2, 500_000_000L, 100_000_000L, 0L, salario, LocalDate.of(2028, 12, 31),
				List.of("Campeao Paulista"), null);
	}

	private List<String> camposInvalidos(JogadorForm f) {
		return validador.validate(f).stream().map(ConstraintViolation::getPropertyPath).map(Object::toString).toList();
	}

	@Test
	void aceitaJogadorValido() {
		assertThat(camposInvalidos(form(1, "Goleiro", 375_000))).isEmpty();
	}

	@Test
	void rejeitaNumeroPosicaoESalarioInvalidos() {
		assertThat(camposInvalidos(form(0, "Lateral", -1)))
			.containsExactlyInAnyOrder("numero", "posicao", "salarioMensalReais");
	}

	@Test
	void aplicaFormNaEntidade() {
		Jogador j = new Jogador(form(1, "Goleiro", 375_000));

		assertThat(j.getMultaInternacionalEuros()).isEqualTo(100_000_000L);
		assertThat(j.getValorAquisicaoReais()).isZero();
		assertThat(j.getTitulos()).containsExactly("Campeao Paulista");
		assertThat(j.getAtualizadoEm()).isNotNull();
	}

}
