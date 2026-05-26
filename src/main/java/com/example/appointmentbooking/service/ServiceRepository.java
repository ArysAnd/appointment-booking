package com.example.appointmentbooking.service;

import com.example.appointmentbooking.service.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<ServiceItem, Long> {
}