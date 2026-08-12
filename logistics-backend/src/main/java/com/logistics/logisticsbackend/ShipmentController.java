package com.logistics.logisticsbackend;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping("/api/shipments/{trackingId}")
    public Shipment getShipment(@PathVariable String trackingId) {

        return shipmentService.getShipmentByTrackingId(trackingId);
    }

    @PostMapping("/api/shipments")
    public Shipment createShipment(@RequestBody Shipment shipment) {

        return shipmentService.createShipment(shipment);
    }

    @PutMapping("/api/shipments/{trackingId}")
    public Shipment updateShipment(
            @PathVariable String trackingId,
            @RequestBody Shipment shipment) {

        return shipmentService.updateShipment(trackingId, shipment);
    }

    @GetMapping("/api/shipments/{trackingId}/history")
    public List<ShipmentHistory> getShipmentHistory(
            @PathVariable String trackingId) {

        return shipmentService.getShipmentHistory(trackingId);
    }
}