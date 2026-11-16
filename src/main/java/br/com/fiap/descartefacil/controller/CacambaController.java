package br.com.fiap.descartefacil.controller;

import br.com.fiap.descartefacil.dto.CacambaRequest;
import br.com.fiap.descartefacil.dto.CacambaResponse;
import br.com.fiap.descartefacil.dto.LeituraRequest;
import br.com.fiap.descartefacil.dto.LeituraResponse;
import br.com.fiap.descartefacil.service.CacambaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cacambas")
@Tag(name = "Caçambas e sensores", description = "Caçambas dos ecopontos e leituras de lotação enviadas pelo sensor")
public class CacambaController {

    private final CacambaService service;

    public CacambaController(CacambaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista caçambas (filtro opcional por ecoponto)")
    public List<CacambaResponse> listar(@RequestParam(required = false) Long ecopontoId) {
        return service.listar(ecopontoId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma caçamba pelo id")
    public CacambaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma caçamba em um ecoponto")
    public ResponseEntity<CacambaResponse> criar(@Valid @RequestBody CacambaRequest request) {
        CacambaResponse criada = service.criar(request);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(local).body(criada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma caçamba e suas leituras")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/leituras")
    @Operation(summary = "Registra uma leitura de lotação (endpoint chamado pelo sensor)")
    public ResponseEntity<LeituraResponse> registrarLeitura(@PathVariable Long id,
                                                            @Valid @RequestBody LeituraRequest request) {
        return ResponseEntity.status(201).body(service.registrarLeitura(id, request));
    }

    @GetMapping("/{id}/leituras")
    @Operation(summary = "Últimas 20 leituras da caçamba")
    public List<LeituraResponse> listarLeituras(@PathVariable Long id) {
        return service.listarLeituras(id);
    }
}
