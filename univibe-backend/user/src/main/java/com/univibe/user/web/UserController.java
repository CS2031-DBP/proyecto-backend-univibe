package com.univibe.user.web;

import com.univibe.user.model.User;
import com.univibe.user.model.Role;
import com.univibe.user.repo.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> list() {
        return userRepository.findAll();
    }

    @PostMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateRole(@PathVariable Long userId, @RequestParam Role role) {
        User u = userRepository.findById(userId).orElseThrow();
        u.setRole(role);
        return userRepository.save(u);
    }
}
