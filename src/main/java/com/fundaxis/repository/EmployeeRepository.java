package com.fundaxis.repository;

import com.fundaxis.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Find employee using official NBL Employee ID (employeeId)
    Optional<Employee> findByEmployeeId(String employeeId);

    // Check duplicate Employee ID (employeeId) during creation
    boolean existsByEmployeeId(String employeeId);

    // Check duplicate email during creation
    boolean existsByEmail(String email);

    // Get employees by status
    List<Employee> findByEmployeeStatus(Employee.EmployeeStatus employeeStatus);

    // Does an employee already exist with this email, but with a different employee ID?
    boolean existsByEmailAndEmployeeIdNot(String email, String employeeId);

}