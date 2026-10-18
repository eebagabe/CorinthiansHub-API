package br.com.corinthianshub.jogador;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/** Guarda fotos no Cloudflare R2 (ou qualquer S3 compativel, como MinIO em dev). */
@Component
public class ArmazenamentoFotos {

	public enum Formato {

		JPEG("jpg", "image/jpeg"), PNG("png", "image/png"), WEBP("webp", "image/webp");

		final String extensao;
		final String contentType;

		Formato(String extensao, String contentType) {
			this.extensao = extensao;
			this.contentType = contentType;
		}

	}

	private final S3Client s3;
	private final String bucket;
	private final String urlPublica;

	public ArmazenamentoFotos(@Value("${app.fotos.endpoint}") String endpoint,
			@Value("${app.fotos.access-key}") String accessKey, @Value("${app.fotos.secret-key}") String secretKey,
			@Value("${app.fotos.bucket}") String bucket, @Value("${app.fotos.url-publica}") String urlPublica) {
		this.bucket = bucket;
		this.urlPublica = urlPublica.replaceAll("/+$", "");
		this.s3 = bucket.isBlank() ? null
				: S3Client.builder()
					.endpointOverride(URI.create(endpoint))
					.region(Region.of("auto"))
					.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
					.forcePathStyle(true)
					.build();
	}

	public boolean configurado() {
		return s3 != null;
	}

	/** Identifica o formato pelos bytes iniciais, sem confiar no nome ou content-type enviados. */
	static Optional<Formato> detectar(byte[] b) {
		if (b.length > 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
			return Optional.of(Formato.JPEG);
		}
		if (b.length > 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
			return Optional.of(Formato.PNG);
		}
		if (b.length > 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F' && b[8] == 'W' && b[9] == 'E'
				&& b[10] == 'B' && b[11] == 'P') {
			return Optional.of(Formato.WEBP);
		}
		return Optional.empty();
	}

	public String salvar(long jogadorId, byte[] conteudo, Formato formato) {
		String chave = "jogadores/%d/%s.%s".formatted(jogadorId, UUID.randomUUID(), formato.extensao);
		s3.putObject(r -> r.bucket(bucket)
			.key(chave)
			.contentType(formato.contentType)
			.cacheControl("public, max-age=31536000, immutable"), RequestBody.fromBytes(conteudo));
		return urlPublica + "/" + chave;
	}

	/** Remove uma foto antiga, se ela for deste armazenamento. */
	public void remover(String url) {
		if (url != null && url.startsWith(urlPublica + "/")) {
			s3.deleteObject(r -> r.bucket(bucket).key(url.substring(urlPublica.length() + 1)));
		}
	}

}
