package br.com.fiap.descartefacil.service;

import br.com.fiap.descartefacil.domain.RotaCataBagulho;
import br.com.fiap.descartefacil.dto.RotaRequest;
import br.com.fiap.descartefacil.dto.RotaResponse;
import br.com.fiap.descartefacil.exception.RecursoNaoEncontradoException;
import br.com.fiap.descartefacil.exception.RequisicaoInvalidaException;
import br.com.fiap.descartefacil.repository.RotaCataBagulhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RotaCataBagulhoService {

    private final RotaCataBagulhoRepository repository;

    public RotaCataBagulhoService(RotaCataBagulhoRepository repository) {
        this.repository = repository;
    }

    public List<RotaResponse> listar() {
        LocalDate hoje = LocalDate.now();
        return repository.findAll().stream().map(r -> RotaResponse.from(r, hoje)).toList();
    }

    public RotaResponse buscar(Long id) {
        return RotaResponse.from(obter(id), LocalDate.now());
    }

    /** "Quando o Cata-Bagulho passa na minha rua?" - logradouro é opcional (busca parcial). */
    public List<RotaResponse> buscarPorLocal(String bairro, String logradouro) {
        LocalDate hoje = LocalDate.now();
        List<RotaCataBagulho> rotas = (logradouro == null || logradouro.isBlank())
                ? repository.findByBairroIgnoreCase(bairro)
                : repository.findByBairroIgnoreCaseAndLogradouroContainingIgnoreCase(bairro, logradouro);
        return rotas.stream().map(r -> RotaResponse.from(r, hoje)).toList();
    }

    @Transactional
    public RotaResponse criar(RotaRequest r) {
        validarHorario(r);
        RotaCataBagulho rota = new RotaCataBagulho(r.bairro(), r.logradouro(), r.diaSemana(),
                r.horaInicio(), r.horaFim());
        return RotaResponse.from(repository.save(rota), LocalDate.now());
    }

    @Transactional
    public RotaResponse atualizar(Long id, RotaRequest r) {
        validarHorario(r);
        RotaCataBagulho rota = obter(id);
        rota.atualizar(r.bairro(), r.logradouro(), r.diaSemana(), r.horaInicio(), r.horaFim());
        return RotaResponse.from(repository.save(rota), LocalDate.now());
    }

    @Transactional
    public void remover(Long id) {
        repository.delete(obter(id));
    }

    private void validarHorario(RotaRequest r) {
        if (!r.horaFim().isAfter(r.horaInicio())) {
            throw new RequisicaoInvalidaException("A hora de fim deve ser posterior à hora de início");
        }
    }

    private RotaCataBagulho obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Rota do Cata-Bagulho", id));
    }
}
