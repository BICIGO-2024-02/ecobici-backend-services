package com.bicigo.mvp.unitTest;

import com.bicigo.mvp.controller.UserController;
import com.bicigo.mvp.dto.UserDto;
import com.bicigo.mvp.dto.UserUpdateDto;
import com.bicigo.mvp.model.User;
import com.bicigo.mvp.repository.UserRepository;
import com.bicigo.mvp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private User mockUser1;
    private User mockUser2;
    private UserUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        userController = new UserController(userRepository);
        // Inyectar los mocks manualmente
        ReflectionTestUtils.setField(userController, "userService", userService);
        // Configurar datos de prueba
        mockUser1 = User.builder()
                .id(1L)
                .userFirstName("Juan")
                .userLastName("Pérez")
                .userEmail("juan@example.com")
                .userPhone("123456789")
                .userBirthDate(LocalDate.of(1990, 1, 1))
                .imageData("image1")
                .build();

        mockUser2 = User.builder()
                .id(2L)
                .userFirstName("María")
                .userLastName("García")
                .userEmail("maria@example.com")
                .userPhone("987654321")
                .userBirthDate(LocalDate.of(1995, 5, 5))
                .imageData("image2")
                .build();

        updateDto = UserUpdateDto.builder()
                .userFirstName("Juan Actualizado")
                .userLastName("Pérez Actualizado")
                .userEmail("juan.updated@example.com")
                .userPhone("999999999")
                .userBirthDate(LocalDate.of(1990, 1, 1))
                .imageData("newImage")
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList(mockUser1, mockUser2));

        // Act
        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        UserDto firstUser = response.getBody().get(0);
        assertEquals(mockUser1.getUserFirstName(), firstUser.getUserFirstName());
        assertEquals(mockUser1.getUserEmail(), firstUser.getUserEmail());

        UserDto secondUser = response.getBody().get(1);
        assertEquals(mockUser2.getUserFirstName(), secondUser.getUserFirstName());
        assertEquals(mockUser2.getUserEmail(), secondUser.getUserEmail());
    }

    @Test
    void updateUser_WithValidData_ShouldReturnSuccess() {
        // Arrange
        Long userId = 1L;
        User updatedUser = User.builder()
                .id(userId)
                .userFirstName(updateDto.getUserFirstName())
                .userLastName(updateDto.getUserLastName())
                .userEmail(updateDto.getUserEmail())
                .userPhone(updateDto.getUserPhone())
                .userBirthDate(updateDto.getUserBirthDate())
                .imageData(updateDto.getImageData())
                .build();

        when(userService.updateUser(eq(userId), any(UserUpdateDto.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<?> response = userController.updateUser(userId, updateDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Usuario actualizado exitosamente", responseBody.get("message"));
        assertEquals(userId, responseBody.get("userId"));

        // La parte updatedData es un UserUpdateDto, no un Map
        @SuppressWarnings("unchecked")
        UserUpdateDto returnedData = (UserUpdateDto) responseBody.get("updatedData");
        assertNotNull(returnedData);
        assertEquals(updateDto.getUserFirstName(), returnedData.getUserFirstName());
        assertEquals(updateDto.getUserLastName(), returnedData.getUserLastName());
        assertEquals(updateDto.getUserEmail(), returnedData.getUserEmail());
        assertEquals(updateDto.getUserPhone(), returnedData.getUserPhone());
        assertEquals(updateDto.getUserBirthDate(), returnedData.getUserBirthDate());
        assertEquals(updateDto.getImageData(), returnedData.getImageData());
    }

    @Test
    void updateUser_WithInvalidData_ShouldReturnBadRequest() {
        // Arrange
        Long userId = 1L;
        String errorMessage = "Error de validación";
        when(userService.updateUser(eq(userId), any(UserUpdateDto.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // Act
        ResponseEntity<?> response = userController.updateUser(userId, updateDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(errorMessage, responseBody.get("error"));
    }

    @Test
    void updateUser_WithRuntimeException_ShouldReturnInternalServerError() {
        // Arrange
        Long userId = 1L;
        String errorMessage = "Error interno del servidor";
        when(userService.updateUser(eq(userId), any(UserUpdateDto.class)))
                .thenThrow(new RuntimeException(errorMessage));

        // Act
        ResponseEntity<?> response = userController.updateUser(userId, updateDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Error al actualizar usuario", responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }
}
