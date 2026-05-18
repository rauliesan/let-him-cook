package com.daw.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para el envío de correos electrónicos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Envía un correo electrónico de forma asíncrona.
     */
    @Async
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indica que es HTML
            
            mailSender.send(message);
            log.info("Correo enviado exitosamente a: {}", to);
        } catch (MessagingException e) {
            log.error("Error al enviar el correo a {}: {}", to, e.getMessage());
            throw new RuntimeException("Error al enviar el correo de recuperación.");
        }
    }

    /**
     * Envía el código de recuperación de contraseña.
     */
    public void enviarCodigoRecuperacion(String email, String codigo) {
        String asunto = "Código de recuperación de contraseña - LetHimCook";
        String html = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eaeaeb; border-radius: 8px;\">"
                + "<h2 style=\"color: #C13E28; text-align: center;\">LetHimCook</h2>"
                + "<p>Has solicitado recuperar tu contraseña.</p>"
                + "<p>Tu código de verificación de 6 dígitos es:</p>"
                + "<div style=\"background-color: #f4f4f5; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px; border-radius: 6px; margin: 20px 0;\">"
                + codigo + "</div>"
                + "<p style=\"color: #666; font-size: 14px;\">Este código expirará en 15 minutos. Si no has solicitado este cambio, puedes ignorar este correo.</p>"
                + "</div>";

        sendEmail(email, asunto, html);
    }
}
