package com.bicigo.mvp.repository;

import com.bicigo.mvp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserEmailAndUserPassword(String user_email, String user_password);
    boolean existsById(Long user_id);
    boolean existsByUserEmail(String user_email);
    User findByUserEmail(String email);
    List<User>findAll();
    @Procedure(procedureName = "update_user_data")
    void updateUserData(
            @Param("p_user_id") Long userId,
            @Param("p_image_data") String imageData,
            @Param("p_user_birth_date") LocalDate userBirthDate,
            @Param("p_user_email") String userEmail,
            @Param("p_user_first_name") String userFirstName,
            @Param("p_user_last_name") String userLastName,
            @Param("p_user_phone") String userPhone
    );
}
