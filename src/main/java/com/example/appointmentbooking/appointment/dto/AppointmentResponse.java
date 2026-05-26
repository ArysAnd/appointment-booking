package com.example.appointmentbooking.appointment.dto;

import com.example.appointmentbooking.appointment.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        String clientName,
        String clientEmail,
        Long specialistId,
        String specialistName,
        Long serviceId,
        String serviceName,
        Long slotId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        LocalDateTime createdAt
) {
}