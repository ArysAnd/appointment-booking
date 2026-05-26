package com.example.appointmentbooking.specialist.dto;

import java.util.Set;

public record SpecialistResponse(
        Long id,
        String fullName,
        String email,
        String specialization,
        Set<String> services
) {
}