package br.com.fiap.descartefacil.integracao;

import br.com.fiap.descartefacil.repository.CacambaRepository;
import br.com.fiap.descartefacil.repository.TipoResiduoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Testes de integração: API completa + Flyway + banco H2 (modo MySQL), cada teste com rollback. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ApiIntegracaoTest {

    private static final double PINHEIROS_LAT = -23.5670;
    private static final double PINHEIROS_LON = -46.7020;

    @Autowired WebApplicationContext context;
    @Autowired TipoResiduoRepository tipoResiduoRepository;
    @Autowired CacambaRepository cacambaRepository;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    // ---------- Tipos de resíduo ----------

    @Test
    void listaOsTresTiposDaCargaInicial() throws Exception {
        mvc.perform(get("/api/tipos-residuo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void criaTipoDeResiduo() throws Exception {
        mvc.perform(post("/api/tipos-residuo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Colchao\",\"descricao\":\"Colchoes usados\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Colchao"));
    }

    @Test
    void rejeitaTipoSemNomeComDetalheDoCampo() throws Exception {
        mvc.perform(post("/api/tipos-residuo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists());
    }

    @Test
    void rejeitaTipoDuplicadoComConflito() throws Exception {
        mvc.perform(post("/api/tipos-residuo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Entulho\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void naoRemoveTipoEmUso() throws Exception {
        mvc.perform(delete("/api/tipos-residuo/{id}", idDoTipo("Entulho")))
                .andExpect(status().isConflict());
    }

    // ---------- Ecopontos ----------

    @Test
    void ecopontoInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/ecopontos/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void criaEcopontoAtivoPorPadrao() throws Exception {
        mvc.perform(post("/api/ecopontos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ecoponto Teste\",\"endereco\":\"Rua A, 1\",\"bairro\":\"Centro\","
                                + "\"latitude\":-23.55,\"longitude\":-46.63}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void rejeitaEcopontoComLatitudeInvalida() throws Exception {
        mvc.perform(post("/api/ecopontos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"X\",\"endereco\":\"Rua\",\"bairro\":\"Centro\","
                                + "\"latitude\":200,\"longitude\":-46.63}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.latitude").exists());
    }

    @Test
    void naoRemoveEcopontoComCacambas() throws Exception {
        Long ecopontoId = cacambaRepository.findByIdentificador("CAC-PIN-01").orElseThrow()
                .getEcoponto().getId();

        mvc.perform(delete("/api/ecopontos/{id}", ecopontoId))
                .andExpect(status().isConflict());
    }

    // ---------- Busca por proximidade + lotação ----------

    @Test
    void ecopontoMaisProximoParaSofaEstaLotadoSegundoOSensor() throws Exception {
        mvc.perform(get("/api/ecopontos/proximos")
                        .param("latitude", String.valueOf(PINHEIROS_LAT))
                        .param("longitude", String.valueOf(PINHEIROS_LON))
                        .param("tipoResiduoId", String.valueOf(idDoTipoPrefixo("Sof")))
                        .param("limite", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].nome").value(containsString("Pinheiros")))
                .andExpect(jsonPath("$[0].distanciaKm").value(0.0))
                .andExpect(jsonPath("$[0].lotado").value(true))
                .andExpect(jsonPath("$[0].nivelPercentual").value(95));
    }

    @Test
    void novaLeituraDoSensorLiberaOEcoponto() throws Exception {
        Long cacambaId = cacambaRepository.findByIdentificador("CAC-PIN-01").orElseThrow().getId();

        mvc.perform(post("/api/cacambas/{id}/leituras", cacambaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nivelPercentual\":10,\"registradaEm\":\"2026-11-13T09:00:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nivelPercentual").value(10));

        mvc.perform(get("/api/ecopontos/proximos")
                        .param("latitude", String.valueOf(PINHEIROS_LAT))
                        .param("longitude", String.valueOf(PINHEIROS_LON))
                        .param("tipoResiduoId", String.valueOf(idDoTipoPrefixo("Sof"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lotado").value(false))
                .andExpect(jsonPath("$[0].nivelPercentual").value(10));
    }

    @Test
    void rejeitaLeituraComNivelAcimaDe100() throws Exception {
        Long cacambaId = cacambaRepository.findByIdentificador("CAC-PIN-01").orElseThrow().getId();

        mvc.perform(post("/api/cacambas/{id}/leituras", cacambaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nivelPercentual\":150}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nivelPercentual").exists());
    }

    @Test
    void buscaProximosComTipoInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/ecopontos/proximos")
                        .param("latitude", "-23.55")
                        .param("longitude", "-46.63")
                        .param("tipoResiduoId", "999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscaProximosSemParametroObrigatorioRetorna400() throws Exception {
        mvc.perform(get("/api/ecopontos/proximos").param("latitude", "-23.55"))
                .andExpect(status().isBadRequest());
    }

    // ---------- Cata-Bagulho ----------

    @Test
    void buscaRotasPorBairroEPorRua() throws Exception {
        mvc.perform(get("/api/rotas-cata-bagulho/buscar").param("bairro", "Mooca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mvc.perform(get("/api/rotas-cata-bagulho/buscar")
                        .param("bairro", "Mooca").param("logradouro", "Taquari"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].diaSemana").value("FRIDAY"))
                .andExpect(jsonPath("$[0].proximaPassagem").exists());
    }

    @Test
    void rejeitaRotaComHorarioFimAntesDoInicio() throws Exception {
        mvc.perform(post("/api/rotas-cata-bagulho")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bairro\":\"Mooca\",\"logradouro\":\"Rua X\",\"diaSemana\":\"MONDAY\","
                                + "\"horaInicio\":\"12:00:00\",\"horaFim\":\"08:00:00\"}"))
                .andExpect(status().isBadRequest());
    }

    // ---------- Ocorrências (dados do 156 para o ML) ----------

    @Test
    void resumoPorBairroOrdenaDoMaisCriticoParaOMenos() throws Exception {
        mvc.perform(get("/api/ocorrencias/resumo-por-bairro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bairro").value("Mooca"))
                .andExpect(jsonPath("$[0].total").value(3))
                .andExpect(jsonPath("$[1].bairro").value("Santana"))
                .andExpect(jsonPath("$[1].total").value(2));
    }

    @Test
    void criaOcorrenciaComStatusAbertaPorPadrao() throws Exception {
        mvc.perform(post("/api/ocorrencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"protocolo\":\"156-2026-9999\",\"bairro\":\"Mooca\",\"latitude\":-23.56,"
                                + "\"longitude\":-46.60,\"tipoResiduoId\":" + idDoTipo("Entulho") + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ABERTA"));
    }

    @Test
    void rejeitaOcorrenciaComProtocoloDuplicado() throws Exception {
        mvc.perform(post("/api/ocorrencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"protocolo\":\"156-2026-0001\",\"bairro\":\"Mooca\",\"latitude\":-23.56,"
                                + "\"longitude\":-46.60,\"tipoResiduoId\":" + idDoTipo("Entulho") + "}"))
                .andExpect(status().isConflict());
    }

    // ---------- Documentação ----------

    @Test
    void documentacaoOpenApiEstaDisponivel() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.paths['/api/ecopontos/proximos']").exists());
    }

    // ---------- helpers ----------

    private Long idDoTipo(String nome) {
        return tipoResiduoRepository.findByNomeIgnoreCase(nome).orElseThrow().getId();
    }

    private Long idDoTipoPrefixo(String prefixo) {
        return tipoResiduoRepository.findAll().stream()
                .filter(t -> t.getNome().startsWith(prefixo))
                .findFirst().orElseThrow().getId();
    }
}
