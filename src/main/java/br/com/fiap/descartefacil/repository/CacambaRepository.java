package br.com.fiap.descartefacil.repository;

import br.com.fiap.descartefacil.domain.Cacamba;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CacambaRepository extends JpaRepository<Cacamba, Long> {

    List<Cacamba> findByEcopontoId(Long ecopontoId);

    List<Cacamba> findByEcopontoIdAndTipoResiduoId(Long ecopontoId, Long tipoResiduoId);

    Optional<Cacamba> findByIdentificador(String identificador);

    boolean existsByIdentificador(String identificador);

    boolean existsByEcopontoId(Long ecopontoId);

    boolean existsByTipoResiduoId(Long tipoResiduoId);
}
