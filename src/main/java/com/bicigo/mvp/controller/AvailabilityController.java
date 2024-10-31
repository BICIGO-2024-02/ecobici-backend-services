package com.bicigo.mvp.controller;

import com.bicigo.mvp.model.Availability;
import com.bicigo.mvp.service.AvailabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ecobici/v1")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    // URL: http://localhost:8080/api/ecobici/v1/availabilities/{availabilityId}
    // Method: GET
    @Transactional(readOnly = true)
    @GetMapping("/availabilities/{availabilityId}")
    public ResponseEntity<Availability> getAvailabilityById(@PathVariable(name = "availabilityId") Long availabilityId) {
        return new ResponseEntity<Availability>(availabilityService.getById(availabilityId), HttpStatus.OK);
    }
}
