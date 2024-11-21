package com.bicigo.mvp.integrationTest;

import com.bicigo.mvp.model.Bicycle;
import com.bicigo.mvp.model.User;
import com.bicigo.mvp.service.BicycleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class BicycleIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BicycleService bicycleService;
    @Test
    public void testPublishBicycle() throws Exception {
        Bicycle bicycle = Bicycle.builder()
                .id(1L)
                .bicycleName("Bike 1")
                .bicycleDescription("Blue Bike")
                .bicycleModel("BMX")
                .bicyclePrice(200.0)
                .bicycleSize("Big")
                .imageData("image.jpg")
                .user(new User())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(bicycle);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/ecobici/v1/bicycles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void testEnterInformationBicycle() throws Exception{
        User user = new User();
        user.setId(1L);
        Bicycle bicycleUpdated = new Bicycle(1L, "Bicicleta 1", "Bicicleta negra", 20.0, "Aro 20", "BMX", "image.jpg", "pickup", "delivery", user);

        given(bicycleService.getBicycleById(1L)).willReturn(bicycleUpdated);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/ecobici/v1/bicycles/1")
                        .content(asJsonString(bicycleUpdated))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}