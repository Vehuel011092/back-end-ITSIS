package com.uad.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.uad.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	 Optional<UserEntity> findById(Long id);
	 UserEntity findByEmail(String email);
	 UserEntity findByName(String name);
	 UserEntity findByEmailAndPassword(String email, String password);
	 boolean existsByEmail(String email); // <- Este es el que necesitamos
}