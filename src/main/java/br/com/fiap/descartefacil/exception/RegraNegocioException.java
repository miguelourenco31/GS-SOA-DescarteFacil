package br.com.fiap.descartefacil.exception;

/** Violação de regra de negócio (duplicidade, recurso em uso etc.) - retorna HTTP 409. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
