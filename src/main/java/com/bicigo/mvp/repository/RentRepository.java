package com.bicigo.mvp.repository;

import com.bicigo.mvp.model.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {
    List<Rent> findByBicycleId(Long bicycle_id);
    List<Rent> findByUserId(Long user_id);
}