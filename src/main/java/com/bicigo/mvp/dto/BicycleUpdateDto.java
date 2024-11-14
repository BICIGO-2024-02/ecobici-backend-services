package com.bicigo.mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BicycleUpdateDto {
    private String bicycleDescription;
    private String bicycleModel;
    private String bicycleName;
    private double bicyclePrice;
    private String bicycleSize;
    private String imageData;
    private String pickUpLocation;
    private String deliveryLocation;
}
