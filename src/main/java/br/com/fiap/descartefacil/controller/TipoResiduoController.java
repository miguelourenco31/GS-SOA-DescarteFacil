package br.com.fiap.descartefacil.controller;

import br.com.fiap.descartefacil.dto.TipoResiduoRequest;
import br.com.fiap.descartefacil.dto.TipoResiduoResponse;
import br.com.fiap.descartefacil.service.TipoResiduoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-residuo")
@Tag(name = "Tipos de resíduo", description = "Sofá, entulho, geladeira velha e outros itens aceitos para descarte")
public class TipoResiduoController {

    private final TipoResiduoService service;

    public TipoResiduoController(TipoResiduoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista os tipos de resíduo")
    public List<TipoResiduoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um tipo de resíduo pelo id")
    public TipoResiduoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um tipo de resíduo")
    public ResponseEntity<TipoResiduoResponse> criar(@Valid @RequestBody TipoResiduoRequest request) {
        TipoResiduoResponse criado = service.criar(request);
        return ResponseEntity.created(local(criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um tipo de resíduo")
    public TipoResiduoResponse atualizar(@PathVariable Long id, @Valid @RequestBody TipoResiduoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um tipo de resíduo (se não estiver em uso)")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    private URI local(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }
}
