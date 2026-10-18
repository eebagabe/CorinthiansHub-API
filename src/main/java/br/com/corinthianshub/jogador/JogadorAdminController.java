package br.com.corinthianshub.jogador;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

/** Backoffice: valores na forma em que sao guardados (mercado em euro, salario em real). */
@RestController
@RequestMapping("/api/admin/jogadores")
public class JogadorAdminController {

	private final JogadorRepository jogadores;
	private final ArmazenamentoFotos fotos;

	public JogadorAdminController(JogadorRepository jogadores, ArmazenamentoFotos fotos) {
		this.jogadores = jogadores;
		this.fotos = fotos;
	}

	@GetMapping
	public List<Jogador> listar() {
		return jogadores.findAllByOrderByNumero();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Jogador criar(@Valid @RequestBody JogadorForm form) {
		return jogadores.saveAndFlush(new Jogador(form));
	}

	@PutMapping("/{id}")
	@Transactional
	public Jogador editar(@PathVariable long id, @Valid @RequestBody JogadorForm form) {
		Jogador jogador = buscar(id);
		jogador.aplicar(form);
		return jogadores.saveAndFlush(jogador);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable long id) {
		jogadores.delete(buscar(id));
	}

	@PostMapping("/{id}/foto")
	@Transactional
	public Jogador trocarFoto(@PathVariable long id, @RequestParam("arquivo") MultipartFile arquivo) throws IOException {
		if (!fotos.configurado()) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Armazenamento de fotos nao configurado");
		}
		Jogador jogador = buscar(id);
		byte[] conteudo = arquivo.getBytes();
		ArmazenamentoFotos.Formato formato = ArmazenamentoFotos.detectar(conteudo)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie uma imagem JPEG, PNG ou WebP"));
		String antiga = jogador.getFotoUrl();
		jogador.trocarFoto(fotos.salvar(id, conteudo, formato));
		jogadores.saveAndFlush(jogador);
		fotos.remover(antiga);
		return jogador;
	}

	private Jogador buscar(long id) {
		return jogadores.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador nao encontrado"));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	Map<String, Object> invalido(MethodArgumentNotValidException e) {
		Map<String, String> erros = new TreeMap<>();
		e.getBindingResult().getFieldErrors().forEach(f -> erros.putIfAbsent(f.getField(), f.getDefaultMessage()));
		return Map.of("erro", "Dados invalidos", "campos", erros);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseStatus(HttpStatus.CONTENT_TOO_LARGE)
	Map<String, String> grandeDemais() {
		return Map.of("erro", "Foto maior que 5 MB");
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	Map<String, String> duplicado() {
		return Map.of("erro", "Ja existe um jogador com esse numero");
	}

}
