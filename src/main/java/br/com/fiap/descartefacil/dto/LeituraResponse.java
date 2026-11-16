package br.com.fiap.descartefacil.dto;

import br.com.fiap.descartefacil.domain.LeituraCacamba;

import java.time.LocalDateTime;

public record LeituraResponse(Long id, Long cacambaId, int nivelPercentual, LocalDateTime registradaEm) {

    public static LeituraResponse from(LeituraCacamba l) {
        return new LeituraResponse(l.getId(), l.getCacamba().getId(), l.getNivelPercentual(),
                l.getRegistradaEm());
    }
}
