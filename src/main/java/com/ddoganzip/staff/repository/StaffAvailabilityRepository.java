package com.ddoganzip.staff.repository;

import com.ddoganzip.staff.entity.StaffAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffAvailabilityRepository extends JpaRepository<StaffAvailability, Long> {
    default StaffAvailability getStaffAvailability() {
        return findById(1L).orElseGet(() -> {
            StaffAvailability sa = new StaffAvailability();
            return save(sa);
        });
    }
}
