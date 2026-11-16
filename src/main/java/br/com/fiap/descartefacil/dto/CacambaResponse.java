package br.com.fiap.descartefacil.dto;

import br.com.fiap.descartefacil.domain.Cacamba;

public record CacambaResponse(Long id, Long ecopontoId, Long tipoResiduoId, String tipoResiduo,
                              String identificador, Double capacidadeM3) {

    public static CacambaResponse from(Cacamba c) {
        return new CacambaResponse(c.getId(), c.getEcoponto().getId(), c.getTipoResiduo().getId(),
                c.getTipoResiduo().getNome(), c.getIdentificador(), c.getCapacidadeM3());
    }
}
