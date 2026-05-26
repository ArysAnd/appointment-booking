package com.example.appointmentbooking.service.dto;

import java.math.BigDecimal;

public record ServiceResponse(
        Long id,
        String name,
        String description,
        Integer durationMinutes,
        BigDecimal price
) {
}