package br.com.fiap.descartefacil.repository;

import br.com.fiap.descartefacil.domain.Ecoponto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EcopontoRepository extends JpaRepository<Ecoponto, Long> {

    /** Ecopontos ativos que possuem ao menos uma caçamba do tipo de resíduo informado. */
    @Query("select distinct c.ecoponto from Cacamba c "
            + "where c.tipoResiduo.id = :tipoResiduoId and c.ecoponto.ativo = true")
    List<Ecoponto> findAtivosComCacambaDoTipo(@Param("tipoResiduoId") Long tipoResiduoId);
}
