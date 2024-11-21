package com.bicigo.mvp.integrationTest;

import com.bicigo.mvp.model.Availability;
import com.bicigo.mvp.service.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AvailabilityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AvailabilityService availabilityService;

    private static final String TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbmRyZWFAZ21haWwuY29tIiwiaWF0IjoxNzMyMjMxMzY2LCJleHAiOjE3MzIzMTc3NjZ9.bsZBeaUwUvYsjQGyVDf28NV8bZNS8Ofbxns_zvmJqf0";

    private Availability mockAvailability;

    @BeforeEach
    void setUp() {
        mockAvailability = new Availability();
        mockAvailability.setId(1L);
        // Configura otras propiedades según tu modelo
    }

    @Test
    public void getAvailabilityById_Success() throws Exception {
        // Arrange
        Long availabilityId = 1L;
        when(availabilityService.getById(availabilityId)).thenReturn(mockAvailability);

        // Act & Assert
        mockMvc.perform(get("/api/ecobici/v1/availabilities/{id}", availabilityId)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(availabilityId));
    }

    @Test
    public void getAvailabilityById_Unauthorized() throws Exception {
        Long availabilityId = 1L;

        mockMvc.perform(get("/api/ecobici/v1/availabilities/{id}", availabilityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getAvailabilityById_InvalidToken() throws Exception {
        Long availabilityId = 1L;

        mockMvc.perform(get("/api/ecobici/v1/availabilities/{id}", availabilityId)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbnZhbGlkIn0.invalid_signature")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAvailabilityById_NotFound() throws Exception {
        // Arrange
        Long availabilityId = 999L;
        when(availabilityService.getById(availabilityId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/ecobici/v1/availabilities/{id}", availabilityId)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void getAvailabilityById_ServerError() throws Exception {
        // Arrange
        Long availabilityId = 1L;
        when(availabilityService.getById(availabilityId))
                .thenThrow(new RuntimeException("Internal Server Error"));

        // Act & Assert
        mockMvc.perform(get("/api/ecobici/v1/availabilities/{id}", availabilityId)
                        .header("Authorization", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
