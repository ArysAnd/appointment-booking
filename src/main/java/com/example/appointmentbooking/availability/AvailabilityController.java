package com.example.appointmentbooking.availability;

import com.example.appointmentbooking.availability.dto.AvailabilityCreateRequest;
import com.example.appointmentbooking.availability.dto.AvailabilityResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/api/availability/specialists/{specialistId}")
    public List<AvailabilityResponse> getAvailableSlotsForSpecialist(
            @PathVariable Long specialistId
    ) {
        return availabilityService.getAvailableSlotsForSpecialist(specialistId);
    }

    @GetMapping("/api/admin/availability/specialists/{specialistId}")
    public List<AvailabilityResponse> getAllSlotsForSpecialist(
            @PathVariable Long specialistId
    ) {
        return availabilityService.getAllSlotsForSpecialist(specialistId);
    }

    @PostMapping("/api/admin/availability")
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilityResponse createAvailabilitySlot(
            @Valid @RequestBody AvailabilityCreateRequest request
    ) {
        return availabilityService.createAvailabilitySlot(request);
    }
}
