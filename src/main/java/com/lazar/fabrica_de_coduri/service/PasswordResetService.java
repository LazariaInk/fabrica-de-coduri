package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.PasswordResetToken;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.repository.PasswordResetTokenRepository;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void requestReset(String email, String appUrl) {
        Optional<User> optionalUser = userRepository.findByUsername(email);
        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(60));
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        String resetUrl = appUrl + "/reset-password?token=" + resetToken.getToken();
        emailService.sendHtmlEmail(user.getUsername(), "Reseteaza parola - Fabrica de Coduri",
                resetEmailTemplate(resetUrl));
    }

    @Transactional(readOnly = true)
    public String validateToken(String token) {
        Optional<PasswordResetToken> optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            return "Linkul de resetare nu este valid.";
        }

        PasswordResetToken resetToken = optionalToken.get();
        if (resetToken.isUsed()) {
            return "Linkul de resetare a fost deja folosit.";
        }
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Linkul de resetare a expirat.";
        }

        return null;
    }

    @Transactional
    public String resetPassword(String token, String newPassword, String confirmPassword) {
        String validationError = validateToken(token);
        if (validationError != null) {
            return validationError;
        }
        if (!newPassword.equals(confirmPassword)) {
            return "Parolele nu coincid.";
        }
        if (newPassword.length() < 8) {
            return "Parola trebuie sa aiba cel putin 8 caractere.";
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token not found"));
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        return null;
    }

    private String resetEmailTemplate(String resetUrl) {
        return """
                <!DOCTYPE html>
                <html lang="ro">
                <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Reseteaza parola - Fabrica de Coduri</title></head>
                <body style="margin:0;padding:0;background:#f3f5f7;font-family:Arial,Helvetica,sans-serif;">
                <div style="display:none;font-size:1px;color:#f3f5f7;line-height:1px;max-height:0;max-width:0;opacity:0;overflow:hidden;">Reseteaza parola contului tau Fabrica de Coduri.</div>
                <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background:#f3f5f7;padding:40px 15px;"><tr><td align="center">
                <table width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%%;background:#ffffff;border-radius:18px;overflow:hidden;">
                <tr><td style="height:8px;background:#2BAF49;"></td></tr>
                <tr><td align="center" style="background:#383E42;padding:25px 30px;">
                <img src="https://www.fabricadecoduri.com/images/logo.png" alt="Fabrica de Coduri" width="190" style="display:block;margin:0 auto 8px auto;border:0;">
                <h1 style="margin:0;color:#ffffff;font-size:28px;font-weight:bold;">Fabrica de Coduri</h1>
                <p style="margin:10px 0 0 0;color:#d7d7d7;font-size:14px;line-height:1.5;">Invata programare usor, in limba romana - tutoriale pas cu pas, exemple practice si exercitii interactive.</p>
                </td></tr>
                <tr><td style="padding:50px 40px;text-align:center;">
                <h2 style="margin:0 0 20px 0;color:#383E42;font-size:28px;">Reseteaza parola</h2>
                <p style="margin:0 0 20px 0;font-size:16px;line-height:1.8;color:#555;">Am primit o solicitare pentru resetarea parolei contului tau.</p>
                <p style="margin:0 0 30px 0;font-size:16px;line-height:1.8;color:#555;">Pentru a seta o parola noua, foloseste butonul de mai jos.</p>
                <table cellpadding="0" cellspacing="0" border="0" align="center"><tr><td bgcolor="#2BAF49" style="border-radius:10px;">
                <a href="%s" style="display:inline-block;padding:16px 36px;color:#ffffff;text-decoration:none;font-size:17px;font-weight:bold;">Reseteaza parola</a>
                </td></tr></table>
                <p style="margin:30px 0 0 0;font-size:13px;line-height:1.7;color:#777;">Linkul de resetare este valabil 60 de minute si poate fi utilizat o singura data.</p>
                <p style="margin:15px 0 0 0;font-size:13px;line-height:1.7;color:#777;">Daca butonul nu functioneaza, copiaza si deschide acest link in browser:<br><a href="%s" style="color:#2BAF49;word-break:break-all;">%s</a></p>
                </td></tr>
                <tr><td style="height:5px;background:#E8DE63;"></td></tr>
                <tr><td align="center" style="background:#383E42;padding:30px;">
                <p style="margin:0;color:#ffffff;font-size:16px;font-weight:bold;">Fabrica de Coduri</p>
                <p style="margin:15px 0 20px 0;color:#c8c8c8;font-size:14px;line-height:1.7;font-weight:bold;">Alatura-te comunitatii si conecteaza-te cu alti pasionati de programare, dezvoltare software si tehnologie.</p>
                <table cellpadding="0" cellspacing="0" border="0" align="center"><tr>
                <td style="padding:0 5px;"><a href="https://www.tiktok.com/@fabricadecoduri" style="display:inline-block;background:#2BAF49;color:#ffffff;text-decoration:none;padding:10px 16px;border-radius:6px;font-size:13px;font-weight:bold;">TikTok</a></td>
                <td style="padding:0 5px;"><a href="https://discord.gg/J5mK6cyTQC" style="display:inline-block;background:#383E42;color:#ffffff;text-decoration:none;padding:10px 16px;border-radius:6px;border:1px solid #2BAF49;font-size:13px;font-weight:bold;">Discord</a></td>
                <td style="padding:0 5px;"><a href="https://www.instagram.com/fabricadecoduri/" style="display:inline-block;background:#E8DE63;color:#383E42;text-decoration:none;padding:10px 16px;border-radius:6px;font-size:13px;font-weight:bold;">Instagram</a></td>
                </tr></table>
                <p style="margin:25px 0 0 0;color:#999999;font-size:12px;line-height:1.6;">Daca nu ai solicitat resetarea parolei, poti ignora acest email. Parola actuala va ramane neschimbata.</p>
                <p style="margin:10px 0 0 0;color:#999999;font-size:12px;line-height:1.6;">Ai nevoie de ajutor? Scrie-ne la <a href="mailto:contact@fabricadecoduri.com" style="color:#E8DE63;text-decoration:none;">contact@fabricadecoduri.com</a>.</p>
                <p style="margin:10px 0 0 0;color:#999999;font-size:12px;">© 2026 Fabrica de Coduri. Toate drepturile rezervate.</p>
                </td></tr></table>
                </td></tr></table>
                </body></html>
                """.formatted(resetUrl, resetUrl, resetUrl);
    }
}
