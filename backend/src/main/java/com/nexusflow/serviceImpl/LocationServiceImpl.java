package com.nexusflow.serviceImpl;

import com.nexusflow.entity.Location;
import com.nexusflow.repository.LocationRepository;
import com.nexusflow.service.LocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location getLocationById(Long id) {
        Optional<Location> opt = locationRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public List<Location> findByCountry(String country) {
        return locationRepository.findByCountry(country);
    }

    @Override
    public List<Location> findByCity(String city) {
        return locationRepository.findByCity(city);
    }

    @Override
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }
}
