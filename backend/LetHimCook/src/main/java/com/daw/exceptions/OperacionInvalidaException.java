package com.daw.exceptions;

/**
 * Excepción personalizada para operaciones que son lógicamente inválidas por
 * reglas de negocio,
 * pero que no necesariamente se deben a un recurso no encontrado o duplicado.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
public class OperacionInvalidaException extends RuntimeException {

	private static final long serialVersionUID = -1203893322939706446L;

	public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
