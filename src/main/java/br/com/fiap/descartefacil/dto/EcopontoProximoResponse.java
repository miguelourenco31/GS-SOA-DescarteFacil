package br.com.fiap.descartefacil.dto;

import java.time.LocalDateTime;

/**
 * Resultado da busca "ecoponto mais perto" para um tipo de resíduo.
 * {@code lotado}, {@code nivelPercentual} e {@code ultimaLeitura} são nulos quando
 * nenhuma caçamba daquele tipo possui leitura de sensor.
 */
public record EcopontoProximoResponse(Long id, String nome, String endereco, String bairro,
                                      double latitude, double longitude, double distanciaKm,
                                      Boolean lotado, Integer nivelPercentual,
                                      LocalDateTime ultimaLeitura) {
}
