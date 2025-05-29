package com.uad.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.uad.entities.UserEntity;
import com.uad.projection.RoleProjection;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	@Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles")
    List<UserEntity> findAllWithRoles();
	 Optional<UserEntity> findById(Long id);
	 UserEntity findByEmail(String email);
	 UserEntity findByName(String name);
	 UserEntity findByEmailAndPassword(String email, String password);
	 boolean existsByEmail(String email); // <- Este es el que necesitamos
	 @Query("""
		        SELECT 
		            r.roleName as roleName, 
		            r.permissions as permissions 
		        FROM UserEntity u
		        JOIN u.roles r
		        WHERE u.email = :email
		    """)
		    List<RoleProjection> findUserRolesWithPermissions(@Param("email") String email);
}