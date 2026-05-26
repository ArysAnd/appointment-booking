package com.example.appointmentbooking.specialist;

import com.example.appointmentbooking.service.ServiceItem;
import com.example.appointmentbooking.service.ServiceRepository;
import com.example.appointmentbooking.specialist.dto.SpecialistCreateRequest;
import com.example.appointmentbooking.specialist.dto.SpecialistResponse;
import com.example.appointmentbooking.user.Role;
import com.example.appointmentbooking.user.User;
import com.example.appointmentbooking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;

    public SpecialistResponse createSpecialist(SpecialistCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("User with this email already exists");
        }

        List<ServiceItem> services = serviceRepository.findAllById(request.serviceIds());

        if (services.size() != request.serviceIds().size()) {
            throw new RuntimeException("One or more services were not found");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.SPECIALIST)
                .build();

        User savedUser = userRepository.save(user);

        Specialist specialist = Specialist.builder()
                .user(savedUser)
                .specialization(request.specialization())
                .services(new HashSet<>(services))
                .build();

        Specialist savedSpecialist = specialistRepository.save(specialist);

        return mapToResponse(savedSpecialist);
    }

    public List<SpecialistResponse> getAllSpecialists() {
        return specialistRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SpecialistResponse getSpecialistById(Long id) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));

        return mapToResponse(specialist);
    }

    private SpecialistResponse mapToResponse(Specialist specialist) {
        Set<String> serviceNames = specialist.getServices()
                .stream()
                .map(ServiceItem::getName)
                .collect(java.util.stream.Collectors.toSet());

        return new SpecialistResponse(
                specialist.getId(),
                specialist.getUser().getFullName(),
                specialist.getUser().getEmail(),
                specialist.getSpecialization(),
                serviceNames
        );
    }
}