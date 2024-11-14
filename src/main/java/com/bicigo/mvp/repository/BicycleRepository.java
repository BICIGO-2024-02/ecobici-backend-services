package com.bicigo.mvp.repository;

import com.bicigo.mvp.model.Bicycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BicycleRepository extends JpaRepository<Bicycle, Long> {
    @Procedure(procedureName = "update_bicycle")
    void updateBicycle(
            @Param("p_bicycle_id") Long bicycleId,
            @Param("p_bicycle_description") String bicycleDescription,
            @Param("p_bicycle_model") String bicycleModel,
            @Param("p_bicycle_name") String bicycleName,
            @Param("p_bicycle_price") double bicyclePrice,
            @Param("p_bicycle_size") String bicycleSize,
            @Param("p_image_data") String imageData,
            @Param("p_pick_up_location") String pickUpLocation,
            @Param("p_delivery_location") String deliveryLocation
    );
}