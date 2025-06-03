package com.uad.repositories;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uad.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByRoleName(String roleName);
	// Método para buscar roles por IDs
    @Query("SELECT r FROM Role r WHERE r.id IN :ids")
    Set<Role> findByIds(@Param("ids") Set<Long> ids);
}