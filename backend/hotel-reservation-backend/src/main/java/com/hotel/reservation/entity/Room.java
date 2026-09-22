package com.hotel.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rooms")
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(unique = true, nullable = false)
    private String roomNumber;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private RoomType roomType;

    private Integer floor;

    @Enumerated(EnumType.STRING)
    private RoomStatus status = RoomStatus.AVAILABLE;

    public enum RoomStatus {
        AVAILABLE, OCCUPIED, CLEANING, MAINTENANCE
    }
}
