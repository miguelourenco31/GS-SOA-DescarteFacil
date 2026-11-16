package br.com.fiap.descartefacil.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Leitura de nível de enchimento enviada pelo sensor da caçamba (ex.: ESP32 + HC-SR04). */
@Entity
@Table(name = "leitura_cacamba")
public class LeituraCacamba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cacamba_id", nullable = false)
    private Cacamba cacamba;

    @Column(name = "nivel_percentual", nullable = false)
    private int nivelPercentual;

    @Column(name = "registrada_em", nullable = false)
    private LocalDateTime registradaEm;

    protected LeituraCacamba() {
    }

    public LeituraCacamba(Cacamba cacamba, int nivelPercentual, LocalDateTime registradaEm) {
        this.cacamba = cacamba;
        this.nivelPercentual = nivelPercentual;
        this.registradaEm = registradaEm;
    }

    public Long getId() { return id; }
    public Cacamba getCacamba() { return cacamba; }
    public int getNivelPercentual() { return nivelPercentual; }
    public LocalDateTime getRegistradaEm() { return registradaEm; }
}
