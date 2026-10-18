package br.com.corinthianshub;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/** Erros esperados (404, 429, foto invalida...) saem como {"erro": "mensagem"} para o frontend exibir. */
@RestControllerAdvice
public class ErrosApi {

	@ExceptionHandler(ResponseStatusException.class)
	ResponseEntity<Map<String, String>> status(ResponseStatusException e) {
		return ResponseEntity.status(e.getStatusCode())
			.body(Map.of("erro", e.getReason() != null ? e.getReason() : "Erro"));
	}

}
