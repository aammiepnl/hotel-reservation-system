CREATE DATABASE hotel_reservation;
USE hotel_reservation;

-- 1. ตาราง users: เก็บข้อมูลผู้ใช้ทั้งหมด (Guest, Receptionist, Admin)
CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role ENUM('GUEST','RECEPTIONIST','ADMIN') NOT NULL,
  full_name VARCHAR(100),
  email VARCHAR(100),
  phone VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. ตาราง room_types: เก็บข้อมูลประเภทห้องและราคา
CREATE TABLE room_types (
  type_id INT AUTO_INCREMENT PRIMARY KEY,
  type_name VARCHAR(50) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  capacity INT NOT NULL,
  description TEXT,
  image_url VARCHAR(255)
);

-- 3. ตาราง rooms: เก็บข้อมูลห้องแต่ละห้องและสถานะ
CREATE TABLE rooms (
  room_id INT AUTO_INCREMENT PRIMARY KEY,
  room_number VARCHAR(10) UNIQUE NOT NULL,
  type_id INT NOT NULL,
  floor INT,
  status ENUM('AVAILABLE','OCCUPIED','CLEANING','MAINTENANCE') DEFAULT 'AVAILABLE',
  FOREIGN KEY (type_id) REFERENCES room_types(type_id)
);

-- 4. ตาราง reservations: เก็บข้อมูลการจองห้องพัก
CREATE TABLE reservations (
  reservation_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  room_id INT NOT NULL,
  check_in_date DATE NOT NULL,
  check_out_date DATE NOT NULL,
  status ENUM('PENDING','CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') DEFAULT 'PENDING',
  total_price DECIMAL(10,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

-- 5. ตาราง payments: เก็บข้อมูลการชำระเงิน
CREATE TABLE payments (
  payment_id INT AUTO_INCREMENT PRIMARY KEY,
  reservation_id INT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  method ENUM('CASH','CREDIT_CARD','BANK_TRANSFER','E_WALLET') DEFAULT 'CASH',
  status ENUM('PENDING','PAID','REFUNDED') DEFAULT 'PENDING',
  paid_at TIMESTAMP NULL,
  FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
);

-- 6. ตาราง reviews: เก็บข้อมูลรีวิวจากลูกค้า
CREATE TABLE reviews (
  review_id INT AUTO_INCREMENT PRIMARY KEY,
  reservation_id INT NOT NULL,
  rating INT CHECK (rating BETWEEN 1 AND 5),
  comment TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
);