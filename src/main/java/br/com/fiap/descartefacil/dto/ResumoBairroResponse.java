package br.com.fiap.descartefacil.dto;

/** Total de ocorrências por bairro: entrada simples para o modelo de previsão de novos pontos críticos. */
public record ResumoBairroResponse(String bairro, long total) {
}
