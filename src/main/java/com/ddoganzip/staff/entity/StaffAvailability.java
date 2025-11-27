package com.ddoganzip.staff.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "staff_availability")
@Getter
@Setter
@NoArgsConstructor
public class StaffAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "available_cooks", nullable = false)
    private Integer availableCooks = 5;  // 가용 요리 담당 직원 수 (기본 5명)

    @Column(name = "available_drivers", nullable = false)
    private Integer availableDrivers = 5;  // 가용 배달 담당 직원 수 (기본 5명)

    @Column(name = "total_cooks", nullable = false)
    private Integer totalCooks = 5;  // 총 요리 담당 직원 수

    @Column(name = "total_drivers", nullable = false)
    private Integer totalDrivers = 5;  // 총 배달 담당 직원 수
}
