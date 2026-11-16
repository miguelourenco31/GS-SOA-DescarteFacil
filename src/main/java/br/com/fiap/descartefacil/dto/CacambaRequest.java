package br.com.fiap.descartefacil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CacambaRequest(
        @NotNull(message = "O ecoponto é obrigatório")
        Long ecopontoId,

        @NotNull(message = "O tipo de resíduo é obrigatório")
        Long tipoResiduoId,

        @NotBlank(message = "O identificador é obrigatório")
        @Size(max = 40, message = "O identificador deve ter no máximo 40 caracteres")
        String identificador,

        @Positive(message = "A capacidade deve ser maior que zero")
        Double capacidadeM3) {
}
