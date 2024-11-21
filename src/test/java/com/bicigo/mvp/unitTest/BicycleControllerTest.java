package com.bicigo.mvp.unitTest;

import com.bicigo.mvp.model.Bicycle;
import com.bicigo.mvp.model.User;
import com.bicigo.mvp.repository.AvailabilityRepository;
import com.bicigo.mvp.repository.BicycleRepository;
import com.bicigo.mvp.repository.UserRepository;
import com.bicigo.mvp.service.BicycleService;
import com.bicigo.mvp.service.UserService;
import com.bicigo.mvp.service.impl.BicycleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class BicycleControllerTest {
    @Mock
    private BicycleRepository bicycleRepository;
    @Mock
    private BicycleService bicycleService;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AvailabilityRepository availabilityRepository;
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        bicycleService = new BicycleServiceImpl(bicycleRepository, userService, availabilityRepository);
    }

    @Test
    public void PublishBicycle(){
        User user = new User();
        user.setId(1L);

        given(userRepository.save(user)).willReturn(user);
        given(userService.getUserById(1L)).willReturn(user);

        Bicycle bicycleToPublish = new Bicycle(1L, "Bicicleta 1", "Bicicleta azul", 20.0, "Aro 20", "BMX", "image.jpg","pickup", "delivery",user);

        when(bicycleRepository.save(any(Bicycle.class))).thenReturn(bicycleToPublish);

        Bicycle bicyclePublished = bicycleService.createBicycle(user.getId(), bicycleToPublish);

        assertNotNull(bicyclePublished);
        assertEquals(1L, bicyclePublished.getId().longValue());
        assertEquals("Bicicleta 1", bicyclePublished.getBicycleName());

        verify(bicycleRepository, times(1)).save(any(Bicycle.class));
    }

    @Test
    public void EnterBicyclesInformation(){
        User user = new User();
        user.setId(1L);

        given(userRepository.save(user)).willReturn(user);
        given(userService.getUserById(1L)).willReturn(user);

        Bicycle bicyclePublished = new Bicycle(2L, "Bicicleta 1", "Bicicleta azul", 20.0, "Aro 20", "BMX", "image.jpg", "pickup","delivery",user);
        Bicycle bicycleUpdated =  new Bicycle(2L, "Bicicleta 1", "Bicicleta negra", 30.0, "Aro 20", "BMX", "image.jpg", "pickup", "delivery",user);

        given(bicycleRepository.save(any(Bicycle.class))).willReturn(bicyclePublished);
        given(bicycleRepository.findById(2L)).willReturn(Optional.of(bicyclePublished));
        given(bicycleRepository.existsById(2L)).willReturn(true);

        when(bicycleRepository.save(any(Bicycle.class))).thenReturn(bicycleUpdated);
        when(bicycleRepository.findById(2L)).thenReturn(Optional.of(bicycleUpdated));

        Bicycle bicycleEdited = bicycleService.updateBicycle(2L,bicycleUpdated);

        assertNotNull(bicycleEdited);
        assertNotEquals(bicyclePublished.getBicycleDescription(), bicycleEdited.getBicycleDescription());
        assertNotEquals(bicyclePublished.getBicyclePrice(), bicycleEdited.getBicyclePrice());

        verify(bicycleRepository, times(1)).save(bicycleUpdated);
    }
}
