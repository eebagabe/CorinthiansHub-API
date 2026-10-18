package br.com.corinthianshub.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.corinthianshub.usuario.Usuario;
import br.com.corinthianshub.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
public class AuthController {

	static final Duration VALIDADE = Duration.ofHours(8);

	public record Login(@NotBlank @Email String email, @NotBlank String senha) {
	}

	public record Token(String token, Instant expiraEm) {
	}

	private final UsuarioRepository usuarios;
	private final PasswordEncoder senhas;
	private final JwtEncoder jwt;
	private final LimiteTentativas limite;
	// compara com um hash qualquer quando o email nao existe, para o tempo de resposta nao revelar emails validos
	private final String hashFalso;

	public AuthController(UsuarioRepository usuarios, PasswordEncoder senhas, JwtEncoder jwt, LimiteTentativas limite) {
		this.usuarios = usuarios;
		this.senhas = senhas;
		this.jwt = jwt;
		this.limite = limite;
		this.hashFalso = senhas.encode("senha-que-nunca-confere");
	}

	@PostMapping("/api/auth/login")
	public Token login(@Valid @RequestBody Login login) {
		String email = login.email().trim().toLowerCase(Locale.ROOT);
		if (limite.bloqueado(email)) {
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Muitas tentativas. Tente em 15 minutos.");
		}
		Usuario usuario = usuarios.findByEmail(email).orElse(null);
		boolean confere = senhas.matches(login.senha(), usuario != null ? usuario.getSenhaHash() : hashFalso);
		if (usuario == null || !confere) {
			limite.registrarFalha(email);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha invalidos");
		}
		limite.limpar(email);
		Instant agora = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
			.subject(usuario.getEmail())
			.issuedAt(agora)
			.expiresAt(agora.plus(VALIDADE))
			.claim("scope", usuario.getPapel())
			.build();
		String token = jwt.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
			.getTokenValue();
		return new Token(token, agora.plus(VALIDADE));
	}

	@GetMapping("/api/admin/sessao")
	public Map<String, String> sessao(@AuthenticationPrincipal Jwt token) {
		return Map.of("email", token.getSubject());
	}

}
