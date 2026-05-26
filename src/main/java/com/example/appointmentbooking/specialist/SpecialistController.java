package com.example.appointmentbooking.specialist;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.example.appointmentbooking.specialist.dto.SpecialistCreateRequest;
import com.example.appointmentbooking.specialist.dto.SpecialistResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SpecialistController {

    private final SpecialistService specialistService;

    @GetMapping("/api/specialists")
    public List<SpecialistResponse> getAllSpecialists() {
        return specialistService.getAllSpecialists();
    }

    @GetMapping("/api/specialists/{id}")
    public SpecialistResponse getSpecialistById(@PathVariable Long id) {
        return specialistService.getSpecialistById(id);
    }

    @PostMapping("/api/admin/specialists")
    @ResponseStatus(HttpStatus.CREATED)
    public SpecialistResponse createSpecialist(
            @Valid @RequestBody SpecialistCreateRequest request
    ) {
        return specialistService.createSpecialist(request);
    }
}