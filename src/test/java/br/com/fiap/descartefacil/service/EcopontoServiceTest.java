package br.com.fiap.descartefacil.service;

import br.com.fiap.descartefacil.domain.Cacamba;
import br.com.fiap.descartefacil.domain.Ecoponto;
import br.com.fiap.descartefacil.domain.LeituraCacamba;
import br.com.fiap.descartefacil.domain.TipoResiduo;
import br.com.fiap.descartefacil.dto.EcopontoProximoResponse;
import br.com.fiap.descartefacil.exception.RecursoNaoEncontradoException;
import br.com.fiap.descartefacil.exception.RequisicaoInvalidaException;
import br.com.fiap.descartefacil.repository.CacambaRepository;
import br.com.fiap.descartefacil.repository.EcopontoRepository;
import br.com.fiap.descartefacil.repository.LeituraCacambaRepository;
import br.com.fiap.descartefacil.repository.TipoResiduoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EcopontoServiceTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 13, 8, 0);

    @Mock EcopontoRepository ecopontoRepository;
    @Mock CacambaRepository cacambaRepository;
    @Mock LeituraCacambaRepository leituraRepository;
    @Mock TipoResiduoRepository tipoResiduoRepository;

    @InjectMocks EcopontoService service;

    private TipoResiduo sofa;
    private Ecoponto perto;
    private Ecoponto longe;

    @BeforeEach
    void setUp() {
        sofa = comId(new TipoResiduo("Sofa", null), 1L);
        perto = comId(new Ecoponto("Perto", "Rua A", "Pinheiros", -23.5670, -46.7020, true, null), 10L);
        longe = comId(new Ecoponto("Longe", "Rua B", "Santana", -23.5020, -46.6290, true, null), 20L);
    }

    @Test
    void ordenaPorDistanciaEMarcaLotacaoPeloSensor() {
        Cacamba cPerto = comId(new Cacamba(perto, sofa, "C-PERTO", 30.0), 100L);
        Cacamba cLonge = comId(new Cacamba(longe, sofa, "C-LONGE", 30.0), 200L);

        when(tipoResiduoRepository.findById(1L)).thenReturn(Optional.of(sofa));
        when(ecopontoRepository.findAtivosComCacambaDoTipo(1L)).thenReturn(List.of(longe, perto));
        when(cacambaRepository.findByEcopontoIdAndTipoResiduoId(10L, 1L)).thenReturn(List.of(cPerto));
        when(cacambaRepository.findByEcopontoIdAndTipoResiduoId(20L, 1L)).thenReturn(List.of(cLonge));
        when(leituraRepository.findFirstByCacambaIdOrderByRegistradaEmDescIdDesc(100L))
                .thenReturn(Optional.of(new LeituraCacamba(cPerto, 95, AGORA)));
        when(leituraRepository.findFirstByCacambaIdOrderByRegistradaEmDescIdDesc(200L))
                .thenReturn(Optional.of(new LeituraCacamba(cLonge, 20, AGORA)));

        List<EcopontoProximoResponse> resultado = service.buscarProximos(-23.5670, -46.7020, 1L, 3);

        assertThat(resultado).extracting(EcopontoProximoResponse::nome).containsExactly("Perto", "Longe");
        assertThat(resultado.get(0).distanciaKm()).isZero();
        assertThat(resultado.get(0).lotado()).isTrue();
        assertThat(resultado.get(0).nivelPercentual()).isEqualTo(95);
        assertThat(resultado.get(1).lotado()).isFalse();
        assertThat(resultado.get(1).distanciaKm()).isGreaterThan(0.0);
    }

    @Test
    void lotadoFicaNuloQuandoNaoHaLeitura() {
        Cacamba cPerto = comId(new Cacamba(perto, sofa, "C-PERTO", 30.0), 100L);

        when(tipoResiduoRepository.findById(1L)).thenReturn(Optional.of(sofa));
        when(ecopontoRepository.findAtivosComCacambaDoTipo(1L)).thenReturn(List.of(perto));
        when(cacambaRepository.findByEcopontoIdAndTipoResiduoId(10L, 1L)).thenReturn(List.of(cPerto));
        when(leituraRepository.findFirstByCacambaIdOrderByRegistradaEmDescIdDesc(100L))
                .thenReturn(Optional.empty());

        EcopontoProximoResponse r = service.buscarProximos(-23.55, -46.63, 1L, 3).get(0);

        assertThat(r.lotado()).isNull();
        assertThat(r.nivelPercentual()).isNull();
        assertThat(r.ultimaLeitura()).isNull();
    }

    @Test
    void naoEstaLotadoSeAlgumaCacambaDoTipoAindaTemEspaco() {
        Cacamba c1 = comId(new Cacamba(perto, sofa, "C-1", 30.0), 100L);
        Cacamba c2 = comId(new Cacamba(perto, sofa, "C-2", 30.0), 101L);

        when(tipoResiduoRepository.findById(1L)).thenReturn(Optional.of(sofa));
        when(ecopontoRepository.findAtivosComCacambaDoTipo(1L)).thenReturn(List.of(perto));
        when(cacambaRepository.findByEcopontoIdAndTipoResiduoId(10L, 1L)).thenReturn(List.of(c1, c2));
        when(leituraRepository.findFirstByCacambaIdOrderByRegistradaEmDescIdDesc(100L))
                .thenReturn(Optional.of(new LeituraCacamba(c1, 98, AGORA)));
        when(leituraRepository.findFirstByCacambaIdOrderByRegistradaEmDescIdDesc(101L))
                .thenReturn(Optional.of(new LeituraCacamba(c2, 35, AGORA)));

        EcopontoProximoResponse r = service.buscarProximos(-23.55, -46.63, 1L, 3).get(0);

        assertThat(r.lotado()).isFalse();
        assertThat(r.nivelPercentual()).isEqualTo(35);
    }

    @Test
    void respeitaOLimiteDeResultados() {
        Cacamba cPerto = comId(new Cacamba(perto, sofa, "C-PERTO", 30.0), 100L);
        Cacamba cLonge = comId(new Cacamba(longe, sofa, "C-LONGE", 30.0), 200L);

        when(tipoResiduoRepository.findById(1L)).thenReturn(Optional.of(sofa));
        when(ecopontoRepository.findAtivosComCacambaDoTipo(1L)).thenReturn(List.of(longe, perto));
        when(cacambaRepository.findByEcopontoIdAndTipoResiduoId(10L, 1L)).thenReturn(List.of(cPerto));
        when(cacambaRepository.findByEcopontoIdAndTipoResiduoId(20L, 1L)).thenReturn(List.of(cLonge));
        when(leituraRepository.findFirstByCacambaIdOrderByRegistradaEmDescIdDesc(100L)).thenReturn(Optional.empty());
        when(leituraRepository.findFirstByCacambaIdOrderByRegistradaEmDescIdDesc(200L)).thenReturn(Optional.empty());

        List<EcopontoProximoResponse> resultado = service.buscarProximos(-23.5670, -46.7020, 1L, 1);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Perto");
    }

    @Test
    void rejeitaCoordenadasInvalidas() {
        assertThatThrownBy(() -> service.buscarProximos(120.0, -46.63, 1L, 3))
                .isInstanceOf(RequisicaoInvalidaException.class);
        assertThatThrownBy(() -> service.buscarProximos(-23.55, -200.0, 1L, 3))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void rejeitaLimiteForaDoIntervalo() {
        assertThatThrownBy(() -> service.buscarProximos(-23.55, -46.63, 1L, 0))
                .isInstanceOf(RequisicaoInvalidaException.class);
        assertThatThrownBy(() -> service.buscarProximos(-23.55, -46.63, 1L, 21))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void falhaQuandoOTipoDeResiduoNaoExiste() {
        when(tipoResiduoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarProximos(-23.55, -46.63, 99L, 3))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    private static <T> T comId(T entidade, Long id) {
        ReflectionTestUtils.setField(entidade, "id", id);
        return entidade;
    }
}
