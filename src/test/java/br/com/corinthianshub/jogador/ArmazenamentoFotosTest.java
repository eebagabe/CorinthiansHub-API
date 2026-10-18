package br.com.corinthianshub.jogador;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import br.com.corinthianshub.jogador.ArmazenamentoFotos.Formato;

class ArmazenamentoFotosTest {

	@Test
	void detectaFormatoPelosBytes() {
		assertThat(ArmazenamentoFotos.detectar(new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00 }))
			.contains(Formato.JPEG);
		assertThat(ArmazenamentoFotos.detectar(new byte[] { (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0 }))
			.contains(Formato.PNG);
		assertThat(ArmazenamentoFotos.detectar("RIFF\0\0\0\0WEBPVP8 ".getBytes(StandardCharsets.ISO_8859_1)))
			.contains(Formato.WEBP);
	}

	@Test
	void rejeitaOutrosArquivos() {
		assertThat(ArmazenamentoFotos.detectar("<svg onload=alert(1)>".getBytes(StandardCharsets.UTF_8))).isEmpty();
		assertThat(ArmazenamentoFotos.detectar(new byte[0])).isEmpty();
	}

	@Test
	void semBucketFicaDesativado() {
		assertThat(new ArmazenamentoFotos("", "", "", "", "").configurado()).isFalse();
	}

}
