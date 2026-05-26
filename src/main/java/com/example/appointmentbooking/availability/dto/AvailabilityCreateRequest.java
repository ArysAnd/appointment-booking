package com.example.appointmentbooking.availability.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AvailabilityCreateRequest(
        @NotNull Long specialistId,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime
) {}