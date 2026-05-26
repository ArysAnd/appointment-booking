package com.example.appointmentbooking.service;

import com.example.appointmentbooking.common.exception.ResourceNotFoundException;
import com.example.appointmentbooking.service.dto.ServiceCreateRequest;
import com.example.appointmentbooking.service.dto.ServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceResponse createService(ServiceCreateRequest request) {
        ServiceItem serviceItem = ServiceItem.builder()
                .name(request.name())
                .description(request.description())
                .durationMinutes(request.durationMinutes())
                .price(request.price())
                .build();

        ServiceItem savedService = serviceRepository.save(serviceItem);

        return mapToResponse(savedService);
    }

    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ServiceResponse getServiceById(Long id) {
        ServiceItem serviceItem = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        return mapToResponse(serviceItem);
    }

    private ServiceResponse mapToResponse(ServiceItem serviceItem) {
        return new ServiceResponse(
                serviceItem.getId(),
                serviceItem.getName(),
                serviceItem.getDescription(),
                serviceItem.getDurationMinutes(),
                serviceItem.getPrice()
        );
    }
}