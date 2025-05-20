package com.uad.entities;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "users")
@JsonInclude(Include.NON_NULL)
@Data
public class UserEntity {
    
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    
	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp  // Anotación especial de Hibernate
    private Date createdAt;
	
	@Column(name = "name")
    private String name;
	
	@Column(name = "email")
    private String email;
    
	 @Column(nullable = false)
	    private String password;

	  // Método para cifrar el password antes de guardar
	    @PrePersist
	    public void prePersist() {
	        if (this.password != null && !this.password.startsWith("$2a$")) {
	            this.password = new BCryptPasswordEncoder().encode(this.password);
	        }
	        this.createdAt = new Date(); 
	    }
}
