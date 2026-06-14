package com.mediai.service;

import com.mediai.entity.User;
import com.mediai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public boolean verifyPassword(String raw, String hashed) {
        return encoder.matches(raw, hashed);
    }

    public Optional<User> findById(long id) {
        return repo.findById(id);
    }
}
