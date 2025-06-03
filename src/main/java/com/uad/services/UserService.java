package com.uad.services;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uad.dto.RegisterUserRequestDTO;
import com.uad.dto.UserAuthResponseDTO;
import com.uad.dto.UserResponseDTO;
import com.uad.dto.UserUpdateDTO;
import com.uad.entities.Role;
import com.uad.entities.UserEntity;
import com.uad.projection.RoleProjection;
import com.uad.repositories.RoleRepository;
import com.uad.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private  RoleRepository roleRepository; // Asegúrate de tener este repositorio

    
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll(); 
    }
    
    public List<UserEntity> getAllUsersWithRoles() {
        return userRepository.findAllWithRoles(); // Usaremos un método personalizado
    }
    
    public UserEntity getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }
    
     
    @Transactional
    public UserResponseDTO registerUser(RegisterUserRequestDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Buscar el rol por nombre
        Role role = roleRepository.findByRoleName(userDTO.getRole())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + userDTO.getRole()));

        // Crear entidad de usuario
        UserEntity user = new UserEntity();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword()); // Será cifrado en @PrePersist
        user.setStatus(userDTO.getStatus());
        user.setRoles(Set.of(role)); // Asignar el rol encontrado

        UserEntity savedUser = userRepository.save(user);
        return new UserResponseDTO(savedUser);
    }
    
    public UserResponseDTO updateUser(long id, UserUpdateDTO userUpdate) {
        UserEntity existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        System.out.println("Usuario: " + userUpdate);
        
        // Actualizar campos básicos
        existingUser.setName(userUpdate.getName());
        existingUser.setEmail(userUpdate.getEmail());
        existingUser.setStatus(userUpdate.getStatus());
        
     // Convertir IDs a entidades Role
        Set<Role> roles = new HashSet<>();
        for (Long roleId : userUpdate.getRoleIds()) {
            Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleId));
            roles.add(role);
        }
        existingUser.setRoles(roles);
        
        existingUser.setUpdatedAt(new Date());
        return new UserResponseDTO(userRepository.save(existingUser));
    }
	
    @Transactional
    public ResponseEntity<?> deleteUserById(Long id) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "status", "error",
                    "message", "Usuario no encontrado con ID: " + id
                ));
        }
        
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Usuario eliminado correctamente",
                "deletedUserId", id
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "status", "error",
                    "message", "No se puede eliminar el usuario debido a restricciones de integridad",
                    "details", e.getMessage()
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Error al eliminar el usuario"
                ));
        }
    }
	public boolean userExists(String email) {
		return userRepository.existsByEmail(email);
	}
	
	public UserAuthResponseDTO getUserWithPermissions(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        
        // Obtener roles y permisos
        List<RoleProjection> roles = userRepository.findUserRolesWithPermissions(email);
        
        return new UserAuthResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getStatus(),
            roles.isEmpty() ? "Sin rol" : roles.get(0).getRoleName(),
            parsePermissions(roles)
        );
    }
    
    private Map<String, Boolean> parsePermissions(List<RoleProjection> roles) {
        Map<String, Boolean> combinedPermissions = new HashMap<>();
        for (RoleProjection role : roles) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Boolean> permMap = mapper.readValue(
                    role.getPermissions(), 
                    new TypeReference<Map<String, Boolean>>(){}
                );
                permMap.forEach((key, value) -> 
                    combinedPermissions.merge(key, value, (oldVal, newVal) -> oldVal || newVal)
                );
            } catch (JsonProcessingException e) {
                // Manejar error
            }
        }
        return combinedPermissions;
    }
}
