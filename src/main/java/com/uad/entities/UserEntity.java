package com.uad.entities;

import java.util.Date;
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
    
	@Column(name = "created_at")
    private Date createdAt;
	
	@Column(name = "name")
    private String name;
	
	@Column(name = "email")
    private String email;
    
	@Column(name = "password")
    private String password;
}
