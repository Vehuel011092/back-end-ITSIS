package com.uad.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uad.dto.UserResponseDTO;
import com.uad.entities.UserEntity;
import com.uad.services.UserService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable long id) {
		UserEntity user = userService.getUserById(id);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
    
    
    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @PostMapping()
    public ResponseEntity<UserEntity> saveUser(@RequestBody UserEntity user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(user));
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserEntity user) {
        try {
            UserResponseDTO response = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    
    
}


