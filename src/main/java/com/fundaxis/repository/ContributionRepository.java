package com.fundaxis.repository;

import com.fundaxis.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    // Find all contributions belonging to a specific Employee
    // Example: employeeId = "EMP001"
    Optional<Contribution> findByEmployeeId(String employeeId);



}
