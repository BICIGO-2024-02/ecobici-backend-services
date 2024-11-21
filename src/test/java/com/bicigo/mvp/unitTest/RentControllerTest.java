package com.bicigo.mvp.unitTest;

import com.bicigo.mvp.controller.RentController;
import com.bicigo.mvp.dto.RentDto;
import com.bicigo.mvp.model.Bicycle;
import com.bicigo.mvp.model.Rent;
import com.bicigo.mvp.model.User;
import com.bicigo.mvp.service.RentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class RentControllerTest {

    @Mock
    private RentService rentService;

    private RentController rentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rentController = new RentController(rentService);
    }

    @Test
    public void testGetRentById() {
        Long rentId = 1L;
        Bicycle bicycle = new Bicycle();
        User user = new User();
        user.setId(2L);
        Rent rent = new Rent(1L, LocalDate.now(), LocalDate.now(), 20.0, bicycle, user, 2L);
        when(rentService.getById(rentId)).thenReturn(rent);
        ResponseEntity<Rent> response = rentController.getRentById(rentId);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rent, response.getBody());
        verify(rentService, times(1)).getById(rentId);
    }

    @Test
    public void testGetRentByBicycleId() {
        Long bicycleId = 3L;
        Bicycle bicycle = new Bicycle();
        User user = new User();
        user.setId(4L);
        Rent rent = new Rent(3L, LocalDate.now(), LocalDate.now().plusDays(5), 20.0, bicycle, user, user.getId());
        List<Rent> rents = Arrays.asList(rent);
        when(rentService.getByBicycleId(bicycleId)).thenReturn(rents);
        ResponseEntity<List<Rent>> response = rentController.getRentByBicycleId(bicycleId);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rents, response.getBody());
        verify(rentService, times(1)).getByBicycleId(bicycleId);
    }


    @Test
    public void testCreateRent() {
        Bicycle bicycle = new Bicycle();
        User user = new User();
        user.setId(5L);
        RentDto rentDto = new RentDto(LocalDate.now(), LocalDate.now().plusDays(5), 20.0, bicycle.getId(), user.getId());
        Rent rent = new Rent(null, LocalDate.now(), LocalDate.now().plusDays(5), 20.0, bicycle, user, user.getId());
        when(rentService.create(rentDto)).thenReturn(rent);
        ResponseEntity<Rent> response = rentController.createRent(rentDto);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(rent, response.getBody());
        verify(rentService, times(1)).create(rentDto);
    }


    @Test
    public void testDeleteRent() {
        Long rentId = 6L;
        ResponseEntity<Void> response = rentController.deleteRent(rentId);
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(rentService, times(1)).delete(rentId);
    }

}