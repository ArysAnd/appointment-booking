package com.example.appointmentbooking.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientEmailOrderByCreatedAtDesc(String email);

    List<Appointment> findBySpecialistUserEmailOrderByCreatedAtDesc(String email);

    boolean existsBySlotIdAndStatus(Long slotId, AppointmentStatus status);
}