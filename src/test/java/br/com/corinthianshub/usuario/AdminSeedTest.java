package br.com.corinthianshub.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class AdminSeedTest {

	private final UsuarioRepository usuarios = mock(UsuarioRepository.class);

	@Test
	void criaAdminComSenhaEmHash() {
		when(usuarios.existsByEmail("admin@teste.com")).thenReturn(false);

		new AdminSeed(usuarios, "admin@teste.com", "senha-forte").run(null);

		ArgumentCaptor<Usuario> salvo = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarios).save(salvo.capture());
		assertThat(salvo.getValue().getEmail()).isEqualTo("admin@teste.com");
		assertThat(salvo.getValue().getPapel()).isEqualTo("ADMIN");
		assertThat(salvo.getValue().getSenhaHash()).isNotEqualTo("senha-forte");
		assertThat(new BCryptPasswordEncoder().matches("senha-forte", salvo.getValue().getSenhaHash())).isTrue();
	}

	@Test
	void naoDuplicaAdminExistente() {
		when(usuarios.existsByEmail("admin@teste.com")).thenReturn(true);

		new AdminSeed(usuarios, "admin@teste.com", "senha-forte").run(null);

		verify(usuarios, never()).save(any());
	}

	@Test
	void exigeVariaveisDefinidas() {
		assertThatThrownBy(() -> new AdminSeed(usuarios, "", "")).isInstanceOf(IllegalStateException.class);
	}

}
