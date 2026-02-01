package com.urban.upark.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

/**
 * Service pour l'envoi d'emails via SMTP
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.name}")
    private String appName;

    /**
     * Retourne l'adresse email avec le nom d'affichage "U-Park"
     */
    private String getDisplayFromAddress() {
        return appName + " <" + fromEmail + ">";
    }

    /**
     * Envoie un email simple (texte brut)
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(getDisplayFromAddress());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        
        mailSender.send(message);
        System.out.println(" Email envoyé à: " + to);
    }

    /**
     * Envoie un email HTML avec nom d'affichage personnalisé
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        try {
            // Utiliser InternetAddress pour définir le nom d'affichage
            helper.setFrom(new InternetAddress(fromEmail, appName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // Fallback si l'encodage échoue
            helper.setFrom(fromEmail);
        }
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
        System.out.println(" Email HTML envoyé à: " + to);
    }

    /**
     * Envoie le code de réinitialisation de mot de passe
     */
    public void sendPasswordResetCode(String to, String code) {
        String subject = appName + " - Code de réinitialisation de mot de passe";
        
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }
                    .container { max-width: 500px; margin: 0 auto; background: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .logo { font-size: 28px; font-weight: bold; color: #3B82F6; }
                    .code-box { background: linear-gradient(135deg, #3B82F6, #8B5CF6); color: white; font-size: 32px; font-weight: bold; letter-spacing: 8px; text-align: center; padding: 20px; border-radius: 10px; margin: 20px 0; }
                    .message { color: #666; line-height: 1.6; margin-bottom: 20px; }
                    .warning { color: #EF4444; font-size: 14px; margin-top: 20px; }
                    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🅿️ %s</div>
                    </div>
                    <p class="message">Bonjour,</p>
                    <p class="message">Vous avez demandé la réinitialisation de votre mot de passe. Voici votre code de vérification :</p>
                    <div class="code-box">%s</div>
                    <p class="message">Entrez ce code dans l'application pour définir un nouveau mot de passe.</p>
                    <p class="warning"> Ce code expire dans <strong>15 minutes</strong>. Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.</p>
                    <div class="footer">
                        <p>© 2026 %s - Application de location de parking</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(appName, code, appName);

        try {
            sendHtmlEmail(to, subject, htmlContent);
        } catch (MessagingException e) {
            System.err.println("Erreur envoi email HTML, tentative texte brut: " + e.getMessage());
            // Fallback to simple email
            String simpleText = """
                %s - Réinitialisation de mot de passe
                
                Votre code de vérification : %s
                
                Ce code expire dans 15 minutes.
                
                Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.
                """.formatted(appName, code);
            sendSimpleEmail(to, subject, simpleText);
        }
    }
}
