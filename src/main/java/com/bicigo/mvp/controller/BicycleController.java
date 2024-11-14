package com.bicigo.mvp.controller;

import com.bicigo.mvp.dto.BicycleUpdateDto;
import com.bicigo.mvp.model.Bicycle;
import com.bicigo.mvp.service.BicycleService;
import com.bicigo.mvp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ecobici/v1/bicycles")
@Slf4j
public class BicycleController {
    @Autowired
    private UserService userService;

    private final BicycleService bicycleService;

    public BicycleController(BicycleService bicycleService) {
        this.bicycleService = bicycleService;
    }

    // URL: http://localhost:8080/api/ecobici/v1/bicycles
    // Method: GET
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<Bicycle>> getAllBicycles() {
        //print somethign
        System.out.println("getAllBicycles");
        return new ResponseEntity<List<Bicycle>>(bicycleService.getAllBicycles(), HttpStatus.OK);
    }

    // URL: http://localhost:8080/api/ecobici/v1/bicycles/{bicycleId}
    // Method: GET
    @Transactional(readOnly = true)
    @GetMapping("/{bicycleId}")
    public ResponseEntity<Bicycle> getBicycleById(@PathVariable(name = "bicycleId") Long bicycleId) {
        return new ResponseEntity<Bicycle>(bicycleService.getBicycleById(bicycleId), HttpStatus.OK);
    }

    // URL: http://localhost:8080/api/ecobici/v1/bicycles/available
    // Method: GET
    @Transactional(readOnly = true)
    @GetMapping("/available")
    public ResponseEntity<List<Bicycle>> getAllAvailableBicycles(
            @RequestParam(name = "start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(name = "end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date
    ) {
        return new ResponseEntity<>(bicycleService.getAllAvailableBicycles(start_date, end_date), HttpStatus.OK);
    }


    // URL: http://localhost:8080/api/ecobici/v1/bicycles/{userId}
    // Method: POST
    @Transactional
    @PostMapping("/{userId}")
    public ResponseEntity<Bicycle> createBicycleWithUserId(@PathVariable(name = "userId") Long userId, @RequestBody Bicycle bicycle) {
        System.out.println("JSON Received: " + bicycle);
        return new ResponseEntity<Bicycle>(bicycleService.createBicycle(userId, bicycle), HttpStatus.CREATED);
    }

    // URL: http://localhost:8080/api/ecobici/v1/bicycles/{bicycleId}
    // Method: PUT
    @Transactional
    @PutMapping("/{bicycleId}")
    public ResponseEntity<?> updateBicycle(
            @PathVariable Long bicycleId,
            @RequestBody BicycleUpdateDto updateDTO) {
        try {
            Bicycle updatedBicycle = bicycleService.updateBicycle(bicycleId, updateDTO);

            // Crear un DTO de respuesta limpio
            BicycleUpdateDto response = BicycleUpdateDto.builder()
                    .bicycleDescription(updatedBicycle.getBicycleDescription())
                    .bicycleModel(updatedBicycle.getBicycleModel())
                    .bicycleName(updatedBicycle.getBicycleName())
                    .bicyclePrice(updatedBicycle.getBicyclePrice())
                    .bicycleSize(updatedBicycle.getBicycleSize())
                    .imageData(updatedBicycle.getImageData())
                    .pickUpLocation(updatedBicycle.getPickUpLocation())
                    .deliveryLocation(updatedBicycle.getDeliveryLocation())
                    .build();

            return ResponseEntity.ok(Map.of(
                    "message", "Bicicleta actualizada exitosamente",
                    "bicycleId", updatedBicycle.getId(),
                    "updatedData", response
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (RuntimeException e) {
            log.error("Error al actualizar bicicleta {}: {}", bicycleId, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Error al actualizar bicicleta",
                            "message", e.getMessage()
                    ));
        }
    }

    // URL: http://localhost:8080/api/ecobici/v1/bicycles/{bicycleId}
    // Method: DELETE
    @Transactional
    @DeleteMapping("/{bicycleId}")
    public ResponseEntity<String> deleteBicycleByBicycleId(@PathVariable(name = "bicycleId") Long bicycleId) {
        return new ResponseEntity<String>("Bicicleta eliminada correctamente", HttpStatus.OK);
    }
}