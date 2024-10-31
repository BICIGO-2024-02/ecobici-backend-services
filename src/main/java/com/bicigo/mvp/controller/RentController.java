package com.bicigo.mvp.controller;

import com.bicigo.mvp.dto.RentDto;
import com.bicigo.mvp.model.Rent;
import com.bicigo.mvp.service.RentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ecobici/v1/rents")
public class RentController {
    private final RentService rentService;

    public RentController(RentService rentService) {
        this.rentService = rentService;
    }

    // URL: http://localhost:8080/api/ecobici/v1/rents/{rentId}
    // Method: GET
    @Transactional(readOnly = true)
    @GetMapping("/{rentId}")
    public ResponseEntity<Rent> getRentById(@PathVariable(name = "rentId") Long rentId) {
        return new ResponseEntity<Rent>(rentService.getById(rentId), HttpStatus.OK);
    }

    // URL: http://localhost:8080/api/ecobici/v1/rents/bicycle/{bicycleId}
    // Method: GET
    @Transactional(readOnly = true)
    @GetMapping("/bicycle/{bicycleId}")
    public ResponseEntity<List<Rent>> getRentByBicycleId(@PathVariable(name = "bicycleId") Long bicycleId) {
        return new ResponseEntity<List<Rent>>(rentService.getByBicycleId(bicycleId), HttpStatus.OK);
    }

    // URL: http://localhost:8080/api/ecobici/v1/rents
    // Method: POST
    @Transactional
    @PostMapping
    public ResponseEntity<Rent> createRent(@RequestBody RentDto rentDto) {
        //modelMapper.getConfiguration().setAmbiguityIgnored(true);
        //modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        //Rent rent = modelMapper.map(rentDto, Rent.class);
        return new ResponseEntity<Rent>(rentService.create(rentDto), HttpStatus.CREATED);
    }

    // URL: http://localhost:8080/api/ecobici/v1/rents/{rentId}
    // Method: DELETE
    @Transactional
    @DeleteMapping("/{rentId}")
    public ResponseEntity<Void> deleteRent(@PathVariable(name = "rentId") Long rentId) {
        rentService.delete(rentId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}