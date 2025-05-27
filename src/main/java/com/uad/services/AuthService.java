package com.uad.services;

import com.uad.dto.UserAuthResponseDTO;
import com.uad.entities.UserEntity;
import com.uad.projection.RoleProjection;
import com.uad.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }
        return new User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    /*public UserEntity authenticateUser(String email, String password) {
        UserEntity user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }*/
    
    public UserAuthResponseDTO authenticateUser(String email, String password) {
        UserEntity user = userRepository.findByEmail(email);
        
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        
        List<RoleProjection> roles = userRepository.findUserRolesWithPermissions(email);
        if (roles.isEmpty()) {
            throw new RuntimeException("Usuario sin roles asignados");
        }
        
        // Tomamos el primer rol (asumiendo 1 rol por usuario)
        return new UserAuthResponseDTO(user, roles.get(0));
    }
}