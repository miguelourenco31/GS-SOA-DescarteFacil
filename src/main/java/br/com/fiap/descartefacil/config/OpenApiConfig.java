package br.com.fiap.descartefacil.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI descarteFacilOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Descarte Fácil API")
                .version("1.0.0")
                .description("Backend do Descarte Fácil (CitizenTech - Global Solution FIAP 2026.2): "
                        + "ecopontos, caçambas com sensor de lotação, rotas do Cata-Bagulho e "
                        + "ocorrências de descarte irregular (base para o modelo de ML)."));
    }
}
