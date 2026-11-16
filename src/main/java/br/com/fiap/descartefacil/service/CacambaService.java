package br.com.fiap.descartefacil.service;

import br.com.fiap.descartefacil.domain.Cacamba;
import br.com.fiap.descartefacil.domain.Ecoponto;
import br.com.fiap.descartefacil.domain.LeituraCacamba;
import br.com.fiap.descartefacil.domain.TipoResiduo;
import br.com.fiap.descartefacil.dto.CacambaRequest;
import br.com.fiap.descartefacil.dto.CacambaResponse;
import br.com.fiap.descartefacil.dto.LeituraRequest;
import br.com.fiap.descartefacil.dto.LeituraResponse;
import br.com.fiap.descartefacil.exception.RecursoNaoEncontradoException;
import br.com.fiap.descartefacil.exception.RegraNegocioException;
import br.com.fiap.descartefacil.repository.CacambaRepository;
import br.com.fiap.descartefacil.repository.EcopontoRepository;
import br.com.fiap.descartefacil.repository.LeituraCacambaRepository;
import br.com.fiap.descartefacil.repository.TipoResiduoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CacambaService {

    private final CacambaRepository cacambaRepository;
    private final EcopontoRepository ecopontoRepository;
    private final TipoResiduoRepository tipoResiduoRepository;
    private final LeituraCacambaRepository leituraRepository;

    public CacambaService(CacambaRepository cacambaRepository, EcopontoRepository ecopontoRepository,
                          TipoResiduoRepository tipoResiduoRepository,
                          LeituraCacambaRepository leituraRepository) {
        this.cacambaRepository = cacambaRepository;
        this.ecopontoRepository = ecopontoRepository;
        this.tipoResiduoRepository = tipoResiduoRepository;
        this.leituraRepository = leituraRepository;
    }

    public List<CacambaResponse> listar(Long ecopontoId) {
        List<Cacamba> cacambas = ecopontoId == null
                ? cacambaRepository.findAll()
                : cacambaRepository.findByEcopontoId(ecopontoId);
        return cacambas.stream().map(CacambaResponse::from).toList();
    }

    public CacambaResponse buscar(Long id) {
        return CacambaResponse.from(obter(id));
    }

    @Transactional
    public CacambaResponse criar(CacambaRequest r) {
        Ecoponto ecoponto = ecopontoRepository.findById(r.ecopontoId())
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Ecoponto", r.ecopontoId()));
        TipoResiduo tipo = tipoResiduoRepository.findById(r.tipoResiduoId())
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Tipo de resíduo", r.tipoResiduoId()));
        if (cacambaRepository.existsByIdentificador(r.identificador())) {
            throw new RegraNegocioException("Já existe uma caçamba com o identificador '" + r.identificador() + "'");
        }
        return CacambaResponse.from(cacambaRepository.save(
                new Cacamba(ecoponto, tipo, r.identificador(), r.capacidadeM3())));
    }

    @Transactional
    public void remover(Long id) {
        cacambaRepository.delete(obter(id));
    }

    /** Recebe uma leitura do sensor da caçamba (o ESP32 faz POST neste serviço). */
    @Transactional
    public LeituraResponse registrarLeitura(Long cacambaId, LeituraRequest r) {
        Cacamba cacamba = obter(cacambaId);
        LocalDateTime quando = r.registradaEm() != null ? r.registradaEm() : LocalDateTime.now();
        return LeituraResponse.from(leituraRepository.save(
                new LeituraCacamba(cacamba, r.nivelPercentual(), quando)));
    }

    /** Últimas 20 leituras da caçamba, da mais recente para a mais antiga. */
    public List<LeituraResponse> listarLeituras(Long cacambaId) {
        obter(cacambaId);
        return leituraRepository.findTop20ByCacambaIdOrderByRegistradaEmDescIdDesc(cacambaId).stream()
                .map(LeituraResponse::from).toList();
    }

    private Cacamba obter(Long id) {
        return cacambaRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Caçamba", id));
    }
}
