package com.uad.dto;

import lombok.Data;

@Data
public class RegisterUserRequestDTO {
    private String name;
    private String email;
    private String password;
    private String status;
    private String role; // Nombre del rol como String
}
