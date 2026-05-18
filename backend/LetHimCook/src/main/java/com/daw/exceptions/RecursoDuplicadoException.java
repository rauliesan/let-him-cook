package com.daw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe
 * (por ejemplo, un usuario con un email que ya está registrado).
 * Devuelve un HTTP 409 Conflict.
 *
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class RecursoDuplicadoException extends RuntimeException {

    
	private static final long serialVersionUID = -7408001664226170443L;

	public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
