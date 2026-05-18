package com.daw.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.ContactoRequestDTO;

import jakarta.validation.Valid;

/** Recibe mensajes del formulario de contacto y los reenvía al correo de soporte (Gmail SMTP). */
@RestController
@RequestMapping("/contacto")
public class ContactoController {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String destinatario;

    public ContactoController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping
    public ResponseEntity<Void> enviar(@Valid @RequestBody ContactoRequestDTO dto) {

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setReplyTo(dto.getEmail());
        mensaje.setSubject("[" + dto.getAsunto() + "] Mensaje de " + dto.getNombre() + " " + dto.getApellido());
        mensaje.setText(
            "Nombre:  " + dto.getNombre() + " " + dto.getApellido() + "\n" +
            "Email:   " + dto.getEmail() + "\n" +
            "Asunto:  " + dto.getAsunto() + "\n\n" +
            "─────────────────────────────\n\n" +
            dto.getMensaje()
        );

        mailSender.send(mensaje);
        return ResponseEntity.ok().build();
    }
}
