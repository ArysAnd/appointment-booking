package com.example.appointmentbooking.appointment;

import com.example.appointmentbooking.appointment.dto.AppointmentCreateRequest;
import com.example.appointmentbooking.availability.AvailabilityRepository;
import com.example.appointmentbooking.availability.AvailabilitySlot;
import com.example.appointmentbooking.common.exception.ConflictException;
import com.example.appointmentbooking.service.ServiceItem;
import com.example.appointmentbooking.service.ServiceRepository;
import com.example.appointmentbooking.specialist.Specialist;
import com.example.appointmentbooking.specialist.SpecialistRepository;
import com.example.appointmentbooking.user.Role;
import com.example.appointmentbooking.user.User;
import com.example.appointmentbooking.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private SpecialistRepository specialistRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void shouldNotBookAlreadyBookedSlot() {
        User client = User.builder()
                .id(1L)
                .fullName("Test Client")
                .email("client@example.com")
                .password("password")
                .role(Role.CLIENT)
                .build();

        ServiceItem serviceItem = ServiceItem.builder()
                .id(1L)
                .name("Initial Consultation")
                .description("First consultation")
                .durationMinutes(30)
                .price(BigDecimal.valueOf(50))
                .build();

        Specialist specialist = Specialist.builder()
                .id(1L)
                .specialization("Cardiology")
                .services(Set.of(serviceItem))
                .build();

        AvailabilitySlot slot = AvailabilitySlot.builder()
                .id(1L)
                .specialist(specialist)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusMinutes(30))
                .booked(true)
                .build();

        AppointmentCreateRequest request = new AppointmentCreateRequest(
                1L,
                1L,
                1L
        );

        when(userRepository.findByEmail("client@example.com"))
                .thenReturn(Optional.of(client));

        when(serviceRepository.findById(1L))
                .thenReturn(Optional.of(serviceItem));

        when(specialistRepository.findById(1L))
                .thenReturn(Optional.of(specialist));

        when(availabilityRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(slot));

        assertThrows(
                ConflictException.class,
                () -> appointmentService.bookAppointment(request, "client@example.com")
        );

        verify(appointmentRepository, never()).save(any());
    }
}