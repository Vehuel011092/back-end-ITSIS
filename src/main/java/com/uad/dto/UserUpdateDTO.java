package com.uad.dto;

import java.util.Set;
import lombok.Data;

//UserUpdateDTO.java
@Data
public class UserUpdateDTO {
 private String name;
 private String email;
 private String status;
 private Set<Long> roleIds; // Solo IDs de roles
}