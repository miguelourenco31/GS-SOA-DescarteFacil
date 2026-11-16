package br.com.fiap.descartefacil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record RotaRequest(
        @NotBlank(message = "O bairro é obrigatório")
        @Size(max = 80, message = "O bairro deve ter no máximo 80 caracteres")
        String bairro,

        @NotBlank(message = "O logradouro é obrigatório")
        @Size(max = 160, message = "O logradouro deve ter no máximo 160 caracteres")
        String logradouro,

        @NotNull(message = "O dia da semana é obrigatório (ex.: MONDAY)")
        DayOfWeek diaSemana,

        @NotNull(message = "A hora de início é obrigatória (ex.: 08:00:00)")
        LocalTime horaInicio,

        @NotNull(message = "A hora de fim é obrigatória (ex.: 12:00:00)")
        LocalTime horaFim) {
}
