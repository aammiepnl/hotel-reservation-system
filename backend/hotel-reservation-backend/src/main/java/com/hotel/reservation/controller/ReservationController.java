package com.hotel.reservation.controller;
import com.hotel.reservation.repository.UserRepository;

import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.entity.Room;
import com.hotel.reservation.entity.User;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.RoomRepository;
import com.hotel.reservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;

    // 1. ค้นหาห้องว่าง
    @GetMapping("/available")
    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(Room.RoomStatus.AVAILABLE);
    }

    // 2. สร้างการจอง
    @PostMapping("/book")
    public ResponseEntity<?> bookRoom(@RequestBody Map<String, String> request) {
        Long userId = Long.parseLong(request.get("userId"));
        Long roomId = Long.parseLong(request.get("roomId"));
        LocalDate checkIn = LocalDate.parse(request.get("checkInDate"));
        LocalDate checkOut = LocalDate.parse(request.get("checkOutDate"));

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Room> roomOpt = roomRepository.findById(roomId);

        if (userOpt.isEmpty() || roomOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User or Room not found");
        }

        Room room = roomOpt.get();
        if (room.getStatus() != Room.RoomStatus.AVAILABLE) {
            return ResponseEntity.badRequest().body("Room is not available");
        }

        // คำนวณราคา
        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (days <= 0) {
            return ResponseEntity.badRequest().body("Check-out date must be after check-in date");
        }
        double totalPrice = days * room.getRoomType().getPrice();

        Reservation reservation = new Reservation();
        reservation.setUser(userOpt.get());
        reservation.setRoom(room);
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setTotalPrice(totalPrice);

        // อัปเดตสถานะห้อง
        room.setStatus(Room.RoomStatus.OCCUPIED);
        roomRepository.save(room);

        Reservation saved = reservationRepository.save(reservation);
        return ResponseEntity.ok(saved);
    }

    // 3. ดูประวัติการจองของ User
    @GetMapping("/user/{userId}")
    public List<Reservation> getUserReservations(@PathVariable Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    // 4. ยกเลิกการจอง
    @DeleteMapping("/cancel/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId) {
        Optional<Reservation> resOpt = reservationRepository.findById(reservationId);
        if (resOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Reservation not found");
        }

        Reservation reservation = resOpt.get();
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);

        // คืนสถานะห้อง
        Room room = reservation.getRoom();
        room.setStatus(Room.RoomStatus.AVAILABLE);
        roomRepository.save(room);

        reservationRepository.save(reservation);
        return ResponseEntity.ok("Reservation cancelled successfully");
    }
}