package br.com.corinthianshub.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;

	@Column(name = "senha_hash")
	private String senhaHash;

	private String papel;

	protected Usuario() {
	}

	public Usuario(String email, String senhaHash, String papel) {
		this.email = email;
		this.senhaHash = senhaHash;
		this.papel = papel;
	}

	public String getEmail() {
		return email;
	}

	public String getSenhaHash() {
		return senhaHash;
	}

	public String getPapel() {
		return papel;
	}

}
