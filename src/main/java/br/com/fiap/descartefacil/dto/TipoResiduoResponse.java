package br.com.fiap.descartefacil.dto;

import br.com.fiap.descartefacil.domain.TipoResiduo;

public record TipoResiduoResponse(Long id, String nome, String descricao) {

    public static TipoResiduoResponse from(TipoResiduo t) {
        return new TipoResiduoResponse(t.getId(), t.getNome(), t.getDescricao());
    }
}
