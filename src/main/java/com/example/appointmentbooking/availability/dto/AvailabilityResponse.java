package com.example.appointmentbooking.availability.dto;

import java.time.LocalDateTime;

public record AvailabilityResponse(
        Long id,
        Long specialistId,
        String specialistName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean booked
) {
}