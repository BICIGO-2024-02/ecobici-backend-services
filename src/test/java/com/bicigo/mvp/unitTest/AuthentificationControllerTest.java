package com.bicigo.mvp.unitTest;

import com.bicigo.mvp.dto.AuthenticationResponse;
import com.bicigo.mvp.dto.LoginRequest;
import com.bicigo.mvp.dto.RegisterRequest;
import com.bicigo.mvp.model.Bicycle;
import com.bicigo.mvp.model.Roles;
import com.bicigo.mvp.model.Token;
import com.bicigo.mvp.model.User;
import com.bicigo.mvp.repository.TokenRepository;
import com.bicigo.mvp.repository.UserRepository;
import com.bicigo.mvp.service.JwtService;
import com.bicigo.mvp.service.impl.AuthServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthentificationControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImp authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    public void testRegisterUser() {
        RegisterRequest request = new RegisterRequest(
                "Andrea", "Doe", "andrea@example.com", "password123", "123-456-789", LocalDate.now(), "img",Roles.USER
        );
        List<Bicycle> bicycles = new ArrayList<>();
        List<Token> tokens = new ArrayList<>();
        User savedUser = new User(1L, "Andrea", "Doe", "andrea@example.com", "encoded_password", "123-456-789", LocalDate.now(), "img", bicycles,Roles.USER, tokens);
        String jwtToken = "mocked_jwt_token";
        String refreshToken = "mocked_refresh_token";
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(jwtToken);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(refreshToken);
        AuthenticationResponse response = authService.register(request);
        assertNotNull(response);
        assertEquals(savedUser.getId(), response.getUser_id());
        assertEquals(jwtToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
        verify(jwtService, times(1)).generateRefreshToken(any(User.class));
    }

    @Test
    public void testLogin() {
        LoginRequest loginRequest = new LoginRequest("andrea@example.com", "password123");
        User user = new User();
        String jwtToken = "mocked_jwt_token";
        String refreshToken = "mocked_refresh_token";
        when(userRepository.findByUserEmail(loginRequest.getUserEmail())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(jwtToken);
        when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        AuthenticationResponse response = authService.login(loginRequest);
        assertEquals(user.getId(), response.getUser_id());
        assertEquals(jwtToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        verify(userRepository, times(1)).findByUserEmail(loginRequest.getUserEmail());
        verify(jwtService, times(1)).generateToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }


}