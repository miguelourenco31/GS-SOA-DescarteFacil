package br.com.fiap.descartefacil.repository;

import br.com.fiap.descartefacil.domain.OcorrenciaDescarte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OcorrenciaDescarteRepository extends JpaRepository<OcorrenciaDescarte, Long> {

    boolean existsByProtocolo(String protocolo);

    boolean existsByTipoResiduoId(Long tipoResiduoId);

    List<OcorrenciaDescarte> findByBairroIgnoreCase(String bairro);

    interface ResumoBairro {
        String getBairro();

        long getTotal();
    }

    /** Total de ocorrências por bairro, do mais crítico para o menos crítico. */
    @Query("select o.bairro as bairro, count(o) as total from OcorrenciaDescarte o "
            + "group by o.bairro order by count(o) desc, o.bairro asc")
    List<ResumoBairro> resumoPorBairro();
}
