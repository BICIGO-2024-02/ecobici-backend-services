package com.bicigo.mvp.service.impl;

import com.bicigo.mvp.dto.UserUpdateDto;
import com.bicigo.mvp.model.User;
import com.bicigo.mvp.repository.UserRepository;
import com.bicigo.mvp.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long user_id) {
        return userRepository.findById(user_id).orElse(null);
    }

    @Override
    public User updateUser(Long userId, UserUpdateDto updateDTO) {
        // Verificar si el usuario existe
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            // Validar que haya al menos un campo para actualizar
            if (isEmptyDTO(updateDTO)) {
                throw new IllegalArgumentException("No se proporcionaron datos para actualizar");
            }

            // Llamar al procedimiento almacenado
            userRepository.updateUserData(
                    userId,
                    updateDTO.getImageData(),
                    updateDTO.getUserBirthDate(),
                    updateDTO.getUserEmail(),
                    updateDTO.getUserFirstName(),
                    updateDTO.getUserLastName(),
                    updateDTO.getUserPhone()
            );

            // Limpiar la caché de la entidad para forzar una recarga
            entityManager.clear();

            // Obtener los datos actualizados de la base de datos
            return userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Error al recuperar usuario actualizado"));

        } catch (Exception e) {
            log.error("Error al actualizar el usuario {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    private boolean isEmptyDTO(UserUpdateDto dto) {
        return dto.getImageData() == null &&
                dto.getUserBirthDate() == null &&
                dto.getUserEmail() == null &&
                dto.getUserFirstName() == null &&
                dto.getUserLastName() == null &&
                dto.getUserPhone() == null;
    }

    @Override
    public void deleteUser(Long user_id) {
        userRepository.deleteById(user_id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
