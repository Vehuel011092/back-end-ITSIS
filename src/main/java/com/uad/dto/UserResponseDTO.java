package com.uad.dto;


import com.uad.entities.UserEntity;
import lombok.Data;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;


@Data
public class UserResponseDTO {
    private Long id;
    private Date createdAt;
    private String name;
    private String email;
    private String status;
    private Set<String> roles;
    private Date updatedAt;
    private Date lastLogin;

    // Constructor que recibe la entidad
    public UserResponseDTO(UserEntity user) {
        this.id = user.getId();
        this.createdAt = user.getCreatedAt();
        this.name = user.getName();
        this.email = user.getEmail();
        this.status = user.getStatus();
        this.lastLogin = user.getLastLogin();
        this.roles = user.getRoles().stream()
				.map(role -> role.getRoleName())
				.collect(Collectors.toSet());
        this.updatedAt = user.getUpdatedAt();
    }
}