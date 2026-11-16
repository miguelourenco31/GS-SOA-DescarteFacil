package br.com.fiap.descartefacil.dto;

import br.com.fiap.descartefacil.domain.Ecoponto;

public record EcopontoResponse(Long id, String nome, String endereco, String bairro,
                               double latitude, double longitude, boolean ativo,
                               String horarioFuncionamento) {

    public static EcopontoResponse from(Ecoponto e) {
        return new EcopontoResponse(e.getId(), e.getNome(), e.getEndereco(), e.getBairro(),
                e.getLatitude(), e.getLongitude(), e.isAtivo(), e.getHorarioFuncionamento());
    }
}
