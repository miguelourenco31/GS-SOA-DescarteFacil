package br.com.fiap.descartefacil.service;

import br.com.fiap.descartefacil.domain.Ecoponto;
import br.com.fiap.descartefacil.domain.LeituraCacamba;
import br.com.fiap.descartefacil.dto.EcopontoProximoResponse;
import br.com.fiap.descartefacil.dto.EcopontoRequest;
import br.com.fiap.descartefacil.dto.EcopontoResponse;
import br.com.fiap.descartefacil.exception.RecursoNaoEncontradoException;
import br.com.fiap.descartefacil.exception.RegraNegocioException;
import br.com.fiap.descartefacil.exception.RequisicaoInvalidaException;
import br.com.fiap.descartefacil.repository.CacambaRepository;
import br.com.fiap.descartefacil.repository.EcopontoRepository;
import br.com.fiap.descartefacil.repository.LeituraCacambaRepository;
import br.com.fiap.descartefacil.repository.TipoResiduoRepository;
import br.com.fiap.descartefacil.util.GeoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EcopontoService {

    /** A partir deste nível (%) a caçamba é considerada lotada. */
    public static final int LIMITE_LOTACAO_PERCENTUAL = 90;
    private static final int LIMITE_MAXIMO_RESULTADOS = 20;

    private final EcopontoRepository ecopontoRepository;
    private final CacambaRepository cacambaRepository;
    private final LeituraCacambaRepository leituraRepository;
    private final TipoResiduoRepository tipoResiduoRepository;

    public EcopontoService(EcopontoRepository ecopontoRepository, CacambaRepository cacambaRepository,
                           LeituraCacambaRepository leituraRepository,
                           TipoResiduoRepository tipoResiduoRepository) {
        this.ecopontoRepository = ecopontoRepository;
        this.cacambaRepository = cacambaRepository;
        this.leituraRepository = leituraRepository;
        this.tipoResiduoRepository = tipoResiduoRepository;
    }

    public List<EcopontoResponse> listar() {
        return ecopontoRepository.findAll().stream().map(EcopontoResponse::from).toList();
    }

    public EcopontoResponse buscar(Long id) {
        return EcopontoResponse.from(obter(id));
    }

    @Transactional
    public EcopontoResponse criar(EcopontoRequest r) {
        Ecoponto ecoponto = new Ecoponto(r.nome(), r.endereco(), r.bairro(), r.latitude(), r.longitude(),
                r.ativo() == null || r.ativo(), r.horarioFuncionamento());
        return EcopontoResponse.from(ecopontoRepository.save(ecoponto));
    }

    @Transactional
    public EcopontoResponse atualizar(Long id, EcopontoRequest r) {
        Ecoponto ecoponto = obter(id);
        ecoponto.atualizar(r.nome(), r.endereco(), r.bairro(), r.latitude(), r.longitude(),
                r.ativo() == null || r.ativo(), r.horarioFuncionamento());
        return EcopontoResponse.from(ecopontoRepository.save(ecoponto));
    }

    @Transactional
    public void remover(Long id) {
        Ecoponto ecoponto = obter(id);
        if (cacambaRepository.existsByEcopontoId(id)) {
            throw new RegraNegocioException("Ecoponto possui caçambas cadastradas; remova-as antes");
        }
        ecopontoRepository.delete(ecoponto);
    }

    /**
     * Ecopontos ativos que aceitam o tipo de resíduo, ordenados pela distância até o cidadão,
     * já com a situação de lotação (último dado do sensor da caçamba).
     */
    public List<EcopontoProximoResponse> buscarProximos(double latitude, double longitude,
                                                         Long tipoResiduoId, int limite) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new RequisicaoInvalidaException("Latitude deve estar entre -90 e 90 e longitude entre -180 e 180");
        }
        if (limite < 1 || limite > LIMITE_MAXIMO_RESULTADOS) {
            throw new RequisicaoInvalidaException("O limite deve estar entre 1 e " + LIMITE_MAXIMO_RESULTADOS);
        }
        tipoResiduoRepository.findById(tipoResiduoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Tipo de resíduo", tipoResiduoId));

        return ecopontoRepository.findAtivosComCacambaDoTipo(tipoResiduoId).stream()
                .map(e -> montarProximo(e, latitude, longitude, tipoResiduoId))
                .sorted(Comparator.comparingDouble(EcopontoProximoResponse::distanciaKm))
                .limit(limite)
                .toList();
    }

    private EcopontoProximoResponse montarProximo(Ecoponto e, double lat, double lon, Long tipoResiduoId) {
        double distancia = Math.round(GeoUtils.distanciaKm(lat, lon, e.getLatitude(), e.getLongitude()) * 100.0) / 100.0;

        List<LeituraCacamba> leituras = cacambaRepository
                .findByEcopontoIdAndTipoResiduoId(e.getId(), tipoResiduoId).stream()
                .map(c -> leituraRepository.findFirstByCacambaIdOrderByRegistradaEmDescIdDesc(c.getId()))
                .flatMap(Optional::stream)
                .toList();

        if (leituras.isEmpty()) {
            return new EcopontoProximoResponse(e.getId(), e.getNome(), e.getEndereco(), e.getBairro(),
                    e.getLatitude(), e.getLongitude(), distancia, null, null, null);
        }

        // O ecoponto só está lotado se TODAS as caçambas daquele tipo estiverem cheias:
        // usamos o menor nível, ou seja, a caçamba com mais espaço livre.
        int nivel = leituras.stream().mapToInt(LeituraCacamba::getNivelPercentual).min().orElseThrow();
        LocalDateTime ultima = leituras.stream().map(LeituraCacamba::getRegistradaEm)
                .max(Comparator.naturalOrder()).orElseThrow();

        return new EcopontoProximoResponse(e.getId(), e.getNome(), e.getEndereco(), e.getBairro(),
                e.getLatitude(), e.getLongitude(), distancia, nivel >= LIMITE_LOTACAO_PERCENTUAL, nivel, ultima);
    }

    private Ecoponto obter(Long id) {
        return ecopontoRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Ecoponto", id));
    }
}
