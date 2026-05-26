package com.example.appointmentbooking.appointment;

import com.example.appointmentbooking.appointment.dto.AppointmentCreateRequest;
import com.example.appointmentbooking.appointment.dto.AppointmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/api/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse bookAppointment(
            @Valid @RequestBody AppointmentCreateRequest request,
            Principal principal
    ) {
        return appointmentService.bookAppointment(request, principal.getName());
    }

    @GetMapping("/api/appointments/my")
    public List<AppointmentResponse> getMyAppointments(Principal principal) {
        return appointmentService.getMyAppointments(principal.getName());
    }

    @GetMapping("/api/specialist/appointments")
    public List<AppointmentResponse> getSpecialistAppointments(Principal principal) {
        return appointmentService.getSpecialistAppointments(principal.getName());
    }

    @PatchMapping("/api/appointments/{appointmentId}/cancel")
    public AppointmentResponse cancelAppointment(
            @PathVariable Long appointmentId,
            Principal principal
    ) {
        return appointmentService.cancelAppointment(appointmentId, principal.getName());
    }
}