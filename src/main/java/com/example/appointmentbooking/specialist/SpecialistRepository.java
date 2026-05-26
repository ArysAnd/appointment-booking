package com.example.appointmentbooking.specialist;

import com.example.appointmentbooking.specialist.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
    Optional<Specialist> findByUserEmail(String email);
}