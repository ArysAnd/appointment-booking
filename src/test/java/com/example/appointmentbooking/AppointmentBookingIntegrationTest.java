package com.example.appointmentbooking;

import com.example.appointmentbooking.auth.dto.AuthResponse;
import com.example.appointmentbooking.auth.dto.LoginRequest;
import com.example.appointmentbooking.auth.dto.RegisterRequest;
import com.example.appointmentbooking.availability.dto.AvailabilityCreateRequest;
import com.example.appointmentbooking.availability.dto.AvailabilityResponse;
import com.example.appointmentbooking.appointment.dto.AppointmentCreateRequest;
import com.example.appointmentbooking.appointment.dto.AppointmentResponse;
import com.example.appointmentbooking.service.dto.ServiceCreateRequest;
import com.example.appointmentbooking.service.dto.ServiceResponse;
import com.example.appointmentbooking.specialist.dto.SpecialistCreateRequest;
import com.example.appointmentbooking.specialist.dto.SpecialistResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class AppointmentBookingIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCompleteFullAppointmentBookingFlow() {
        String baseUrl = "http://localhost:" + port;

        AuthResponse adminAuth = login(
                baseUrl,
                new LoginRequest("admin@example.com", "admin123")
        );

        assertThat(adminAuth.token()).isNotBlank();
        assertThat(adminAuth.role()).isEqualTo("ADMIN");

        ServiceResponse service = createService(baseUrl, adminAuth.token());

        assertThat(service.id()).isNotNull();
        assertThat(service.name()).isEqualTo("Initial Consultation");

        SpecialistResponse specialist = createSpecialist(
                baseUrl,
                adminAuth.token(),
                service.id()
        );

        assertThat(specialist.id()).isNotNull();
        assertThat(specialist.fullName()).isEqualTo("Dr. John Smith");

        AvailabilityResponse slot = createAvailability(
                baseUrl,
                adminAuth.token(),
                specialist.id()
        );

        assertThat(slot.id()).isNotNull();
        assertThat(slot.booked()).isFalse();

        AuthResponse clientAuth = registerClient(baseUrl);

        assertThat(clientAuth.token()).isNotBlank();
        assertThat(clientAuth.role()).isEqualTo("CLIENT");

        AppointmentResponse appointment = bookAppointment(
                baseUrl,
                clientAuth.token(),
                service.id(),
                specialist.id(),
                slot.id()
        );

        assertThat(appointment.id()).isNotNull();
        assertThat(appointment.status().name()).isEqualTo("BOOKED");

        AppointmentResponse[] myAppointments = getMyAppointments(
                baseUrl,
                clientAuth.token()
        );

        assertThat(myAppointments).hasSize(1);
        assertThat(myAppointments[0].id()).isEqualTo(appointment.id());

        AppointmentResponse cancelledAppointment = cancelAppointment(
                baseUrl,
                clientAuth.token(),
                appointment.id()
        );

        assertThat(cancelledAppointment.status().name()).isEqualTo("CANCELLED");
    }

    private AuthResponse login(String baseUrl, LoginRequest request) {
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/login",
                request,
                AuthResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        return response.getBody();
    }

    private AuthResponse registerClient(String baseUrl) {
        RegisterRequest request = new RegisterRequest(
                "Integration Test Client",
                "integration-client@example.com",
                "password123"
        );

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/register",
                request,
                AuthResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        return response.getBody();
    }

    private ServiceResponse createService(String baseUrl, String adminToken) {
        ServiceCreateRequest request = new ServiceCreateRequest(
                "Initial Consultation",
                "First consultation with a specialist",
                30,
                BigDecimal.valueOf(50.00)
        );

        HttpEntity<ServiceCreateRequest> entity = new HttpEntity<>(
                request,
                bearerHeaders(adminToken)
        );

        ResponseEntity<ServiceResponse> response = restTemplate.exchange(
                baseUrl + "/api/admin/services",
                HttpMethod.POST,
                entity,
                ServiceResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        return response.getBody();
    }

    private SpecialistResponse createSpecialist(
            String baseUrl,
            String adminToken,
            Long serviceId
    ) {
        SpecialistCreateRequest request = new SpecialistCreateRequest(
                "Dr. John Smith",
                "john.integration@example.com",
                "password123",
                "Cardiology",
                Set.of(serviceId)
        );

        HttpEntity<SpecialistCreateRequest> entity = new HttpEntity<>(
                request,
                bearerHeaders(adminToken)
        );

        ResponseEntity<SpecialistResponse> response = restTemplate.exchange(
                baseUrl + "/api/admin/specialists",
                HttpMethod.POST,
                entity,
                SpecialistResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        return response.getBody();
    }

    private AvailabilityResponse createAvailability(
            String baseUrl,
            String adminToken,
            Long specialistId
    ) {
        AvailabilityCreateRequest request = new AvailabilityCreateRequest(
                specialistId,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(7).plusMinutes(30)
        );

        HttpEntity<AvailabilityCreateRequest> entity = new HttpEntity<>(
                request,
                bearerHeaders(adminToken)
        );

        ResponseEntity<AvailabilityResponse> response = restTemplate.exchange(
                baseUrl + "/api/admin/availability",
                HttpMethod.POST,
                entity,
                AvailabilityResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        return response.getBody();
    }

    private AppointmentResponse bookAppointment(
            String baseUrl,
            String clientToken,
            Long serviceId,
            Long specialistId,
            Long slotId
    ) {
        AppointmentCreateRequest request = new AppointmentCreateRequest(
                serviceId,
                specialistId,
                slotId
        );

        HttpEntity<AppointmentCreateRequest> entity = new HttpEntity<>(
                request,
                bearerHeaders(clientToken)
        );

        ResponseEntity<AppointmentResponse> response = restTemplate.exchange(
                baseUrl + "/api/appointments",
                HttpMethod.POST,
                entity,
                AppointmentResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        return response.getBody();
    }

    private AppointmentResponse[] getMyAppointments(
            String baseUrl,
            String clientToken
    ) {
        HttpEntity<Void> entity = new HttpEntity<>(bearerHeaders(clientToken));

        ResponseEntity<AppointmentResponse[]> response = restTemplate.exchange(
                baseUrl + "/api/appointments/my",
                HttpMethod.GET,
                entity,
                AppointmentResponse[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        return response.getBody();
    }

    private AppointmentResponse cancelAppointment(
            String baseUrl,
            String clientToken,
            Long appointmentId
    ) {
        HttpEntity<Void> entity = new HttpEntity<>(bearerHeaders(clientToken));

        ResponseEntity<AppointmentResponse> response = restTemplate.exchange(
                baseUrl + "/api/appointments/" + appointmentId + "/cancel",
                HttpMethod.PATCH,
                entity,
                AppointmentResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        return response.getBody();
    }

    private HttpHeaders bearerHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }
}