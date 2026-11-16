package br.com.fiap.descartefacil.exception;

/** Parâmetros inválidos que não são cobertos pelo Bean Validation - retorna HTTP 400. */
public class RequisicaoInvalidaException extends RuntimeException {

    public RequisicaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
