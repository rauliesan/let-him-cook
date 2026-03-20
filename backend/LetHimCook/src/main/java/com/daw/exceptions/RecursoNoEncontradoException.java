package com.daw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un recurso no se encuentra en la base de datos.
 * Devuelve un HTTP 404 Not Found.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecursoNoEncontradoException extends RuntimeException {

	
	private static final long serialVersionUID = 4791675227991100662L;

	public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
