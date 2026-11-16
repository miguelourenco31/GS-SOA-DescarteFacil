package br.com.fiap.descartefacil.controller;

import br.com.fiap.descartefacil.dto.RotaRequest;
import br.com.fiap.descartefacil.dto.RotaResponse;
import br.com.fiap.descartefacil.service.RotaCataBagulhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/rotas-cata-bagulho")
@Tag(name = "Cata-Bagulho", description = "Dias e horários de passagem do Cata-Bagulho por rua")
public class RotaCataBagulhoController {

    private final RotaCataBagulhoService service;

    public RotaCataBagulhoController(RotaCataBagulhoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todas as rotas")
    public List<RotaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Quando o Cata-Bagulho passa na minha rua?",
            description = "Filtra por bairro (obrigatório) e logradouro (opcional, busca parcial). "
                    + "Retorna também a próxima data de passagem.")
    public List<RotaResponse> buscar(@RequestParam String bairro,
                                     @RequestParam(required = false) String logradouro) {
        return service.buscarPorLocal(bairro, logradouro);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma rota pelo id")
    public RotaResponse buscarPorId(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra uma rota")
    public ResponseEntity<RotaResponse> criar(@Valid @RequestBody RotaRequest request) {
        RotaResponse criada = service.criar(request);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(local).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma rota")
    public RotaResponse atualizar(@PathVariable Long id, @Valid @RequestBody RotaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma rota")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
