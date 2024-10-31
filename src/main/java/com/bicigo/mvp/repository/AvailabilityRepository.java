package com.bicigo.mvp.repository;

import com.bicigo.mvp.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByBicycleId(Long bicycle_id);
    List<Availability> findByBicycleIdAndAvailabilityType(Long bicycle_id, boolean availability_type);
    boolean existsByBicycleIdAndAvailabilityStartDateLessThanEqualAndAvailabilityEndDateGreaterThanEqual(Long bicycle_id, java.time.LocalDate availability_start_date, java.time.LocalDate availability_end_date);
}
