package com.nexusflow.controller;

import com.nexusflow.entity.Location;
import com.nexusflow.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        Location location = locationService.getLocationById(id);
        return (location != null) ? ResponseEntity.ok(location) : ResponseEntity.notFound().build();
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<Location>> getByCountry(@PathVariable String country) {
        return ResponseEntity.ok(locationService.findByCountry(country));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Location>> getByCity(@PathVariable String city) {
        return ResponseEntity.ok(locationService.findByCity(city));
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        return ResponseEntity.ok(locationService.saveLocation(location));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
