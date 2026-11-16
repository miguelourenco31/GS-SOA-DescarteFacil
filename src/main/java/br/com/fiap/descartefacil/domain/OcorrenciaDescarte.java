package br.com.fiap.descartefacil.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Ocorrência de descarte irregular (origem: chamados do 156). Alimenta o modelo de ML. */
@Entity
@Table(name = "ocorrencia_descarte")
public class OcorrenciaDescarte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String protocolo;

    @Column(nullable = false, length = 80)
    private String bairro;

    @Column(length = 160)
    private String logradouro;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_residuo_id", nullable = false)
    private TipoResiduo tipoResiduo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOcorrencia status;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    protected OcorrenciaDescarte() {
    }

    public OcorrenciaDescarte(String protocolo, String bairro, String logradouro, double latitude,
                              double longitude, TipoResiduo tipoResiduo, StatusOcorrencia status,
                              LocalDateTime dataAbertura) {
        this.protocolo = protocolo;
        atualizar(bairro, logradouro, latitude, longitude, tipoResiduo, status, dataAbertura);
    }

    public void atualizar(String bairro, String logradouro, double latitude, double longitude,
                          TipoResiduo tipoResiduo, StatusOcorrencia status, LocalDateTime dataAbertura) {
        this.bairro = bairro;
        this.logradouro = logradouro;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tipoResiduo = tipoResiduo;
        this.status = status;
        this.dataAbertura = dataAbertura;
    }

    public Long getId() { return id; }
    public String getProtocolo() { return protocolo; }
    public String getBairro() { return bairro; }
    public String getLogradouro() { return logradouro; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public TipoResiduo getTipoResiduo() { return tipoResiduo; }
    public StatusOcorrencia getStatus() { return status; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
}
