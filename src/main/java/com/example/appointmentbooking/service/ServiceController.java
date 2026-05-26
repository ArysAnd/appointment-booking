package com.example.appointmentbooking.service;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.example.appointmentbooking.service.dto.ServiceCreateRequest;
import com.example.appointmentbooking.service.dto.ServiceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping("/api/services")
    public List<ServiceResponse> getAllServices() {
        return serviceService.getAllServices();
    }

    @GetMapping("/api/services/{id}")
    public ServiceResponse getServiceById(@PathVariable Long id) {
        return serviceService.getServiceById(id);
    }

    @PostMapping("/api/admin/services")
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceResponse createService(
            @Valid @RequestBody ServiceCreateRequest request
    ) {
        return serviceService.createService(request);
    }
}