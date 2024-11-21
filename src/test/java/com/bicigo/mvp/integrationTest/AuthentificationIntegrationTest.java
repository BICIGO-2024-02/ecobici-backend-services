package com.bicigo.mvp.integrationTest;

import com.bicigo.mvp.dto.AuthenticationResponse;
import com.bicigo.mvp.dto.LoginRequest;
import com.bicigo.mvp.dto.RegisterRequest;
import com.bicigo.mvp.model.Roles;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthentificationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRegisterStudent() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .userFirstName("Usuario")
                .userLastName("Prueba")
                .userEmail("correo@example.com")
                .userPassword("contraseña123")
                .userPhone("12341235678")
                .userBirthDate(LocalDate.of(2001, 10, 15))
                .imageData("imagen")
                .role(Roles.USER)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(registerRequest);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/ecobici/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated()) // Espera una respuesta HTTP 201 Created
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(responseBody, AuthenticationResponse.class);
    }
    @Test
    public void testLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserEmail("correo@example.com");
        loginRequest.setUserPassword("contraseña");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/ecobici/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()) // Espera una respuesta HTTP 200 OK
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
    }
}
