package com.mediai.service;

import com.mediai.entity.Admin;
import com.mediai.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {
    @Autowired
    private AdminRepository repo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Admin findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public boolean verifyPassword(String raw, String hashed) {
        return encoder.matches(raw, hashed);
    }

    public void save(Admin a) {
        a.setPassword(encoder.encode(a.getPassword()));
        repo.save(a);
    }

    public List<Admin> findAll() {
        return repo.findAll();
    }
}
