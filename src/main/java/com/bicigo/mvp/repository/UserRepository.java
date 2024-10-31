package com.bicigo.mvp.repository;

import com.bicigo.mvp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserEmailAndUserPassword(String user_email, String user_password);
    boolean existsById(Long user_id);
    boolean existsByUserEmail(String user_email);


    User findByUserEmail(String email);

    List<User>findAll();

}
