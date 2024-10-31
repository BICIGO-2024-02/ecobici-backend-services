package com.bicigo.mvp.controller;

import com.bicigo.mvp.dto.UserDto;
import com.bicigo.mvp.exception.ResourceNotFoundException;
import com.bicigo.mvp.exception.ValidationException;
import com.bicigo.mvp.model.User;
import com.bicigo.mvp.repository.UserRepository;
import com.bicigo.mvp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ecobici/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // URL: http://localhost:8080/api/ecobici/v1/users
    // Method: GET
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<List<UserDto>>(users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    // URL: http://localhost:8080/api/ecobici/v1/users/{userId}
    // Method: GET
    @Transactional(readOnly = true)
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(name = "userId") Long userId) {
        existsUserByUserId(userId);
        User user = userService.getUserById(userId);
        UserDto userDto = convertToDto(user);
        return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
    }

    // URL: http://localhost:8080/api/ecobici/v1/register
    // Method: POST
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        validateUser(user);
        existsUserByEmail(user);
        return new ResponseEntity<User>(userService.createUser(user), HttpStatus.CREATED);
    }

    // URL: http://localhost:8080/api/ecobici/v1/users/{userId}
    // Method: PUT
    @Transactional
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable(name = "userId") Long userId, @RequestBody User user) {
        existsUserByUserId(userId);
        validateUser(user);
        user.setId(userId);
        User responseUser = ifDifferentOrEmptyUpdate(user);
        return new ResponseEntity<User>(responseUser, HttpStatus.OK);

    }

    // URL: http://localhost:8080/api/ecobici/v1/users/{userId}
    // Method: DELETE
    @Transactional
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "userId") Long userId) {
        existsUserByUserId(userId);
        userService.deleteUser(userId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .userFirstName(user.getUserFirstName())
                .userLastName(user.getUserLastName())
                .userEmail(user.getUserEmail())
                .userPhone(user.getUserPhone())
                .userBirthDate(user.getUserBirthDate())
                .imageData(user.getImageData())
                .bicycles(user.getBicycles())
                .build();
    }

    private void validateUser(User user) {
        if (user.getUserFirstName() == null || user.getUserFirstName().isEmpty()) {
            throw new ValidationException("El nombre del usuario debe ser obligatorio");
        }
        if (user.getUserFirstName().length() > 50) {
            throw new ValidationException("El nombre del usuario no debe exceder los 50 caracteres");
        }
        if (user.getUserLastName() == null || user.getUserLastName().isEmpty()) {
            throw new ValidationException("El apellido del usuario debe ser obligatorio");
        }
        if (user.getUserLastName().length() > 50) {
            throw new ValidationException("El apellido del usuario no debe exceder los 50 caracteres");
        }
        if (user.getUserEmail() == null || user.getUserEmail().isEmpty()) {
            throw new ValidationException("El email del usuario debe ser obligatorio");
        }
        if (user.getUserEmail().length() > 50) {
            throw new ValidationException("El email del usuario no debe exceder los 50 caracteres");
        }
        if (user.getUserPassword() == null || user.getUserPassword().isEmpty()) {
            throw new ValidationException("La contraseña del usuario debe ser obligatorio");
        }
        if (user.getUserPassword().length() > 100) {
            throw new ValidationException("La contraseña del usuario no debe exceder los 100 caracteres");
        }

    }

    private void existsUserByEmail(User user) {
        if (userRepository.existsByUserEmail(user.getUserEmail())) {
            throw new ValidationException("Ya existe un usuario con el email " + user.getUserEmail());
        }
    }

    private void existsUserByEmail(String email) {
        if (!userRepository.existsByUserEmail(email)) {
            throw new ResourceNotFoundException("No existe un usuario con el email " + email);
        }
    }

    private void existsUserByUserId(Long userId) {
        if (userService.getUserById(userId) == null) {
            throw new ResourceNotFoundException("No existe un usuario con el id " + userId);
        }
    }

    private User ifDifferentOrEmptyUpdate(User user){
        return userRepository.findById(user.getId()).map(userToUpdate -> {
            if (user.getUserFirstName() != null && !user.getUserFirstName().isEmpty() && !user.getUserFirstName().equals(userToUpdate.getUserFirstName())) {
                userToUpdate.setUserFirstName(user.getUserFirstName());
            }
            if (user.getUserLastName() != null && !user.getUserLastName().isEmpty() && !user.getUserLastName().equals(userToUpdate.getUserLastName())) {
                userToUpdate.setUserLastName(user.getUserLastName());
            }
            if (user.getUserEmail() != null && !user.getUserEmail().isEmpty() && !user.getUserEmail().equals(userToUpdate.getUserEmail())) {
                userToUpdate.setUserEmail(user.getUserEmail());
            }
            if (user.getUserPassword() != null && !user.getUserPassword().isEmpty() && !user.getUserPassword().equals(userToUpdate.getUserPassword())) {
                userToUpdate.setUserPassword(user.getUserPassword());
            }
            if (user.getUserBirthDate() != null && !user.getUserBirthDate().equals(userToUpdate.getUserBirthDate())) {
                userToUpdate.setUserBirthDate(user.getUserBirthDate());
            }
            if (user.getUserPhone() != null && !user.getUserPhone().isEmpty() && !user.getUserPhone().equals(userToUpdate.getUserPhone())) {
                userToUpdate.setUserPhone(user.getUserPhone());
            }
            if (user.getImageData() != null && !user.getImageData().isEmpty() && !user.getImageData().equals(userToUpdate.getImageData())) {
                userToUpdate.setImageData(user.getImageData());
            }
            return userService.updateUser(userToUpdate);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + user.getId()));
    }
}
