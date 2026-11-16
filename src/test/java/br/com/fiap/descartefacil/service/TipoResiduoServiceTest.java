package br.com.fiap.descartefacil.service;

import br.com.fiap.descartefacil.domain.TipoResiduo;
import br.com.fiap.descartefacil.dto.TipoResiduoRequest;
import br.com.fiap.descartefacil.dto.TipoResiduoResponse;
import br.com.fiap.descartefacil.exception.RecursoNaoEncontradoException;
import br.com.fiap.descartefacil.exception.RegraNegocioException;
import br.com.fiap.descartefacil.repository.CacambaRepository;
import br.com.fiap.descartefacil.repository.OcorrenciaDescarteRepository;
import br.com.fiap.descartefacil.repository.TipoResiduoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoResiduoServiceTest {

    @Mock TipoResiduoRepository repository;
    @Mock CacambaRepository cacambaRepository;
    @Mock OcorrenciaDescarteRepository ocorrenciaRepository;

    @InjectMocks TipoResiduoService service;

    @Test
    void criaTipoQuandoNomeEstaLivre() {
        when(repository.existsByNomeIgnoreCase("Colchao")).thenReturn(false);
        when(repository.save(any(TipoResiduo.class))).thenAnswer(inv -> {
            TipoResiduo t = inv.getArgument(0);
            ReflectionTestUtils.setField(t, "id", 7L);
            return t;
        });

        TipoResiduoResponse r = service.criar(new TipoResiduoRequest("Colchao", "Colchoes usados"));

        assertThat(r.id()).isEqualTo(7L);
        assertThat(r.nome()).isEqualTo("Colchao");
    }

    @Test
    void naoCriaTipoComNomeDuplicado() {
        when(repository.existsByNomeIgnoreCase("Entulho")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(new TipoResiduoRequest("Entulho", null)))
                .isInstanceOf(RegraNegocioException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void naoRemoveTipoEmUsoPorCacamba() {
        TipoResiduo tipo = new TipoResiduo("Entulho", null);
        when(repository.findById(1L)).thenReturn(Optional.of(tipo));
        when(cacambaRepository.existsByTipoResiduoId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.remover(1L)).isInstanceOf(RegraNegocioException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void removeTipoSemUso() {
        TipoResiduo tipo = new TipoResiduo("Colchao", null);
        when(repository.findById(1L)).thenReturn(Optional.of(tipo));
        when(cacambaRepository.existsByTipoResiduoId(1L)).thenReturn(false);
        when(ocorrenciaRepository.existsByTipoResiduoId(1L)).thenReturn(false);

        service.remover(1L);

        verify(repository).delete(tipo);
    }

    @Test
    void buscaInexistenteLancaNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(99L)).isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
