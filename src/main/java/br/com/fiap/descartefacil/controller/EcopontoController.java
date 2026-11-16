package br.com.fiap.descartefacil.controller;

import br.com.fiap.descartefacil.dto.EcopontoProximoResponse;
import br.com.fiap.descartefacil.dto.EcopontoRequest;
import br.com.fiap.descartefacil.dto.EcopontoResponse;
import br.com.fiap.descartefacil.service.EcopontoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ecopontos")
@Tag(name = "Ecopontos", description = "Pontos de descarte, busca por proximidade e situação de lotação")
public class EcopontoController {

    private final EcopontoService service;

    public EcopontoController(EcopontoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista os ecopontos")
    public List<EcopontoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/proximos")
    @Operation(summary = "Ecopontos mais próximos que aceitam o item, com situação de lotação",
            description = "Ordenados por distância (km). 'lotado' vem do último dado do sensor da caçamba "
                    + "(>= 90%); é nulo quando não há leitura.")
    public List<EcopontoProximoResponse> proximos(@RequestParam double latitude,
                                                  @RequestParam double longitude,
                                                  @RequestParam Long tipoResiduoId,
                                                  @RequestParam(defaultValue = "3") int limite) {
        return service.buscarProximos(latitude, longitude, tipoResiduoId, limite);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um ecoponto pelo id")
    public EcopontoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um ecoponto")
    public ResponseEntity<EcopontoResponse> criar(@Valid @RequestBody EcopontoRequest request) {
        EcopontoResponse criado = service.criar(request);
        return ResponseEntity.created(local(criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um ecoponto")
    public EcopontoResponse atualizar(@PathVariable Long id, @Valid @RequestBody EcopontoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um ecoponto (sem caçambas)")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    private URI local(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }
}
