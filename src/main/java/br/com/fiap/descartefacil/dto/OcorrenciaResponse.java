package br.com.fiap.descartefacil.dto;

import br.com.fiap.descartefacil.domain.OcorrenciaDescarte;
import br.com.fiap.descartefacil.domain.StatusOcorrencia;

import java.time.LocalDateTime;

public record OcorrenciaResponse(Long id, String protocolo, String bairro, String logradouro,
                                 double latitude, double longitude, Long tipoResiduoId,
                                 String tipoResiduo, StatusOcorrencia status,
                                 LocalDateTime dataAbertura) {

    public static OcorrenciaResponse from(OcorrenciaDescarte o) {
        return new OcorrenciaResponse(o.getId(), o.getProtocolo(), o.getBairro(), o.getLogradouro(),
                o.getLatitude(), o.getLongitude(), o.getTipoResiduo().getId(),
                o.getTipoResiduo().getNome(), o.getStatus(), o.getDataAbertura());
    }
}
