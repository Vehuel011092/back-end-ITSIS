package com.uad.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Role {

 @Id 
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "role_id", columnDefinition = "BIGINT") 
 private Long roleId; // ✅ BIGINT en BD
 
 @Column(name = "role_name")
 private String roleName;
 
 @Column(name = "permissions", columnDefinition = "jsonb")
 private String permissions;

}
