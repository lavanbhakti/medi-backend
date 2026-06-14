package com.mediai.controller;

import com.mediai.entity.Admin;
import com.mediai.entity.User;
import com.mediai.security.JwtUtil;
import com.mediai.service.AdminService;
import com.mediai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private AdminService adminService;
    @Autowired private JwtUtil jwtUtil;

    // ── USER REGISTER ─────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (userService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
        }
        User u = new User();
        u.setName(body.get("name"));
        u.setEmail(email);
        u.setPassword(body.get("password"));
        u.setPhone(body.getOrDefault("phone", ""));
        userService.register(u);
        return ResponseEntity.ok(Map.of("success", true, "message", "Registered successfully!"));
    }

    // ── USER LOGIN ────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody Map<String, String> body) {
        Optional<User> userOpt = userService.findByEmail(body.get("email"));
        if (userOpt.isPresent() && userService.verifyPassword(body.get("password"), userOpt.get().getPassword())) {
            User u = userOpt.get();
            String token = jwtUtil.generateToken(u.getEmail(), "USER");
            return ResponseEntity.ok(Map.of(
                "success", true, "token", token,
                "email", u.getEmail(), "name", u.getName(),
                "id", u.getId(), "role", "USER"
            ));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
    }

    // ── ADMIN LOGIN ───────────────────────────────────────────
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> body) {
        Admin admin = adminService.findByEmail(body.get("email"));
        if (admin != null && adminService.verifyPassword(body.get("password"), admin.getPassword())) {
            String token = jwtUtil.generateToken(admin.getEmail(), "ADMIN");
            return ResponseEntity.ok(Map.of(
                "success", true, "token", token,
                "email", admin.getEmail(), "role", "ADMIN"
            ));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid admin credentials"));
    }

    // ── ADMIN REGISTER (first time setup) ────────────────────
    @PostMapping("/admin/register")
    public ResponseEntity<?> adminRegister(@RequestBody Map<String, String> body) {
        Admin a = new Admin();
        a.setEmail(body.get("email"));
        a.setPassword(body.get("password"));
        adminService.save(a);
        return ResponseEntity.ok(Map.of("success", true, "message", "Admin registered"));
    }
}
