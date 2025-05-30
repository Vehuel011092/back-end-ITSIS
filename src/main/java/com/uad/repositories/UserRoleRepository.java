package com.uad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uad.entities.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	
}