package com.juliana.api_juliana.services;

import com.juliana.api_juliana.entities.Appointment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Recuperación de contraseña";
        String url = "http://localhost:4200/password-recovery/change-password?token=" + token;
        String body = "Haz click en el siguiente enlace para restablecer tu contraseña:\n" + url;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("juliana.lamimaker@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendConfirmedAppointmentEmail(String to, Appointment appointment) {
        String subject = "¡Tu turno está confirmado! \uD83C\uDF89";
        String body = "Hola " + appointment.getClient().getName() + ",\n\n" +
                "Tu turno ha sido confirmado correctamente.\n\n" +
                "📌 Servicio: " + appointment.getTreatment().getName() + "\n" +
                "📅 Fecha: " + appointment.getDate() + "\n" +
                "⏰ Hora: " + appointment.getTime() + "\n\n" +
                "Te esperamos. Si necesitas reprogramar o cancelar, por favor contáctanos.\n\n" +
                "Muchas gracias.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("juliana.lamimaker@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
