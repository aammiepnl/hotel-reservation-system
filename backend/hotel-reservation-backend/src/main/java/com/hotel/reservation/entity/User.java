package com.hotel.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String fullName;
    private String email;
    private String phone;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Role {
        GUEST, RECEPTIONIST, ADMIN
    }
}