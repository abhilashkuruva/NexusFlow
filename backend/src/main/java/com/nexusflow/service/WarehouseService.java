package com.nexusflow.service;

import com.nexusflow.entity.Warehouse;
import com.nexusflow.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findByIsActiveTrueOrderByWarehouseNameAsc();
    }

    public Optional<Warehouse> getWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }

    public Optional<Warehouse> getWarehouseByName(String name) {
        return warehouseRepository.findByWarehouseName(name);
    }

    public List<Warehouse> getWarehousesByCountry(String country) {
        return warehouseRepository.findByCountryAndIsActiveTrue(country);
    }

    public List<String> getAllCountries() {
        return warehouseRepository.findAllCountries();
    }

    public List<Warehouse> searchWarehouses(String searchTerm) {
        return warehouseRepository.searchWarehouses(searchTerm);
    }

    public Warehouse createWarehouse(Warehouse warehouse) {
        if (warehouseRepository.existsByWarehouseName(warehouse.getWarehouseName())) {
            throw new RuntimeException("Warehouse with this name already exists");
        }

        if (warehouse.getIsActive() == null) {
            warehouse.setIsActive(true);
        }

        return warehouseRepository.save(warehouse);
    }

    public Warehouse updateWarehouse(Long id, Warehouse warehouseDetails) {
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(id);
        if (warehouseOpt.isEmpty()) {
            throw new RuntimeException("Warehouse not found");
        }

        Warehouse warehouse = warehouseOpt.get();

        if (warehouseDetails.getWarehouseName() != null) {
            warehouse.setWarehouseName(warehouseDetails.getWarehouseName());
        }
        if (warehouseDetails.getLocation() != null) {
            warehouse.setLocation(warehouseDetails.getLocation());
        }
        if (warehouseDetails.getCountry() != null) {
            warehouse.setCountry(warehouseDetails.getCountry());
        }
        if (warehouseDetails.getCity() != null) {
            warehouse.setCity(warehouseDetails.getCity());
        }
        if (warehouseDetails.getAddress() != null) {
            warehouse.setAddress(warehouseDetails.getAddress());
        }
        if (warehouseDetails.getCapacity() != null) {
            warehouse.setCapacity(warehouseDetails.getCapacity());
        }
        if (warehouseDetails.getManagerName() != null) {
            warehouse.setManagerName(warehouseDetails.getManagerName());
        }
        if (warehouseDetails.getContactPhone() != null) {
            warehouse.setContactPhone(warehouseDetails.getContactPhone());
        }
        if (warehouseDetails.getContactEmail() != null) {
            warehouse.setContactEmail(warehouseDetails.getContactEmail());
        }
        if (warehouseDetails.getWarehouseType() != null) {
            warehouse.setWarehouseType(warehouseDetails.getWarehouseType());
        }
        if (warehouseDetails.getIsActive() != null) {
            warehouse.setIsActive(warehouseDetails.getIsActive());
        }

        return warehouseRepository.save(warehouse);
    }

    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new RuntimeException("Warehouse not found");
        }
        warehouseRepository.deleteById(id);
    }

    public long countActiveWarehouses() {
        return warehouseRepository.countActiveWarehouses();
    }

    public long countByCountry(String country) {
        return warehouseRepository.countByCountry(country);
    }

    public Long getTotalCapacity() {
        Long capacity = warehouseRepository.getTotalCapacity();
        return capacity != null ? capacity : 0L;
    }
}