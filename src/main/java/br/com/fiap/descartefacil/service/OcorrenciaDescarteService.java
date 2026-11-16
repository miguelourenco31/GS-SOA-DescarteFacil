package br.com.fiap.descartefacil.service;

import br.com.fiap.descartefacil.domain.OcorrenciaDescarte;
import br.com.fiap.descartefacil.domain.StatusOcorrencia;
import br.com.fiap.descartefacil.domain.TipoResiduo;
import br.com.fiap.descartefacil.dto.OcorrenciaRequest;
import br.com.fiap.descartefacil.dto.OcorrenciaResponse;
import br.com.fiap.descartefacil.dto.ResumoBairroResponse;
import br.com.fiap.descartefacil.exception.RecursoNaoEncontradoException;
import br.com.fiap.descartefacil.exception.RegraNegocioException;
import br.com.fiap.descartefacil.repository.OcorrenciaDescarteRepository;
import br.com.fiap.descartefacil.repository.TipoResiduoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OcorrenciaDescarteService {

    private final OcorrenciaDescarteRepository repository;
    private final TipoResiduoRepository tipoResiduoRepository;

    public OcorrenciaDescarteService(OcorrenciaDescarteRepository repository,
                                     TipoResiduoRepository tipoResiduoRepository) {
        this.repository = repository;
        this.tipoResiduoRepository = tipoResiduoRepository;
    }

    public List<OcorrenciaResponse> listar(String bairro) {
        List<OcorrenciaDescarte> ocorrencias = (bairro == null || bairro.isBlank())
                ? repository.findAll()
                : repository.findByBairroIgnoreCase(bairro);
        return ocorrencias.stream().map(OcorrenciaResponse::from).toList();
    }

    public OcorrenciaResponse buscar(Long id) {
        return OcorrenciaResponse.from(obter(id));
    }

    @Transactional
    public OcorrenciaResponse criar(OcorrenciaRequest r) {
        if (repository.existsByProtocolo(r.protocolo())) {
            throw new RegraNegocioException("Já existe uma ocorrência com o protocolo '" + r.protocolo() + "'");
        }
        OcorrenciaDescarte o = new OcorrenciaDescarte(r.protocolo(), r.bairro(), r.logradouro(),
                r.latitude(), r.longitude(), obterTipo(r.tipoResiduoId()),
                r.status() != null ? r.status() : StatusOcorrencia.ABERTA,
                r.dataAbertura() != null ? r.dataAbertura() : LocalDateTime.now());
        return OcorrenciaResponse.from(repository.save(o));
    }

    @Transactional
    public OcorrenciaResponse atualizar(Long id, OcorrenciaRequest r) {
        OcorrenciaDescarte o = obter(id);
        o.atualizar(r.bairro(), r.logradouro(), r.latitude(), r.longitude(), obterTipo(r.tipoResiduoId()),
                r.status() != null ? r.status() : o.getStatus(),
                r.dataAbertura() != null ? r.dataAbertura() : o.getDataAbertura());
        return OcorrenciaResponse.from(repository.save(o));
    }

    @Transactional
    public void remover(Long id) {
        repository.delete(obter(id));
    }

    /** Bairros com mais ocorrências primeiro: base para o modelo que prevê novos pontos críticos. */
    public List<ResumoBairroResponse> resumoPorBairro() {
        return repository.resumoPorBairro().stream()
                .map(r -> new ResumoBairroResponse(r.getBairro(), r.getTotal()))
                .toList();
    }

    private TipoResiduo obterTipo(Long id) {
        return tipoResiduoRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Tipo de resíduo", id));
    }

    private OcorrenciaDescarte obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Ocorrência", id));
    }
}
