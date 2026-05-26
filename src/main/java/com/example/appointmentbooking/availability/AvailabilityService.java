package com.example.appointmentbooking.availability;

import com.example.appointmentbooking.availability.dto.AvailabilityCreateRequest;
import com.example.appointmentbooking.availability.dto.AvailabilityResponse;
import com.example.appointmentbooking.common.exception.BadRequestException;
import com.example.appointmentbooking.common.exception.ConflictException;
import com.example.appointmentbooking.common.exception.ResourceNotFoundException;
import com.example.appointmentbooking.specialist.Specialist;
import com.example.appointmentbooking.specialist.SpecialistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final SpecialistRepository specialistRepository;

    @Transactional
    public AvailabilityResponse createAvailabilitySlot(AvailabilityCreateRequest request) {
        Specialist specialist = specialistRepository.findById(request.specialistId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found"));

        validateTimeRange(request.startTime(), request.endTime());

        long overlappingSlots = availabilityRepository.countOverlappingSlots(
                specialist.getId(),
                request.startTime(),
                request.endTime()
        );

        if (overlappingSlots > 0) {
            throw new ConflictException("This availability slot overlaps with an existing slot");
        }

        AvailabilitySlot availabilitySlot = AvailabilitySlot.builder()
                .specialist(specialist)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .booked(false)
                .build();

        AvailabilitySlot savedSlot = availabilityRepository.save(availabilitySlot);

        return mapToResponse(savedSlot);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAvailableSlotsForSpecialist(Long specialistId) {
        if (!specialistRepository.existsById(specialistId)) {
            throw new ResourceNotFoundException("Specialist not found");
        }

        return availabilityRepository
                .findBySpecialistIdAndBookedFalseAndStartTimeAfterOrderByStartTimeAsc(
                        specialistId,
                        LocalDateTime.now()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAllSlotsForSpecialist(Long specialistId) {
        if (!specialistRepository.existsById(specialistId)) {
            throw new ResourceNotFoundException("Specialist not found");
        }

        return availabilityRepository
                .findBySpecialistIdOrderByStartTimeAsc(specialistId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start time must be in the future");
        }

        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException("End time must be after start time");
        }
    }

    private AvailabilityResponse mapToResponse(AvailabilitySlot slot) {
        return new AvailabilityResponse(
                slot.getId(),
                slot.getSpecialist().getId(),
                slot.getSpecialist().getUser().getFullName(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.isBooked()
        );
    }
}
