package com.uad.dto;

import com.uad.entities.UserEntity;
import lombok.Data;
import java.util.Date;

@Data
public class UserResponseDTO {
    private Long id;
    private Date createdAt;
    private String name;
    private String email;

    // Constructor que recibe la entidad
    public UserResponseDTO(UserEntity user) {
        this.id = user.getId();
        this.createdAt = user.getCreatedAt();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}