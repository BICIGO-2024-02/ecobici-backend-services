package com.bicigo.mvp.service.impl;

import com.bicigo.mvp.dto.AvailabilityDto;
import com.bicigo.mvp.dto.RentDto;
import com.bicigo.mvp.exception.ValidationException;
import com.bicigo.mvp.model.Availability;
import com.bicigo.mvp.model.Bicycle;
import com.bicigo.mvp.model.Rent;
import com.bicigo.mvp.repository.BicycleRepository;
import com.bicigo.mvp.repository.RentRepository;
import com.bicigo.mvp.repository.UserRepository;
import com.bicigo.mvp.service.AvailabilityService;
import com.bicigo.mvp.service.RentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class RentServiceImpl implements RentService {
    RentRepository rentRepository;
    UserRepository userRepository;
    BicycleRepository bicycleRepository;
    AvailabilityService availabilityService;
    ModelMapper modelMapper;

    public RentServiceImpl(
            RentRepository rentRepository,
            UserRepository userRepository,
            BicycleRepository bicycleRepository,
            AvailabilityService availabilityService,
            ModelMapper modelMapper
    ) {
        this.rentRepository = rentRepository;
        this.userRepository = userRepository;
        this.bicycleRepository = bicycleRepository;
        this.availabilityService = availabilityService;
        this.modelMapper = modelMapper;
    }


    @Override
    public Rent create(RentDto rentDto) {
        validateData(rentDto);
        bicycleExists(rentDto.getBicycleId());

        Rent rent = new Rent();

        rent.setRentStartDate(rentDto.getRentStartDate());
        rent.setRentEndDate(rentDto.getRentEndDate());
        rent.setRentPrice(rentDto.getRentPrice());
        rent.setBicycle(bicycleRepository.findById(rentDto.getBicycleId()).orElse(null));

        bicycleAvailable(rent, rent.getRentStartDate(), rent.getRentEndDate());
        return rentRepository.save(rent);
    }

    @Override
    public Rent getById(Long rent_id) {
        return rentRepository.findById(rent_id).orElse(null);
    }

    @Override
    public void delete(Long rent_id) {
        rentRepository.deleteById(rent_id);
    }

    @Override
    public List<Rent> getByBicycleId(Long bicycle_id) {
        return rentRepository.findByBicycleId(bicycle_id);
    }

    private void userExists(Long user_id) {
        if (!userRepository.existsById(user_id)) {
            throw new ValidationException("User with id " + user_id + " does not exist");
        }
    }

    private void bicycleExists(Long bicycle_id) {
        if (!(bicycleRepository.existsById(bicycle_id))) {
            throw new ValidationException("Bicycle with id " + bicycle_id + " does not exist");
        }
        userExists(bicycleRepository.findById(bicycle_id).orElse(null).getUser().getId());
    }

    private void bicycleAvailable(Rent rent, LocalDate rent_start_date, LocalDate rent_end_date) {
        List<Availability> availabilityList = availabilityService.getByBicycleIdAndAvailabilityType(rent.getBicycle().getId(), false);

        for (Availability availability : availabilityList) {
            if (availability.getAvailabilityEndDate().isBefore(rent_start_date) || availability.getAvailabilityStartDate().isAfter(rent_end_date)) {
                continue;
            }

            if (availability.getAvailabilityStartDate().isBefore(rent_start_date) && availability.getAvailabilityEndDate().isAfter(rent_end_date)) {
                throw new ValidationException("The bicycle with id " + rent.getBicycle().getId() + " is not available for the requested rental period. It is already rented out.");
            }

            if (availability.getAvailabilityStartDate().isBefore(rent_start_date) && availability.getAvailabilityEndDate().isBefore(rent_end_date)) {
                throw new ValidationException("Bicycle with id " + rent.getBicycle().getId() + " is not available for the requested rental period.");
            }

            if (availability.getAvailabilityStartDate().isAfter(rent_start_date) && availability.getAvailabilityEndDate().isAfter(rent_end_date)) {
                throw new ValidationException("Bicycle with id " + rent.getBicycle().getId() + " is not available for the requested rental period.");
            }

            if (availability.getAvailabilityStartDate().equals(rent_start_date) || availability.getAvailabilityEndDate().equals(rent_start_date) ||
                    availability.getAvailabilityStartDate().equals(rent_end_date) || availability.getAvailabilityEndDate().equals(rent_end_date)) {
                throw new ValidationException("Bicycle with id " + rent.getBicycle().getId() + " is not available for the requested rental period.");
            }
        }

        AvailabilityDto availability = new AvailabilityDto();
        availability.setAvailabilityStartDate(rent_start_date);
        availability.setAvailabilityEndDate(rent_end_date);
        availability.setBicycleId(rent.getBicycle().getId());
        availabilityService.create(availability);
    }

    private void validateData(RentDto rent){
        if (rent.getBicycleId() == null) {
            throw new ValidationException("Bicycle is required");
        }
        if (rent.getRentPrice() <= 0) {
            throw new ValidationException("Rent price must be greater than 0");
        }
        if (rent.getRentEndDate() == null) {
            throw new ValidationException("Rent date is required");
        }
        if (rent.getRentStartDate() == null) {
            throw new ValidationException("Return date is required");
        }
        if (rent.getRentStartDate().isAfter(rent.getRentEndDate())) {
            throw new ValidationException("Return end_date must be after rent start_date");
        }
        if (rent.getRentEndDate().equals(rent.getRentStartDate())) {
            throw new ValidationException("Return end_date must be after rent start_date");
        }
        if (rent.getRentStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Rent start_date must be after today");
        }
    }
}