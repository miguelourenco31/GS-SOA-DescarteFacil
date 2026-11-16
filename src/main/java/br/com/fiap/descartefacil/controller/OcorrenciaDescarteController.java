package br.com.fiap.descartefacil.controller;

import br.com.fiap.descartefacil.dto.OcorrenciaRequest;
import br.com.fiap.descartefacil.dto.OcorrenciaResponse;
import br.com.fiap.descartefacil.dto.ResumoBairroResponse;
import br.com.fiap.descartefacil.service.OcorrenciaDescarteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
@Tag(name = "Ocorrências de descarte irregular", description = "Chamados do 156 usados pelo modelo de ML")
public class OcorrenciaDescarteController {

    private final OcorrenciaDescarteService service;

    public OcorrenciaDescarteController(OcorrenciaDescarteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista ocorrências (filtro opcional por bairro)")
    public List<OcorrenciaResponse> listar(@RequestParam(required = false) String bairro) {
        return service.listar(bairro);
    }

    @GetMapping("/resumo-por-bairro")
    @Operation(summary = "Total de ocorrências por bairro (mais críticos primeiro)")
    public List<ResumoBairroResponse> resumoPorBairro() {
        return service.resumoPorBairro();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma ocorrência pelo id")
    public OcorrenciaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Registra uma ocorrência de descarte irregular")
    public ResponseEntity<OcorrenciaResponse> criar(@Valid @RequestBody OcorrenciaRequest request) {
        OcorrenciaResponse criada = service.criar(request);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(local).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma ocorrência (ex.: muda o status)")
    public OcorrenciaResponse atualizar(@PathVariable Long id, @Valid @RequestBody OcorrenciaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma ocorrência")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
