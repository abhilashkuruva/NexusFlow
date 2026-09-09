package com.nexusflow.repository;

import com.nexusflow.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByCountry(String country);
    List<Location> findByCity(String city);
}
