package com.bicigo.mvp.service.impl;

import com.bicigo.mvp.dto.AvailabilityDto;
import com.bicigo.mvp.exception.ResourceNotFoundException;
import com.bicigo.mvp.exception.ValidationException;
import com.bicigo.mvp.model.Availability;
import com.bicigo.mvp.repository.AvailabilityRepository;
import com.bicigo.mvp.repository.BicycleRepository;
import com.bicigo.mvp.service.AvailabilityService;
import com.bicigo.mvp.service.BicycleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {
    AvailabilityRepository availabilityRepository;
    BicycleService bicycleService;
    BicycleRepository bicycleRepository;
    ModelMapper modelMapper;

    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository, BicycleService bicycleService, BicycleRepository bicycleRepository, ModelMapper modelMapper) {
        this.availabilityRepository = availabilityRepository;
        this.bicycleService = bicycleService;
        this.bicycleRepository = bicycleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Availability create(AvailabilityDto availabilityDto) {
        validateAvailability(availabilityDto);
        existsBicycle(availabilityDto.getBicycleId());
        Availability availability = modelMapper.map(availabilityDto, Availability.class);
        return availabilityRepository.save(availability);
    }

    @Override
    public Availability getById(Long availability_id) {
        existsAvailability(availability_id);
        return availabilityRepository.findById(availability_id).get();
    }

    @Override
    public void delete(Long availability_id) {
        existsAvailability(availability_id);
        availabilityRepository.deleteById(availability_id);
    }

    @Override
    public boolean existsBetweenDates(Long bicycle_id, LocalDate availability_start_date, LocalDate availability_end_date) {
        existsBicycle(bicycle_id);
        return availabilityRepository.existsByBicycleIdAndAvailabilityStartDateLessThanEqualAndAvailabilityEndDateGreaterThanEqual(bicycle_id, availability_end_date, availability_start_date);
    }

    @Override
    public List<Availability> getByBicycleId(Long bicycle_id) {
        existsBicycle(bicycle_id);
        return availabilityRepository.findByBicycleId(bicycle_id);
    }

    @Override
    public List<Availability> getByBicycleIdAndAvailabilityType(Long bicycle_id, boolean availability_type) {
        existsBicycle(bicycle_id);
        return availabilityRepository.findByBicycleId(bicycle_id);
    }

    private void existsBicycle(Long bicycle_id) {
        if (!bicycleRepository.existsById(bicycle_id))
            throw new ResourceNotFoundException("Bicycle with id " + bicycle_id + " does not exist");
    }

    private void existsAvailability (Long availability_id) {
        if (!availabilityRepository.existsById(availability_id))
            throw new ResourceNotFoundException("Availability with id " + availability_id + " does not exist");
    }

    private void validateAvailability(AvailabilityDto availability) {
        if (availability.getAvailabilityStartDate().isAfter(availability.getAvailabilityEndDate()))
            throw new ValidationException("Availability start date must be before availability end date");
        if (availability.getAvailabilityStartDate().isBefore(LocalDate.now()))
            throw new ValidationException("Availability start date must be after today");
        if (availability.getBicycleId() == null)
            throw new ValidationException("Bicycle id must not be null");
    }
}