package com.uad.services;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uad.dto.UserResponseDTO;
import com.uad.entities.UserEntity;
import com.uad.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll(); 
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
}
