package br.com.fiap.descartefacil.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "cacamba")
public class Cacamba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ecoponto_id", nullable = false)
    private Ecoponto ecoponto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_residuo_id", nullable = false)
    private TipoResiduo tipoResiduo;

    @Column(nullable = false, unique = true, length = 40)
    private String identificador;

    @Column(name = "capacidade_m3")
    private Double capacidadeM3;

    protected Cacamba() {
    }

    public Cacamba(Ecoponto ecoponto, TipoResiduo tipoResiduo, String identificador, Double capacidadeM3) {
        this.ecoponto = ecoponto;
        this.tipoResiduo = tipoResiduo;
        this.identificador = identificador;
        this.capacidadeM3 = capacidadeM3;
    }

    public Long getId() { return id; }
    public Ecoponto getEcoponto() { return ecoponto; }
    public TipoResiduo getTipoResiduo() { return tipoResiduo; }
    public String getIdentificador() { return identificador; }
    public Double getCapacidadeM3() { return capacidadeM3; }
}
