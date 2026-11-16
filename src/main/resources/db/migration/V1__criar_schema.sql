-- Descarte Facil - schema inicial (compativel com MySQL 8 e H2 em modo MySQL)

CREATE TABLE tipo_residuo (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nome      VARCHAR(60)  NOT NULL,
    descricao VARCHAR(255),
    CONSTRAINT pk_tipo_residuo PRIMARY KEY (id),
    CONSTRAINT uk_tipo_residuo_nome UNIQUE (nome)
);

CREATE TABLE ecoponto (
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    nome                   VARCHAR(120) NOT NULL,
    endereco               VARCHAR(200) NOT NULL,
    bairro                 VARCHAR(80)  NOT NULL,
    latitude               DOUBLE       NOT NULL,
    longitude              DOUBLE       NOT NULL,
    ativo                  BOOLEAN      NOT NULL,
    horario_funcionamento  VARCHAR(100),
    CONSTRAINT pk_ecoponto PRIMARY KEY (id)
);

CREATE TABLE cacamba (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    ecoponto_id      BIGINT      NOT NULL,
    tipo_residuo_id  BIGINT      NOT NULL,
    identificador    VARCHAR(40) NOT NULL,
    capacidade_m3    DOUBLE,
    CONSTRAINT pk_cacamba PRIMARY KEY (id),
    CONSTRAINT uk_cacamba_identificador UNIQUE (identificador),
    CONSTRAINT fk_cacamba_ecoponto FOREIGN KEY (ecoponto_id) REFERENCES ecoponto (id),
    CONSTRAINT fk_cacamba_tipo_residuo FOREIGN KEY (tipo_residuo_id) REFERENCES tipo_residuo (id)
);

CREATE TABLE leitura_cacamba (
    id                BIGINT   NOT NULL AUTO_INCREMENT,
    cacamba_id        BIGINT   NOT NULL,
    nivel_percentual  INT      NOT NULL,
    registrada_em     DATETIME NOT NULL,
    CONSTRAINT pk_leitura_cacamba PRIMARY KEY (id),
    CONSTRAINT fk_leitura_cacamba FOREIGN KEY (cacamba_id) REFERENCES cacamba (id) ON DELETE CASCADE
);

CREATE INDEX idx_leitura_cacamba_data ON leitura_cacamba (cacamba_id, registrada_em);

CREATE TABLE rota_cata_bagulho (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    bairro       VARCHAR(80)  NOT NULL,
    logradouro   VARCHAR(160) NOT NULL,
    dia_semana   VARCHAR(12)  NOT NULL,
    hora_inicio  TIME         NOT NULL,
    hora_fim     TIME         NOT NULL,
    CONSTRAINT pk_rota_cata_bagulho PRIMARY KEY (id)
);

CREATE INDEX idx_rota_bairro ON rota_cata_bagulho (bairro);

CREATE TABLE ocorrencia_descarte (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    protocolo        VARCHAR(40)  NOT NULL,
    bairro           VARCHAR(80)  NOT NULL,
    logradouro       VARCHAR(160),
    latitude         DOUBLE       NOT NULL,
    longitude        DOUBLE       NOT NULL,
    tipo_residuo_id  BIGINT       NOT NULL,
    status           VARCHAR(20)  NOT NULL,
    data_abertura    DATETIME     NOT NULL,
    CONSTRAINT pk_ocorrencia_descarte PRIMARY KEY (id),
    CONSTRAINT uk_ocorrencia_protocolo UNIQUE (protocolo),
    CONSTRAINT fk_ocorrencia_tipo_residuo FOREIGN KEY (tipo_residuo_id) REFERENCES tipo_residuo (id)
);

CREATE INDEX idx_ocorrencia_bairro ON ocorrencia_descarte (bairro);
