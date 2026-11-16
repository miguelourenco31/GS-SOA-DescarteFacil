# Descarte Fácil — SOA e Web Services

Global Solution FIAP 2026.2 · CitizenTech · Engenharia de Software

> **Onde jogo meu sofá velho?** O cidadão escolhe o item (sofá, entulho, geladeira velha) e o app mostra o
> ecoponto mais próximo, se ele está lotado agora (sensor na caçamba) e quando o Cata-Bagulho passa na rua.
> Os chamados do 156 alimentam um modelo de ML que prevê onde surgirá o próximo ponto de descarte irregular.

## Integrantes

| Nome | RM |
- Lorenzzo Vendruscolo Dias - RM558305
- Gabriel Martins Vannucci - RM556883
- Miguel Marques Lourenço - RM555426
- Pedro Henrique Ferronato - RM554757

## O problema

São Paulo tem 128 ecopontos e, mesmo assim, cerca de 1.400 pontos frequentes de descarte irregular — crime
ambiental com multa de até R$ 25 mil e prisão. Falta informação simples na hora em que o cidadão precisa descartar.

## O que este repositório entrega

Esta é a **camada de backend** da solução: uma API REST em Java 21 + Spring Boot que serve:

- o **app do cidadão** (busca de ecoponto por proximidade, lotação e rota do Cata-Bagulho);
- o **sensor da caçamba** (ESP32 + HC-SR04 envia o nível de enchimento via `POST`);
- o **modelo de ML** (consome as ocorrências do 156 e o resumo por bairro).

## Stack

| Item | Tecnologia |
|------|------------|
| Linguagem / framework | Java 21, Spring Boot 4.0.6 (Maven) |
| Persistência | Spring Data JPA (Hibernate) |
| Banco de dados | **MySQL 8** |
| Migrações | **Flyway** (`src/main/resources/db/migration`) |
| Documentação | **Swagger UI / OpenAPI** (springdoc) |
| Testes | JUnit 5, Mockito, MockMvc, H2 em memória (modo MySQL) |

## Arquitetura

```
controller  ->  service  ->  repository  ->  MySQL (schema versionado pelo Flyway)
   (REST/DTO)   (regras)     (Spring Data)
```

- `domain/` entidades JPA: `TipoResiduo`, `Ecoponto`, `Cacamba`, `LeituraCacamba`, `RotaCataBagulho`, `OcorrenciaDescarte`
- `dto/` records de entrada/saída com Bean Validation (a entidade nunca é exposta na API)
- `service/` regras de negócio (lotação, distância, próxima passagem do Cata-Bagulho)
- `exception/` tratamento global de erros padronizado (RFC 7807 / `ProblemDetail`)

### Modelo de dados

```
tipo_residuo 1───* cacamba *───1 ecoponto
                     │
                     1
                     │
                     *
              leitura_cacamba            rota_cata_bagulho      ocorrencia_descarte *───1 tipo_residuo
```

### Regras de negócio principais

- **Ecoponto mais próximo:** apenas ecopontos **ativos** que tenham caçamba do tipo de resíduo escolhido, ordenados
  pela distância (fórmula de Haversine).
- **Lotação:** usa a última leitura do sensor de cada caçamba daquele tipo. O ecoponto está **lotado** se o menor
  nível entre as caçambas for **≥ 90 %** (se uma caçamba ainda tem espaço, ele não está lotado). Sem leitura, `lotado` vem `null`.
- **Cata-Bagulho:** busca por bairro (e rua, opcional) e retorna a **próxima data de passagem**.
- **Integridade:** não é possível remover ecoponto com caçambas nem tipo de resíduo em uso (HTTP 409).

## Como rodar

### Pré-requisitos

- Java 21 ou superior
- Maven 3.9+
- MySQL 8 rodando localmente

### 1. Configurar o banco

O banco `descarte_facil` é criado automaticamente na primeira execução (`createDatabaseIfNotExist=true`).
Por padrão a API conecta em `localhost:3306` com usuário `root` e senha `root`. Se o seu MySQL for diferente,
use variáveis de ambiente (sem alterar o código):

```bash
# Linux / macOS
export DB_USER=meu_usuario
export DB_PASSWORD=minha_senha

# Windows (PowerShell)
$env:DB_USER="meu_usuario"; $env:DB_PASSWORD="minha_senha"
```

Variáveis disponíveis: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.

### 2. Subir a API

```bash
mvn spring-boot:run
```

Na primeira execução o Flyway cria as tabelas (`V1`) e carrega dados de exemplo (`V2`).

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI (JSON): http://localhost:8080/v3/api-docs
- Exemplos de chamadas prontas: [`docs/requests.http`](docs/requests.http) (extensão *REST Client* do VS Code)

### 3. Rodar os testes

```bash
mvn test
```

Os testes **não precisam do MySQL**: usam H2 em memória no modo MySQL, executando as mesmas migrations do Flyway.

## Migrações (Flyway)

| Versão | Arquivo | Conteúdo |
|--------|---------|----------|
| V1 | `V1__criar_schema.sql` | Tabelas, chaves estrangeiras, unicidade e índices |
| V2 | `V2__dados_iniciais.sql` | Dados de **exemplo** (tipos, ecopontos, caçambas, leituras, rotas e ocorrências) |

O Hibernate **não** altera o schema (`ddl-auto=none`): toda mudança de banco é uma nova migration (`V3__...`).
Os ecopontos e ocorrências da V2 são fictícios, só para demonstração; em produção seriam substituídos por
dados abertos da Prefeitura e do 156.

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/ecopontos/proximos?latitude&longitude&tipoResiduoId&limite` | Ecopontos mais próximos + lotação |
| GET / POST | `/api/ecopontos` | Lista / cadastra |
| GET / PUT / DELETE | `/api/ecopontos/{id}` | Consulta / atualiza / remove |
| GET / POST | `/api/tipos-residuo` | Lista / cadastra |
| GET / PUT / DELETE | `/api/tipos-residuo/{id}` | Consulta / atualiza / remove |
| GET / POST | `/api/cacambas` | Lista (filtro `ecopontoId`) / cadastra |
| GET / DELETE | `/api/cacambas/{id}` | Consulta / remove |
| POST | `/api/cacambas/{id}/leituras` | **Sensor** envia o nível de lotação (0–100) |
| GET | `/api/cacambas/{id}/leituras` | Últimas 20 leituras |
| GET | `/api/rotas-cata-bagulho/buscar?bairro&logradouro` | Quando o Cata-Bagulho passa na rua |
| GET / POST | `/api/rotas-cata-bagulho` | Lista / cadastra |
| GET / PUT / DELETE | `/api/rotas-cata-bagulho/{id}` | Consulta / atualiza / remove |
| GET / POST | `/api/ocorrencias` | Lista (filtro `bairro`) / registra chamado do 156 |
| GET | `/api/ocorrencias/resumo-por-bairro` | Total por bairro (insumo do ML) |
| GET / PUT / DELETE | `/api/ocorrencias/{id}` | Consulta / atualiza / remove |

### Exemplo: o sensor da caçamba (ESP32) enviando uma leitura

```http
POST /api/cacambas/1/leituras
Content-Type: application/json

{ "nivelPercentual": 92 }
```

### Padrão de erros

```json
{
  "title": "Dados inválidos",
  "status": 400,
  "detail": "Um ou mais campos são inválidos.",
  "erros": { "nome": "O nome é obrigatório" }
}
```

`400` validação · `404` não encontrado · `409` duplicidade ou recurso em uso.

## Critérios de avaliação × onde está no projeto

| Critério | Pontos | Onde |
|----------|--------|------|
| Estruturação do domínio | 3,0 | `domain/`, `dto/`, `service/`, `controller/`, `exception/` |
| Conexão com banco de dados | 3,0 | `application.properties` (MySQL) + `repository/` |
| Migrações | 1,5 | `src/main/resources/db/migration` (Flyway V1, V2) |
| Documentação automática | 1,5 | Swagger UI em `/swagger-ui.html` |
| Testes automatizados | 1,0 | `src/test` (unitários + integração) |

## Evidências da aplicação

Coloque aqui os prints (pasta `docs/evidencias/`):

- [ ] Swagger UI com todos os endpoints
- [ ] Tabelas criadas no MySQL e tabela `flyway_schema_history`
- [ ] `GET /api/ecopontos/proximos` mostrando `lotado: true` e `lotado: false`
- [ ] `POST /api/cacambas/{id}/leituras` e a busca de ecopontos mudando de resultado
- [ ] `GET /api/rotas-cata-bagulho/buscar`
- [ ] `GET /api/ocorrencias/resumo-por-bairro`
- [ ] Resultado do `mvn test` (BUILD SUCCESS)

## Integração com o restante da CitizenTech

- **App:** consome `/api/ecopontos/proximos` e `/api/rotas-cata-bagulho/buscar`.
- **IoT:** o ESP32 com sensor ultrassônico chama `POST /api/cacambas/{id}/leituras`.
- **Machine Learning:** lê `/api/ocorrencias` e `/api/ocorrencias/resumo-por-bairro` para prever novos pontos críticos.
