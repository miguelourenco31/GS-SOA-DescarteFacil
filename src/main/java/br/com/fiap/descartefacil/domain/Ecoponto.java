package br.com.fiap.descartefacil.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "ecoponto")
public class Ecoponto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 200)
    private String endereco;

    @Column(nullable = false, length = 80)
    private String bairro;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "horario_funcionamento", length = 100)
    private String horarioFuncionamento;

    protected Ecoponto() {
    }

    public Ecoponto(String nome, String endereco, String bairro, double latitude, double longitude,
                    boolean ativo, String horarioFuncionamento) {
        atualizar(nome, endereco, bairro, latitude, longitude, ativo, horarioFuncionamento);
    }

    public void atualizar(String nome, String endereco, String bairro, double latitude, double longitude,
                          boolean ativo, String horarioFuncionamento) {
        this.nome = nome;
        this.endereco = endereco;
        this.bairro = bairro;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ativo = ativo;
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public String getBairro() { return bairro; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isAtivo() { return ativo; }
    public String getHorarioFuncionamento() { return horarioFuncionamento; }
}
