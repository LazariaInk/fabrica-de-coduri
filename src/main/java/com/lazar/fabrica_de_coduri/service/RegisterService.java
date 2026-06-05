package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.RegisterDTO;
import com.lazar.fabrica_de_coduri.model.User;
import com.lazar.fabrica_de_coduri.model.VerificationToken;
import com.lazar.fabrica_de_coduri.repository.UserRepository;
import com.lazar.fabrica_de_coduri.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegisterService {

        private final UserRepository userRepository;
        private final VerificationTokenRepository tokenRepository;
        private final EmailService emailService;
        private final PasswordEncoder encoder;

    public RegisterService(UserRepository userRepository, VerificationTokenRepository tokenRepository, EmailService emailService, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.encoder = encoder;
    }

    public String registerUser(RegisterDTO dto, String appUrl) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return "User already exists";
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole("ROLE_USER");
        user.setEnabled(false);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        String confirmationUrl = appUrl + "/confirm?token=" + token;

        emailService.sendHtmlEmail(user.getUsername(), "Confirma contul Fabrica de Coduri",
                confirmationEmailTemplate(confirmationUrl));

        return null;
    }

    public String confirmUser(String token) {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            return "Invalid token";
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Token expired";
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        return null;
    }

    private String confirmationEmailTemplate(String confirmationUrl) {
        return """
                <!DOCTYPE html>
                <html lang="ro">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Confirma contul Fabrica de Coduri</title>
                </head>
                <body style="margin:0;padding:0;background:#f3f5f7;font-family:Arial,Helvetica,sans-serif;">
                <div style="display:none;font-size:1px;color:#f3f5f7;line-height:1px;max-height:0;max-width:0;opacity:0;overflow:hidden;">
                  Confirma adresa de email pentru a activa contul Fabrica de Coduri.
                </div>
                <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background:#f3f5f7;padding:40px 15px;"><tr><td align="center">
                <table width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%%;background:#ffffff;border-radius:18px;overflow:hidden;">
                <tr><td style="height:8px;background:#2BAF49;"></td></tr>
                <tr><td align="center" style="background:#383E42;padding:25px 30px;">
                <img src="https://www.fabricadecoduri.com/images/logo.png" alt="Fabrica de Coduri" width="190" style="display:block;margin:0 auto 8px auto;border:0;">
                <h1 style="margin:0;color:#ffffff;font-size:28px;font-weight:bold;">Fabrica de Coduri</h1>
                <p style="margin:10px 0 0 0;color:#d7d7d7;font-size:14px;line-height:1.5;">Invata programare usor, in limba romana.</p>
                </td></tr>
                <tr><td style="padding:50px 40px;text-align:center;">
                <h2 style="margin:0 0 20px 0;color:#383E42;font-size:28px;">Bun venit!</h2>
                <p style="margin:0 0 20px 0;font-size:16px;line-height:1.8;color:#555;">Iti multumim ca te-ai inregistrat pe <strong>Fabrica de Coduri</strong>.</p>
                <p style="margin:0 0 30px 0;font-size:16px;line-height:1.8;color:#555;">Pentru a activa contul si a avea acces la toate lectiile si cursurile, confirma adresa ta de email folosind butonul de mai jos.</p>
                <table cellpadding="0" cellspacing="0" border="0" align="center"><tr><td bgcolor="#2BAF49" style="border-radius:10px;">
                <a href="%s" style="display:inline-block;padding:16px 36px;color:#ffffff;text-decoration:none;font-size:17px;font-weight:bold;">Confirma contul</a>
                </td></tr></table>
                <p style="margin:30px 0 0 0;font-size:13px;line-height:1.7;color:#777;">Linkul de confirmare este valabil 24 de ore.</p>
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
                <p style="margin:25px 0 0 0;color:#999999;font-size:12px;line-height:1.6;">Daca nu ai creat tu acest cont, poti ignora acest email. Contul nu va fi activat fara confirmare.</p>
                <p style="margin:10px 0 0 0;color:#999999;font-size:12px;line-height:1.6;">Ai nevoie de ajutor? Scrie-ne la <a href="mailto:contact@fabricadecoduri.com" style="color:#E8DE63;text-decoration:none;">contact@fabricadecoduri.com</a>.</p>
                <p style="margin:10px 0 0 0;color:#999999;font-size:12px;">© 2026 Fabrica de Coduri. Toate drepturile rezervate.</p>
                </td></tr></table>
                </td></tr></table>
                </body>
                </html>
                """.formatted(confirmationUrl, confirmationUrl, confirmationUrl);
    }
    }

