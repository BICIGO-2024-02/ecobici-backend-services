package com.bicigo.mvp.service.impl;

import com.bicigo.mvp.dto.AuthenticationResponse;
import com.bicigo.mvp.dto.LoginRequest;
import com.bicigo.mvp.dto.RegisterRequest;
import com.bicigo.mvp.exception.ValidationException;
import com.bicigo.mvp.model.Roles;
import com.bicigo.mvp.model.Token;
import com.bicigo.mvp.model.TokenType;
import com.bicigo.mvp.model.User;
import com.bicigo.mvp.repository.TokenRepository;
import com.bicigo.mvp.repository.UserRepository;
import com.bicigo.mvp.service.AuthService;
import com.bicigo.mvp.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
                .userFirstName(registerRequest.getUserFirstName())
                .userLastName(registerRequest.getUserLastName())
                .userEmail(registerRequest.getUserEmail())
                .userPassword(passwordEncoder.encode(registerRequest.getUserPassword()))
                .userPhone(registerRequest.getUserPhone())
                .userBirthDate(registerRequest.getUserBirthDate())
                .imageData(registerRequest.getImageData())
                .role(Roles.USER)
                .build();
        var savedUser = userRepository. save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .user_id(user.getId())
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();

    }

    @Override
    public AuthenticationResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserEmail(),
                        loginRequest.getUserPassword()));
        var user = userRepository.findByUserEmail(loginRequest.getUserEmail());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .user_id(user.getId())
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByUserEmail(userEmail);
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }

    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void validateRegisterRequest(RegisterRequest registerRequest)
    {
        if(registerRequest.getUserFirstName()==null  ||
                registerRequest.getUserFirstName().isEmpty())
        {
            throw new ValidationException("El nombre del usuario debe ser obligatorio");
        }
        if(registerRequest.getUserFirstName().length()>50)
        {
            throw new ValidationException("El nombre del usuario no debe exceder los 50 caracteres");
        }
        if(registerRequest.getUserLastName()==null || registerRequest.getUserLastName().isEmpty())
        {
            throw new ValidationException("El apellido del usuario debe ser obligatorio");
        }
        if(registerRequest.getUserLastName().length()>50)
        {
            throw new ValidationException("El apellido del usuario no debe exceder los 50 caracteres");
        }
        if (registerRequest.getUserEmail() == null || registerRequest.getUserEmail().isEmpty()) {
            throw new ValidationException("El email del usuario debe ser obligatorio");
        }
        if (registerRequest.getUserEmail().length() > 50) {
            throw new ValidationException("El email del usuario no debe exceder los 50 caracteres");
        }
        if (registerRequest.getUserPassword() == null || registerRequest.getUserPassword().isEmpty()) {
            throw new ValidationException("La contraseña del usuario debe ser obligatorio");
        }
        if (registerRequest.getUserPassword().length() > 100) {
            throw new ValidationException("La contraseña del usuario no debe exceder los 100 caracteres");
        }
    }

    public void existsUserByEmail(RegisterRequest registerRequest) {
        if (userRepository.existsByUserEmail(registerRequest.getUserEmail())) {
            throw new ValidationException("Ya existe un usuario con el email " + registerRequest.getUserEmail());
        }
    }


}