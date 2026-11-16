package br.com.fiap.descartefacil.domain;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "rota_cata_bagulho")
public class RotaCataBagulho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String bairro;

    @Column(nullable = false, length = 160)
    private String logradouro;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 12)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    protected RotaCataBagulho() {
    }

    public RotaCataBagulho(String bairro, String logradouro, DayOfWeek diaSemana,
                           LocalTime horaInicio, LocalTime horaFim) {
        atualizar(bairro, logradouro, diaSemana, horaInicio, horaFim);
    }

    public void atualizar(String bairro, String logradouro, DayOfWeek diaSemana,
                          LocalTime horaInicio, LocalTime horaFim) {
        this.bairro = bairro;
        this.logradouro = logradouro;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public Long getId() { return id; }
    public String getBairro() { return bairro; }
    public String getLogradouro() { return logradouro; }
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
}
