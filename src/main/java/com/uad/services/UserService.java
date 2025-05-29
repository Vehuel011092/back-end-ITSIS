package com.uad.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uad.dto.UserAuthResponseDTO;
import com.uad.dto.UserResponseDTO;
import com.uad.entities.UserEntity;
import com.uad.projection.RoleProjection;
import com.uad.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
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
    

    public UserResponseDTO registerUser(UserEntity user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        UserEntity savedUser = userRepository.save(user);
        return new UserResponseDTO(savedUser);
    }
    
    public UserResponseDTO updateUser(long id, UserEntity user) {
		UserEntity existingUser = userRepository.findById(id).orElse(null);
		Date currentDate = new Date();
		if (existingUser == null) {
			throw new RuntimeException("Usuario no encontrado");
		}
		existingUser.setName(user.getName());
		existingUser.setEmail(user.getEmail());
		existingUser.setStatus(user.getStatus());
		existingUser.setRoles(user.getRoles());
		existingUser.setUpdatedAt(currentDate);
		UserEntity updatedUser = userRepository.save(existingUser);
		return new UserResponseDTO(updatedUser);
	}
	
	public void deleteUser(long id) {
		UserEntity user = userRepository.findById(id).orElse(null);
		if (user == null) {
			throw new RuntimeException("Usuario no encontrado");
		}
		userRepository.delete(user);
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
