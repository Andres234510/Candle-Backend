package com.candlenaturals.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String nombreUsuario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bienvenido a Candle Naturals");
        message.setText("Hola " + nombreUsuario + ",\n\nGracias por registrarte en Candle Naturals. ¡Esperamos que disfrutes tu experiencia!");

        mailSender.send(message);
    }
}