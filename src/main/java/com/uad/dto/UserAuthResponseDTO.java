package com.uad.dto;

import java.util.Collections;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uad.entities.UserEntity;
import com.uad.projection.RoleProjection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserAuthResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String status;
    private String role;
    private Map<String, Boolean> permissions;

    // Constructor desde la entidad y consulta
    public UserAuthResponseDTO(UserEntity user, RoleProjection roleInfo) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.status = user.getStatus();
        this.role = roleInfo.getRoleName();
        this.permissions = parsePermissions(roleInfo.getPermissions());
    }

    private Map<String, Boolean> parsePermissions(String jsonPermissions) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonPermissions, new TypeReference<Map<String, Boolean>>(){});
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }
    
    @Data
    @AllArgsConstructor
    public static class RoleInfo {
        private String roleName;
        private Map<String, Boolean> permissions;
    }
    
    // Getters
}