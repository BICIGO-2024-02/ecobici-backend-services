package com.bicigo.mvp.service;

import com.bicigo.mvp.dto.BicycleUpdateDto;
import com.bicigo.mvp.model.Bicycle;

import java.time.LocalDate;
import java.util.List;

public interface BicycleService {
    public abstract Bicycle createBicycle(Long userId, Bicycle bicycle);
    public abstract Bicycle getBicycleById(Long bicycle_id);
    public abstract Bicycle updateBicycle(Long bicycleId, Bicycle bicycle);
    public abstract void deleteBicycle(Long bicycle_id);
    public abstract List<Bicycle> getAllBicycles();
    public abstract List<Bicycle> getAllAvailableBicycles(LocalDate start_date, LocalDate end_date);
    public abstract Bicycle updateBicycle(Long bicycleId, BicycleUpdateDto updateDto);
}
