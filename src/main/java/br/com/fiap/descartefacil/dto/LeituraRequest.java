package br.com.fiap.descartefacil.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record LeituraRequest(
        @NotNull(message = "O nível é obrigatório")
        @Min(value = 0, message = "O nível deve estar entre 0 e 100")
        @Max(value = 100, message = "O nível deve estar entre 0 e 100")
        Integer nivelPercentual,

        /** Opcional: se omitido, usa o horário atual do servidor. */
        LocalDateTime registradaEm) {
}
