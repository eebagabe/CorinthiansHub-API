package br.com.corinthianshub.jogador;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Dados que o backoffice envia para criar ou editar um jogador. Valor de mercado em euro, salario em real. */
public record JogadorForm(@NotNull @Min(1) @Max(999) Integer numero, @NotBlank @Size(max = 100) String nome,
		@NotNull @Pattern(regexp = "Goleiro|Defensor|Meio-campista|Atacante") String posicao,
		@NotNull @Pattern(regexp = "[A-Z]{3}") String nacionalidade,
		@Size(max = 1000) @Pattern(regexp = "https://.+") String fotoUrl, @Size(max = 200) String nomeCompleto,
		@Past LocalDate nascimento, @PositiveOrZero Integer jogos, @PositiveOrZero Integer titular,
		@PositiveOrZero Integer jogosSemSofrerGols, @PositiveOrZero Integer jogosSelecao,
		@PositiveOrZero Long valorMercadoEuros, @PositiveOrZero long salarioMensalReais, LocalDate fimContrato,
		@NotNull List<@NotBlank @Size(max = 200) String> titulos, @Size(max = 5000) String fontes) {
}
