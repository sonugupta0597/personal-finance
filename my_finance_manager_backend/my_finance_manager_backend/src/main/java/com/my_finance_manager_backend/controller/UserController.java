package com.my_finance_manager_backend.controller;

import com.my_finance_manager_backend.model.User;
import com.my_finance_manager_backend.repository.UserRepository;
import com.my_finance_manager_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")

public class UserController {

    private final UserRepository userRepo;
    private final UserService service;

    public UserController(UserRepository userRepo, UserService service) {
        this.userRepo = userRepo;
        this.service = service;
    }

    // Login user
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Optional<User> user = userRepo.findByUsernameAndPassword(username, password);
        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "userId", user.get().getId(),
                    "username", user.get().getUsername()
            ));
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }


    // Register
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        // Log the received user data for debugging
        System.out.println("Received registration request:");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Password: " + (user.getPassword() != null ? "[PROVIDED]" : "NULL"));
        
        // Additional validation
        if (user == null) {
            System.out.println("ERROR: User object is null");
            return ResponseEntity.badRequest().build();
        }
        
        if (user.getUsername() == null) {
            System.out.println("ERROR: Username is null in request");
            return ResponseEntity.badRequest().body(null);
        }
        
        if (user.getPassword() == null) {
            System.out.println("ERROR: Password is null in request");
            return ResponseEntity.badRequest().body(null);
        }
        
        return ResponseEntity.ok(service.register(user));
    }



    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        User u = service.getById(id);
        return (u != null) ? ResponseEntity.ok(u) : ResponseEntity.notFound().build();
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
