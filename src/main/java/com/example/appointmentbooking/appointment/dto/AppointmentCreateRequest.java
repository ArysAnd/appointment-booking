package com.example.appointmentbooking.appointment.dto;

import jakarta.validation.constraints.NotNull;

public record AppointmentCreateRequest(
        @NotNull Long serviceId,
        @NotNull Long specialistId,
        @NotNull Long slotId
) {}