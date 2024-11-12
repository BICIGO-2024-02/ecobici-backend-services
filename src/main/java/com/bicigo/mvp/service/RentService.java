package com.bicigo.mvp.service;

import com.bicigo.mvp.dto.RentDto;
import com.bicigo.mvp.model.Rent;

import java.util.List;

public interface RentService {
    public abstract Rent create(RentDto rent);
    public abstract Rent getById(Long rent_id);
    public abstract void delete(Long rent_id);
    public abstract List<Rent> getByBicycleId(Long bicycle_id);
    public abstract List<Rent> getByUserId(Long user_id);
}
