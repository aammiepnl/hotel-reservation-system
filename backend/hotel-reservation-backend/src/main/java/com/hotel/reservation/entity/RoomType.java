package com.hotel.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "room_types")
@Data
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long typeId;

    private String typeName;
    private Double price;
    private Integer capacity;
    private String description;
    private String imageUrl;
}
