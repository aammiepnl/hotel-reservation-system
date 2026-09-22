package com.hotel.reservation.controller;

import com.hotel.reservation.entity.User;
import com.hotel.reservation.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // API สำหรับสมัครสมาชิก
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        // ในระบบจริงต้องเข้ารหัสรหัสผ่าน แต่เพื่อการศึกษาเราจะเก็บแบบธรรมดาก่อน
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // API สำหรับเข้าสู่ระบบ
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return ResponseEntity.ok(userOpt.get());
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }
}