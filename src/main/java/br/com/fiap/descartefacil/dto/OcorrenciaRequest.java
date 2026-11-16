package br.com.fiap.descartefacil.dto;

import br.com.fiap.descartefacil.domain.StatusOcorrencia;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record OcorrenciaRequest(
        @NotBlank(message = "O protocolo é obrigatório")
        @Size(max = 40, message = "O protocolo deve ter no máximo 40 caracteres")
        String protocolo,

        @NotBlank(message = "O bairro é obrigatório")
        @Size(max = 80, message = "O bairro deve ter no máximo 80 caracteres")
        String bairro,

        @Size(max = 160, message = "O logradouro deve ter no máximo 160 caracteres")
        String logradouro,

        @NotNull(message = "A latitude é obrigatória")
        @DecimalMin(value = "-90.0", message = "Latitude deve estar entre -90 e 90")
        @DecimalMax(value = "90.0", message = "Latitude deve estar entre -90 e 90")
        Double latitude,

        @NotNull(message = "A longitude é obrigatória")
        @DecimalMin(value = "-180.0", message = "Longitude deve estar entre -180 e 180")
        @DecimalMax(value = "180.0", message = "Longitude deve estar entre -180 e 180")
        Double longitude,

        @NotNull(message = "O tipo de resíduo é obrigatório")
        Long tipoResiduoId,

        /** Opcional: se omitido, a ocorrência nasce como ABERTA. */
        StatusOcorrencia status,

        /** Opcional: se omitido, usa o horário atual do servidor. */
        LocalDateTime dataAbertura) {
}
