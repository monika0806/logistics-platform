package com.logistics.logisticsbackend;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentHistoryRepository shipmentHistoryRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ShipmentService(
            ShipmentRepository shipmentRepository,
            ShipmentHistoryRepository shipmentHistoryRepository,
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper) {

        this.shipmentRepository = shipmentRepository;
        this.shipmentHistoryRepository = shipmentHistoryRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // =========================================================
    // GET - Find shipment using tracking ID
    // Redis is checked first
    // =========================================================

    public Shipment getShipmentByTrackingId(String trackingId) {

    String key = "shipment:" + trackingId;

    try {

        // 1. Check Redis
        String cachedShipment = redisTemplate.opsForValue().get(key);

        if (cachedShipment != null) {

            System.out.println("REDIS CACHE HIT: " + trackingId);

            return objectMapper.readValue(
                    cachedShipment,
                    Shipment.class
            );
        }

        System.out.println("REDIS CACHE MISS: " + trackingId);

        // 2. If not in Redis, check PostgreSQL
        Shipment shipment = shipmentRepository
                .findByTrackingId(trackingId)
                .orElseThrow(
                        () -> new RuntimeException("Shipment not found")
                );

        // 3. Store in Redis
        String json = objectMapper.writeValueAsString(shipment);

        redisTemplate.opsForValue().set(key, json);

        System.out.println(
                "SHIPMENT STORED IN REDIS: " + trackingId
        );

        return shipment;

    } catch (RuntimeException e) {

        // IMPORTANT:
        // Do not convert "Shipment not found"
        // into "Error while accessing Redis".

        if ("Shipment not found".equals(e.getMessage())) {
            throw e;
        }

        throw new RuntimeException(
                "Error while accessing Redis",
                e
        );

    } catch (Exception e) {

        throw new RuntimeException(
                "Error while accessing Redis",
                e
        );
    }
}

    // =========================================================
    // POST - Create a new shipment
    // =========================================================

    @Transactional
    public Shipment createShipment(Shipment shipment) {

        // 1. Save shipment in PostgreSQL
        Shipment savedShipment = shipmentRepository.save(shipment);

        // 2. Create initial history record
        ShipmentHistory history = new ShipmentHistory();

        history.setTrackingId(
                savedShipment.getTrackingId());

        history.setStatus(
                savedShipment.getStatus());

        history.setLocation(
                savedShipment.getCurrentLocation());

        history.setEventTime(
                LocalDateTime.now());

        // 3. Save history
        shipmentHistoryRepository.save(history);

        // 4. Add new shipment to Redis
        try {

            String key = "shipment:" +
                    savedShipment.getTrackingId();

            String shipmentJson = objectMapper.writeValueAsString(
                    savedShipment);

            redisTemplate
                    .opsForValue()
                    .set(key, shipmentJson);

            System.out.println(
                    "NEW SHIPMENT STORED IN REDIS: "
                            + savedShipment.getTrackingId());

        } catch (Exception e) {

            System.out.println(
                    "Could not store shipment in Redis");
        }

        return savedShipment;
    }

    // =========================================================
    // PUT - Update shipment and create history
    // =========================================================

    @Transactional
    public Shipment updateShipment(
            String trackingId,
            Shipment updatedShipment) {

        // 1. Find existing shipment
        Shipment existingShipment = shipmentRepository
                .findByTrackingId(trackingId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Shipment not found"));

        // 2. Update status
        existingShipment.setStatus(
                updatedShipment.getStatus());

        // 3. Update location
        existingShipment.setCurrentLocation(
                updatedShipment.getCurrentLocation());

        // 4. Save updated shipment
        Shipment savedShipment = shipmentRepository.save(
                existingShipment);

        // 5. Create history record
        ShipmentHistory history = new ShipmentHistory();

        history.setTrackingId(
                savedShipment.getTrackingId());

        history.setStatus(
                savedShipment.getStatus());

        history.setLocation(
                savedShipment.getCurrentLocation());

        history.setEventTime(
                LocalDateTime.now());

        // 6. Save history
        shipmentHistoryRepository.save(history);

        // =====================================================
        // Update Redis cache
        // =====================================================

        try {

            String key = "shipment:" + trackingId;

            String shipmentJson = objectMapper.writeValueAsString(
                    savedShipment);

            redisTemplate
                    .opsForValue()
                    .set(key, shipmentJson);

            System.out.println(
                    "REDIS CACHE UPDATED: "
                            + trackingId);

        } catch (Exception e) {

            System.out.println(
                    "Could not update Redis cache");
        }

        // 7. Return updated shipment
        return savedShipment;
    }

    // =========================================================
    // GET - Shipment history
    // =========================================================

    public List<ShipmentHistory> getShipmentHistory(
            String trackingId) {

        return shipmentHistoryRepository
                .findByTrackingIdOrderByEventTimeAsc(
                        trackingId);
    }
}