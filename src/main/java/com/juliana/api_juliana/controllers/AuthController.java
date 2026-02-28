package com.juliana.api_juliana.controllers;

import com.juliana.api_juliana.dtos.JwtDto;
import com.juliana.api_juliana.dtos.LoginUserDto;
import com.juliana.api_juliana.dtos.NewUserDto;
import com.juliana.api_juliana.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDto loginUserDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
        try {
            String jwt = authService.authenticate(loginUserDto.username, loginUserDto.getPassword());
            return ResponseEntity.ok(new JwtDto(jwt));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody NewUserDto newUserDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body("Revise los campos");
        }

        try {
            authService.registerNormalUser(newUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registrado");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(
            @Valid @RequestBody NewUserDto newUserDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body("Revise los campos");
        }

        try {
            authService.registerAdmin(newUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Administrador creado");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check-auth")
    public ResponseEntity<String> checkAuth() {
        return ResponseEntity.ok().body("Autenticado");
    }
}
