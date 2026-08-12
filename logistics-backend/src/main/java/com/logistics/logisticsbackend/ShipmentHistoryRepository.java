package com.logistics.logisticsbackend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentHistoryRepository
        extends JpaRepository<ShipmentHistory, Long> {

    List<ShipmentHistory> findByTrackingIdOrderByEventTimeAsc(
            String trackingId);
}