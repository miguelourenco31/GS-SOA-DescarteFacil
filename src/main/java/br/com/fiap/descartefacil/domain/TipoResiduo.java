package br.com.fiap.descartefacil.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_residuo")
public class TipoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nome;

    @Column(length = 255)
    private String descricao;

    protected TipoResiduo() {
    }

    public TipoResiduo(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public void atualizar(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
}
