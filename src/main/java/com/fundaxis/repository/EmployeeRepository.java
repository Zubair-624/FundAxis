package com.fundaxis.repository;

import com.fundaxis.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    // Check whether the email belongs to another employee
    boolean existsByEmailAndEmployeeIdNot(String email, String employeeId);

    List<Employee> findByEmployeeStatus(
            Employee.EmployeeStatus employeeStatus
    );


}