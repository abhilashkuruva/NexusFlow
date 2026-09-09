package com.nexusflow.service;

import com.nexusflow.entity.Location;
import java.util.List;

public interface LocationService {
    List<Location> getAllLocations();
    Location getLocationById(Long id);
    List<Location> findByCountry(String country);
    List<Location> findByCity(String city);
    Location saveLocation(Location location);
    void deleteLocation(Long id);
}
