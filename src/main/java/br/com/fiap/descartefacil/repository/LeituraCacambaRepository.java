package br.com.fiap.descartefacil.repository;

import br.com.fiap.descartefacil.domain.LeituraCacamba;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeituraCacambaRepository extends JpaRepository<LeituraCacamba, Long> {

    Optional<LeituraCacamba> findFirstByCacambaIdOrderByRegistradaEmDescIdDesc(Long cacambaId);

    List<LeituraCacamba> findTop20ByCacambaIdOrderByRegistradaEmDescIdDesc(Long cacambaId);
}
