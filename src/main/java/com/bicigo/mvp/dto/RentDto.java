package com.bicigo.mvp.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentDto {
    private LocalDate rentStartDate;
    private LocalDate rentEndDate;
    private Double rentPrice;
    private Long bicycleId;
    private Long userId;
}
