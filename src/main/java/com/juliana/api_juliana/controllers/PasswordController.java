package com.juliana.api_juliana.controllers;

import com.juliana.api_juliana.dtos.APIResponseDto;
import com.juliana.api_juliana.dtos.ResetPasswordDto;
import com.juliana.api_juliana.entities.PasswordResetToken;
import com.juliana.api_juliana.entities.User;
import com.juliana.api_juliana.repositories.PasswordResetTokenRepository;
import com.juliana.api_juliana.repositories.UserRepository;
import com.juliana.api_juliana.services.EmailService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class PasswordController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordController(UserRepository userRepository, PasswordResetTokenRepository tokenRepository,
                              EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<APIResponseDto> forgotPassword(@RequestParam String email) {

        try{
            User user = userRepository.findByEmail(email)
                    .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));

            String token = UUID.randomUUID().toString();

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setEmail(user.getEmail());
            resetToken.setExpirationDate(LocalDateTime.now().plusMinutes(15));
            tokenRepository.save(resetToken);

            emailService.sendPasswordResetEmail(user.getEmail(), token);

            return ResponseEntity.ok(new APIResponseDto("Email found", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponseDto("Email not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponseDto("Server error", null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<APIResponseDto> resetPassword(@RequestBody ResetPasswordDto request) {
        try {
            PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                    .orElseThrow(() -> new IllegalArgumentException("Token inválido."));

            if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
                System.out.println(">>> Token expirado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponseDto("El token ha expirado.", null));
            }

            User user = userRepository.findByEmail(resetToken.getEmail())
                    .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado."));

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            tokenRepository.delete(resetToken);
            return ResponseEntity.ok(new APIResponseDto("Contraseña actualizada correctamente.", null));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponseDto(e.getMessage(), null));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponseDto(e.getMessage(), null));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponseDto("Error interno. Intente más tarde.", null));
        }
    }
}
