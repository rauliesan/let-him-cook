package com.daw.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactoRequestDTO {

    @NotBlank
    @Size(min = 2, max = 60)
    private String nombre;

    @NotBlank
    @Size(min = 2, max = 60)
    private String apellido;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String asunto;

    @NotBlank
    @Size(min = 10, max = 2000)
    private String mensaje;
}
