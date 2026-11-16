package br.com.fiap.descartefacil.dto;

import br.com.fiap.descartefacil.domain.RotaCataBagulho;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

/** {@code proximaPassagem} é a próxima data (hoje inclusive) em que o Cata-Bagulho passa na rua. */
public record RotaResponse(Long id, String bairro, String logradouro, DayOfWeek diaSemana,
                           LocalTime horaInicio, LocalTime horaFim, LocalDate proximaPassagem) {

    public static RotaResponse from(RotaCataBagulho r, LocalDate hoje) {
        return new RotaResponse(r.getId(), r.getBairro(), r.getLogradouro(), r.getDiaSemana(),
                r.getHoraInicio(), r.getHoraFim(), hoje.with(TemporalAdjusters.nextOrSame(r.getDiaSemana())));
    }
}
