package br.com.fiap.descartefacil.repository;

import br.com.fiap.descartefacil.domain.TipoResiduo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoResiduoRepository extends JpaRepository<TipoResiduo, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Optional<TipoResiduo> findByNomeIgnoreCase(String nome);
}
