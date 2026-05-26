package com.example.appointmentbooking.auth.dto;

public record AuthResponse(
        String token,
        String email,
        String role
) {}