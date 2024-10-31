package com.bicigo.mvp.controller;

import com.bicigo.mvp.dto.AuthenticationResponse;
import com.bicigo.mvp.dto.LoginRequest;
import com.bicigo.mvp.dto.RegisterRequest;
import com.bicigo.mvp.repository.UserRepository;
import com.bicigo.mvp.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ecobici/v1/auth")
public class AuthentificationController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    // URL: http://localhost:8080/api/ecobici/v1/auth/register
    // Method: POST
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerStudent(@RequestBody RegisterRequest request) {
        authService.existsUserByEmail(request);
        authService.validateRegisterRequest(request);
        AuthenticationResponse registeredUser = authService.register(request);
        return new ResponseEntity<AuthenticationResponse>(registeredUser, HttpStatus.CREATED);
    }

    // URL: http://localhost:8080/api/ecobici/v1/auth/login
    // Method: POST
    //@Transactional(readOnly = true)
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {

        AuthenticationResponse loggedUser = authService.login(request);
        // Agrega el ID del usuario a la respuesta


        return new ResponseEntity<AuthenticationResponse>(loggedUser, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }

}
