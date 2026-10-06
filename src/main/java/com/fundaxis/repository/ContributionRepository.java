package com.fundaxis.repository;

import com.fundaxis.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    // Find all contributions belonging to a specific Employee
    List<Contribution> findByEmployeeEmployeeId(String employeeId);

    // Check whether an employee already has a contribution for this month
    boolean existsByEmployeeEmployeeIdAndContributionMonth(String employeeId, LocalDate contributionMonth);

    // Check whether another contribution already uses this month
    boolean existsByEmployeeEmployeeIdAndContributionMonthAndIdNot(String employeeId, LocalDate contributionMonth, Long id);

}





