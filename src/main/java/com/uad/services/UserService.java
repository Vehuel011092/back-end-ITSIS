package com.uad.services;

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
}
