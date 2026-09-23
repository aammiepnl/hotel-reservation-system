package com.hotel.reservation.controller;

import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.entity.Room;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.RoomRepository;
import com.hotel.reservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. ดูรายงานสรุป (Occupancy Rate + Revenue)
    @GetMapping("/report")
    public Map<String, Object> getReport() {
        Map<String, Object> report = new HashMap<>();

        // จำนวนห้องทั้งหมด
        long totalRooms = roomRepository.count();
        report.put("totalRooms", totalRooms);

        // จำนวนห้องที่ไม่ว่าง
        long occupiedRooms = roomRepository.findByStatus(Room.RoomStatus.OCCUPIED).size();
        report.put("occupiedRooms", occupiedRooms);

        // Occupancy Rate
        double occupancyRate = totalRooms > 0 ? (occupiedRooms * 100.0 / totalRooms) : 0.0;
        report.put("occupancyRate", String.format("%.2f%%", occupancyRate));

        // รายได้รวม (จากทุกการจองที่ไม่ถูกยกเลิก)
        List<Reservation> allReservations = reservationRepository.findAll();
        double totalRevenue = allReservations.stream()
                .filter(r -> r.getStatus() != Reservation.ReservationStatus.CANCELLED)
                .mapToDouble(Reservation::getTotalPrice)
                .sum();
        report.put("totalRevenue", totalRevenue);

        // จำนวนผู้ใช้ทั้งหมด
        report.put("totalUsers", userRepository.count());

        // จำนวนการจองทั้งหมด
        report.put("totalReservations", allReservations.size());

        return report;
    }

    // 2. ดูรายการห้องทั้งหมดพร้อมสถานะ
    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}