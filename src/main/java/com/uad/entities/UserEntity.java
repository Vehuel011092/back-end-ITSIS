package com.uad.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
	    
	  @Column(name = "status")
	  private String status;
	  
	  @Column(name = "last_login")
	  private Date lastLogin;
	  
	  @Column(name = "updated_at")
	  private Date updatedAt;

	    // Relación muchos a muchos con roles
	  
	  @ManyToMany(fetch = FetchType.EAGER)
	    @JoinTable(
	        name = "user_roles",
	        joinColumns = @JoinColumn(name = "user_id"),
	        inverseJoinColumns = @JoinColumn(name = "role_id")
	    )
	    private Set<Role> roles = new HashSet<>();
}
