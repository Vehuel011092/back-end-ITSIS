package com.uad.services;

import com.uad.entities.Role;
import com.uad.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleService {
    
    private final RoleRepository roleRepository;

    // Inyección de dependencia por constructor
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll(); // Obtiene todos los roles de la base de datos
    }
}