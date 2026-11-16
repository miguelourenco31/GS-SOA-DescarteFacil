package br.com.fiap.descartefacil.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class GeoUtilsTest {

    @Test
    void distanciaEntrePontosIguaisEZero() {
        assertThat(GeoUtils.distanciaKm(-23.55, -46.63, -23.55, -46.63)).isZero();
    }

    @Test
    void distanciaSaoPauloRioEmTornoDe360Km() {
        double km = GeoUtils.distanciaKm(-23.5505, -46.6333, -22.9068, -43.1729);
        assertThat(km).isBetween(350.0, 365.0);
    }

    @Test
    void distanciaESimetrica() {
        double ida = GeoUtils.distanciaKm(-23.5670, -46.7020, -23.5020, -46.6290);
        double volta = GeoUtils.distanciaKm(-23.5020, -46.6290, -23.5670, -46.7020);
        assertThat(ida).isCloseTo(volta, within(1e-9));
    }
}
