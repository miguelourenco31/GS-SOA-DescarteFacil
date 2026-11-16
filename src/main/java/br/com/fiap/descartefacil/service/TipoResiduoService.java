package br.com.fiap.descartefacil.service;

import br.com.fiap.descartefacil.domain.TipoResiduo;
import br.com.fiap.descartefacil.dto.TipoResiduoRequest;
import br.com.fiap.descartefacil.dto.TipoResiduoResponse;
import br.com.fiap.descartefacil.exception.RecursoNaoEncontradoException;
import br.com.fiap.descartefacil.exception.RegraNegocioException;
import br.com.fiap.descartefacil.repository.CacambaRepository;
import br.com.fiap.descartefacil.repository.OcorrenciaDescarteRepository;
import br.com.fiap.descartefacil.repository.TipoResiduoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TipoResiduoService {

    private final TipoResiduoRepository repository;
    private final CacambaRepository cacambaRepository;
    private final OcorrenciaDescarteRepository ocorrenciaRepository;

    public TipoResiduoService(TipoResiduoRepository repository, CacambaRepository cacambaRepository,
                              OcorrenciaDescarteRepository ocorrenciaRepository) {
        this.repository = repository;
        this.cacambaRepository = cacambaRepository;
        this.ocorrenciaRepository = ocorrenciaRepository;
    }

    public List<TipoResiduoResponse> listar() {
        return repository.findAll().stream().map(TipoResiduoResponse::from).toList();
    }

    public TipoResiduoResponse buscar(Long id) {
        return TipoResiduoResponse.from(obter(id));
    }

    @Transactional
    public TipoResiduoResponse criar(TipoResiduoRequest request) {
        if (repository.existsByNomeIgnoreCase(request.nome())) {
            throw new RegraNegocioException("Já existe um tipo de resíduo com o nome '" + request.nome() + "'");
        }
        return TipoResiduoResponse.from(repository.save(new TipoResiduo(request.nome(), request.descricao())));
    }

    @Transactional
    public TipoResiduoResponse atualizar(Long id, TipoResiduoRequest request) {
        TipoResiduo tipo = obter(id);
        boolean mudouNome = !tipo.getNome().equalsIgnoreCase(request.nome());
        if (mudouNome && repository.existsByNomeIgnoreCase(request.nome())) {
            throw new RegraNegocioException("Já existe um tipo de resíduo com o nome '" + request.nome() + "'");
        }
        tipo.atualizar(request.nome(), request.descricao());
        return TipoResiduoResponse.from(repository.save(tipo));
    }

    @Transactional
    public void remover(Long id) {
        TipoResiduo tipo = obter(id);
        if (cacambaRepository.existsByTipoResiduoId(id) || ocorrenciaRepository.existsByTipoResiduoId(id)) {
            throw new RegraNegocioException("Tipo de resíduo em uso por caçambas ou ocorrências; não pode ser removido");
        }
        repository.delete(tipo);
    }

    private TipoResiduo obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Tipo de resíduo", id));
    }
}
