package br.com.fiap.descartefacil.service;

import br.com.fiap.descartefacil.domain.RotaCataBagulho;
import br.com.fiap.descartefacil.dto.RotaResponse;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class RotaResponseTest {

    private final RotaCataBagulho rotaSexta = new RotaCataBagulho("Mooca", "Rua Taquari",
            DayOfWeek.FRIDAY, LocalTime.of(13, 0), LocalTime.of(17, 0));

    @Test
    void proximaPassagemEmOutroDiaDaSemana() {
        // 2026-11-13 é uma sexta; a partir de segunda (2026-11-09) a próxima sexta é 2026-11-13
        RotaResponse r = RotaResponse.from(rotaSexta, LocalDate.of(2026, 11, 9));
        assertThat(r.proximaPassagem()).isEqualTo(LocalDate.of(2026, 11, 13));
    }

    @Test
    void proximaPassagemInclueHoje() {
        RotaResponse r = RotaResponse.from(rotaSexta, LocalDate.of(2026, 11, 13));
        assertThat(r.proximaPassagem()).isEqualTo(LocalDate.of(2026, 11, 13));
    }

    @Test
    void proximaPassagemNaSemanaSeguinteQuandoJaPassou() {
        // sábado 2026-11-14 -> próxima sexta é 2026-11-20
        RotaResponse r = RotaResponse.from(rotaSexta, LocalDate.of(2026, 11, 14));
        assertThat(r.proximaPassagem()).isEqualTo(LocalDate.of(2026, 11, 20));
    }
}
