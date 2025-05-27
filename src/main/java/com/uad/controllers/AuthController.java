package com.uad.controllers;

import com.uad.config.JwtUtil;
import com.uad.dto.UserAuthResponseDTO;
import com.uad.entities.UserEntity;
import com.uad.repositories.UserRepository;
import com.uad.services.AuthService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, JwtUtil jwtUtil, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder  ) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        try {
            // 1. Verificar si el usuario existe
            UserEntity user = userRepository.findByEmail(email);
            UserAuthResponseDTO authResponse = authService.authenticateUser(email, password);
            if (authResponse == null) {
                return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "code", "INVALID_CREDENTIALS",
                    "message", "Credenciales inválidas"
                ));
            }

            // 2. Validar contraseña
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "code", "INVALID_PASSWORD",
                    "message", "Contraseña incorrecta"
                ));
            }

            // 3. Generar token si todo es correcto
            String token = jwtUtil.generateToken(authService.loadUserByUsername(email));
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "token", token,
                "user", authResponse // DTO sin información sensible
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "status", "error",
                "code", "SERVER_ERROR",
                "message", "Error interno del servidor"
            ));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
        @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        try {
            // Verificar si el header existe
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new JwtException("Formato de token inválido");
            }

            // Extraer y validar token
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            
            // Buscar usuario
            UserEntity user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("Usuario no encontrado");
            }

            return ResponseEntity.ok(new UserAuthResponseDTO(user,
				userRepository.findUserRolesWithPermissions(email).stream().findFirst()
					.orElseThrow(() -> new RuntimeException("Usuario sin roles asignados"))
			));
            
        } catch (JwtException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                    "status", "error",
                    "message", "Token inválido o expirado",
                    "details", ex.getMessage()
                ));
                
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "status", "error",
                    "message", ex.getMessage()
                ));
                
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Error interno del servidor"
                ));
        }
    }
}