package br.com.corinthianshub.usuario;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** Cria o usuario admin a partir de ADMIN_EMAIL/ADMIN_PASSWORD, so se ele ainda nao existir. */
@Component
public class AdminSeed implements ApplicationRunner {

	private final UsuarioRepository usuarios;
	private final String email;
	private final String senha;

	public AdminSeed(UsuarioRepository usuarios, @Value("${app.admin.email}") String email,
			@Value("${app.admin.password}") String senha) {
		if (email.isBlank() || senha.isBlank()) {
			throw new IllegalStateException("ADMIN_EMAIL e ADMIN_PASSWORD precisam estar definidos");
		}
		this.usuarios = usuarios;
		this.email = email;
		this.senha = senha;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!usuarios.existsByEmail(email)) {
			usuarios.save(new Usuario(email, new BCryptPasswordEncoder().encode(senha), "ADMIN"));
		}
	}

}
