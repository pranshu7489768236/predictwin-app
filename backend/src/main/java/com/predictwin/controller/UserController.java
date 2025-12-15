package com.predictwin.controller;

import com.predictwin.model.User;
import com.predictwin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body("Unauthorized");
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("User not found");
        java.util.Map<String,Object> resp = new java.util.HashMap<>();
        resp.put("id", user.getId());
        resp.put("username", user.getUsername());
        return ResponseEntity.ok(resp);
    }
}
