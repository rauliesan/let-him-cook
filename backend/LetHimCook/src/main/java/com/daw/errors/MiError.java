package com.daw.errors;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Clase para mostrar los errores.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
@AllArgsConstructor
public class MiError {

	private HttpStatus estado;
	private LocalDateTime fecha;
	private String mensaje;
	private String path;

}
