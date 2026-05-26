package com.example.appointmentbooking.availability;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findBySpecialistIdAndBookedFalseAndStartTimeAfterOrderByStartTimeAsc(
            Long specialistId,
            LocalDateTime currentTime
    );

    List<AvailabilitySlot> findBySpecialistIdOrderByStartTimeAsc(Long specialistId);

    @Query("""
            select count(slot)
            from AvailabilitySlot slot
            where slot.specialist.id = :specialistId
            and :startTime < slot.endTime
            and :endTime > slot.startTime
            """)
    long countOverlappingSlots(
            @Param("specialistId") Long specialistId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select slot from AvailabilitySlot slot where slot.id = :id")
    Optional<AvailabilitySlot> findByIdForUpdate(@Param("id") Long id);
}