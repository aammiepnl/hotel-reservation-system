package com.hotel.reservation.controller;

import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.entity.Room;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    // 1. ดูรายการ arrivals วันนี้ (Check-in วันนี้)
    @GetMapping("/arrivals")
    public List<Reservation> getTodayArrivals() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findAll().stream()
                .filter(r -> r.getCheckInDate().equals(today)
                        && r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                .toList();
    }

    // 2. ดูรายการ departures วันนี้ (Check-out วันนี้)
    @GetMapping("/departures")
    public List<Reservation> getTodayDepartures() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findAll().stream()
                .filter(r -> r.getCheckOutDate().equals(today)
                        && r.getStatus() == Reservation.ReservationStatus.CHECKED_IN)
                .toList();
    }

    // 3. Check-in Guest
    @PutMapping("/checkin/{reservationId}")
    public ResponseEntity<?> checkIn(@PathVariable Long reservationId) {
        Optional<Reservation> resOpt = reservationRepository.findById(reservationId);
        if (resOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Reservation not found");
        }

        Reservation reservation = resOpt.get();
        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING
                && reservation.getStatus() != Reservation.ReservationStatus.CONFIRMED) {
            return ResponseEntity.badRequest().body("Reservation cannot be checked in");
        }

        reservation.setStatus(Reservation.ReservationStatus.CHECKED_IN);
        reservationRepository.save(reservation);

        Room room = reservation.getRoom();
        room.setStatus(Room.RoomStatus.OCCUPIED);
        roomRepository.save(room);

        return ResponseEntity.ok("Check-in successful");
    }

    // 4. Check-out Guest
    @PutMapping("/checkout/{reservationId}")
    public ResponseEntity<?> checkOut(@PathVariable Long reservationId) {
        Optional<Reservation> resOpt = reservationRepository.findById(reservationId);
        if (resOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Reservation not found");
        }

        Reservation reservation = resOpt.get();
        if (reservation.getStatus() != Reservation.ReservationStatus.CHECKED_IN) {
            return ResponseEntity.badRequest().body("Guest has not checked in yet");
        }

        reservation.setStatus(Reservation.ReservationStatus.CHECKED_OUT);
        reservationRepository.save(reservation);

        Room room = reservation.getRoom();
        room.setStatus(Room.RoomStatus.CLEANING);
        roomRepository.save(room);

        return ResponseEntity.ok("Check-out successful");
    }
}