package com.uad.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uad.config.JwtUtil;
import com.uad.dto.RegisterUserRequestDTO;
import com.uad.dto.UserAuthResponseDTO;
import com.uad.dto.UserResponseDTO;
import com.uad.dto.UserUpdateDTO;
import com.uad.entities.UserEntity;
import com.uad.services.RoleService;
import com.uad.services.UserService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.uad.entities.Role;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RoleService roleService; // Servicio para obtener los datos de los roles

    @Autowired
    private ObjectMapper objectMapper; // Para convertir la cadena JSON de permisos
    
    
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable long id) {
		UserEntity user = userService.getUserById(id);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
    
    @GetMapping("/current-user")
    public ResponseEntity<UserAuthResponseDTO> getCurrentUser(
        @RequestHeader("Authorization") String authHeader
    ) {
        try {
            // Extraer token del header
            String token = authHeader.replace("Bearer ", "");
            
            // Validar token
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Obtener email del token
            String email = jwtUtil.extractUsername(token);
            
            // Buscar usuario en la base de datos
            UserAuthResponseDTO user = userService.getUserWithPermissions(email);
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsersWithRoles();
        
        List<Map<String, Object>> response = users.stream().map(user -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("createAt", user.getCreatedAt());
            userMap.put("name", user.getName());
            userMap.put("email", user.getEmail());
            userMap.put("status", user.getStatus());
            
            // Mapear roles
            List<Map<String, Object>> rolesList = user.getRoles().stream().map(role -> {
                Map<String, Object> roleMap = new HashMap<>();
                roleMap.put("name", role.getRoleName());
                
                try {
                    // Parsear los permisos JSON
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Boolean> permissions = mapper.readValue(
                        role.getPermissions(), 
                        new TypeReference<Map<String, Boolean>>(){}
                    );
                    roleMap.put("permissions", permissions);
                } catch (Exception e) {
                    roleMap.put("permissions", Collections.emptyMap());
                }
                
                return roleMap;
            }).collect(Collectors.toList());
            
            userMap.put("roles", rolesList);
            return userMap;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUserById(id);
    }
    
    @PostMapping()
    public ResponseEntity<UserEntity> saveUser(@RequestBody UserEntity user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(user));
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserRequestDTO userDTO) {
        try {
            UserResponseDTO response = userService.registerUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    

    @GetMapping("/roles")
    public ResponseEntity<List<Map<String, Object>>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        System.out.println(roles);
        
        List<Map<String, Object>> response = roles.stream().map(role -> {
            Map<String, Object> roleMap = new HashMap<>();
            roleMap.put("id", role.getRoleId());
            roleMap.put("name", role.getRoleName());
            
            // Convertir el string JSON a objeto Map
            try {
                Map<String, Boolean> permissions = objectMapper.readValue(
                    role.getPermissions(), 
                    new TypeReference<Map<String, Boolean>>(){}
                );
                roleMap.put("permissions", permissions);
            } catch (Exception e) {
                // Manejar error
                roleMap.put("permissions", Map.of());
            }
            
            return roleMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/update-user/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDTO user, @PathVariable Long id) {
		try {
			UserResponseDTO updatedUser = userService.updateUser(id, user);
			return ResponseEntity.ok(updatedUser);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
    }
		
    
    
}


