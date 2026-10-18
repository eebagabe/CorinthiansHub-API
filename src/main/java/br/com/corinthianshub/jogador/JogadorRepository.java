package br.com.corinthianshub.jogador;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JogadorRepository extends JpaRepository<Jogador, Long> {

	List<Jogador> findAllByOrderByNumero();

}
