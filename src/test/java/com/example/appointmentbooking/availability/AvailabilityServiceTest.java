package com.example.appointmentbooking.availability;

import com.example.appointmentbooking.availability.dto.AvailabilityCreateRequest;
import com.example.appointmentbooking.common.exception.BadRequestException;
import com.example.appointmentbooking.common.exception.ConflictException;
import com.example.appointmentbooking.common.exception.ResourceNotFoundException;
import com.example.appointmentbooking.specialist.Specialist;
import com.example.appointmentbooking.specialist.SpecialistRepository;
import com.example.appointmentbooking.user.Role;
import com.example.appointmentbooking.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

	@Mock
	private AvailabilityRepository availabilityRepository;

	@Mock
	private SpecialistRepository specialistRepository;

	@InjectMocks
	private AvailabilityService availabilityService;

	@Test
	void shouldNotCreateAvailabilitySlotWhenSpecialistDoesNotExist() {
		AvailabilityCreateRequest request = new AvailabilityCreateRequest(
				999L,
				LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(1).plusMinutes(30)
		);

		when(specialistRepository.findById(999L))
				.thenReturn(Optional.empty());

		assertThrows(
				ResourceNotFoundException.class,
				() -> availabilityService.createAvailabilitySlot(request)
		);

		verify(availabilityRepository, never()).save(any());
	}

	@Test
	void shouldNotCreateAvailabilitySlotWhenStartTimeIsInPast() {
		User specialistUser = User.builder()
				.id(1L)
				.fullName("Dr. John Smith")
				.email("john.smith@example.com")
				.password("password")
				.role(Role.SPECIALIST)
				.build();

		Specialist specialist = Specialist.builder()
				.id(1L)
				.user(specialistUser)
				.specialization("Cardiology")
				.build();

		AvailabilityCreateRequest request = new AvailabilityCreateRequest(
				1L,
				LocalDateTime.now().minusDays(1),
				LocalDateTime.now().plusMinutes(30)
		);

		when(specialistRepository.findById(1L))
				.thenReturn(Optional.of(specialist));

		assertThrows(
				BadRequestException.class,
				() -> availabilityService.createAvailabilitySlot(request)
		);

		verify(availabilityRepository, never()).save(any());
	}

	@Test
	void shouldNotCreateAvailabilitySlotWhenEndTimeIsBeforeStartTime() {
		User specialistUser = User.builder()
				.id(1L)
				.fullName("Dr. John Smith")
				.email("john.smith@example.com")
				.password("password")
				.role(Role.SPECIALIST)
				.build();

		Specialist specialist = Specialist.builder()
				.id(1L)
				.user(specialistUser)
				.specialization("Cardiology")
				.build();

		LocalDateTime startTime = LocalDateTime.now().plusDays(1);
		LocalDateTime endTime = startTime.minusMinutes(30);

		AvailabilityCreateRequest request = new AvailabilityCreateRequest(
				1L,
				startTime,
				endTime
		);

		when(specialistRepository.findById(1L))
				.thenReturn(Optional.of(specialist));

		assertThrows(
				BadRequestException.class,
				() -> availabilityService.createAvailabilitySlot(request)
		);

		verify(availabilityRepository, never()).save(any());
	}

	@Test
	void shouldNotCreateOverlappingAvailabilitySlot() {
		User specialistUser = User.builder()
				.id(1L)
				.fullName("Dr. John Smith")
				.email("john.smith@example.com")
				.password("password")
				.role(Role.SPECIALIST)
				.build();

		Specialist specialist = Specialist.builder()
				.id(1L)
				.user(specialistUser)
				.specialization("Cardiology")
				.build();

		LocalDateTime startTime = LocalDateTime.now().plusDays(1);
		LocalDateTime endTime = startTime.plusMinutes(30);

		AvailabilityCreateRequest request = new AvailabilityCreateRequest(
				1L,
				startTime,
				endTime
		);

		when(specialistRepository.findById(1L))
				.thenReturn(Optional.of(specialist));

		when(availabilityRepository.countOverlappingSlots(1L, startTime, endTime))
				.thenReturn(1L);

		assertThrows(
				ConflictException.class,
				() -> availabilityService.createAvailabilitySlot(request)
		);

		verify(availabilityRepository, never()).save(any());
	}
}