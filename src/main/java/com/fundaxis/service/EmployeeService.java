package com.fundaxis.service;

import com.fundaxis.entity.Employee;
import com.fundaxis.repository.EmployeeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fundaxis.exception.EmployeeNotFoundException;
import com.fundaxis.exception.DuplicateEmployeeException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeService {


    ///==================== Constructor Dependency Injection ====================///

    private final EmployeeRepository employeeRepository;

    // EmployeeService needs EmployeeRepository
    // to communicate with the database
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    ///==================== Deactivate Employee ====================///

    // business operations are intentionally using the official employee ID
    @Transactional
    public Employee deactivateEmployee(String employeeId, String reasonOfDeactivation) {

        // Find employee using official Employee ID
        Employee existingEmployee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with Employee ID: " + employeeId + " does not exist"));


        // Only active employees can be deactivated
        if (existingEmployee.getEmployeeStatus() != Employee.EmployeeStatus.ACTIVE){
            throw new IllegalStateException("Only active employees can be deactivated: " + employeeId);

        }

        // Mark the employee as inactive
        existingEmployee.setEmployeeStatus(Employee.EmployeeStatus.INACTIVE);

        // Store the reason for deactivation
        existingEmployee.setDeactivationReason(reasonOfDeactivation);

        // Store the deactivation date and time
        existingEmployee.setDeactivatedAt(LocalDateTime.now());

        // Save the updated employee
        return employeeRepository.save(existingEmployee);
    }


    ///==================== Activate Employee ====================///

    // business operations are intentionally using the official employee ID
    @Transactional
    public Employee activateEmployee(String employeeId) {

        // Find employee using official Employee ID
        Employee existingEmployee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with Employee ID: " + employeeId + " does not exist"));


        // Only inactive employees can be activated
        if (existingEmployee.getEmployeeStatus() != Employee.EmployeeStatus.INACTIVE){
            throw new IllegalStateException("Only inactive employees can be activated: " + employeeId);

        }

        // Change employee status to active
        existingEmployee.setEmployeeStatus(Employee.EmployeeStatus.ACTIVE);

        // Remove old deactivation information
        existingEmployee.setDeactivationReason(null);
        existingEmployee.setDeactivatedAt(null);

        // Save the updated employee
        return employeeRepository.save(existingEmployee);

    }


    ///==================== Employee List ====================///


    ///---------- Get All Employees ----------///

    public List<Employee> getAllEmployeeList() {

        return employeeRepository.findAll();
    }


    ///---------- Get Active Employees ----------///

    public List<Employee> getAllActiveEmployeeList() {

        return employeeRepository.findByEmployeeStatus(
                Employee.EmployeeStatus.ACTIVE
        );
    }


    ///---------- Get Inactive Employees ----------///

    public List<Employee> getAllInactiveEmployeeList() {

        return employeeRepository.findByEmployeeStatus(Employee.EmployeeStatus.INACTIVE);
    }


    ///---------- Read - By Employee Status ----------
    public List<Employee> getAllEmployeeByEmployeeStatus(Employee.EmployeeStatus employeeStatus){

        return employeeRepository.findByEmployeeStatus(employeeStatus);
    }




    ///==================== Create Employee ====================///

    @Transactional
    public Employee createEmployee(Employee employee) {

        // Prevent duplicate Employee ID
        if (employeeRepository.existsByEmployeeId(employee.getEmployeeId())) {
            throw new DuplicateEmployeeException("Employee ID already exists: " + employee.getEmployeeId());
        }

        // Check whether Email already exists or, Prevent duplicate email
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new DuplicateEmployeeException("Employee Email already exists: " + employee.getEmail());
        }

        // New employees always start as ACTIVE
        employee.setEmployeeStatus(Employee.EmployeeStatus.ACTIVE);

        // Save employee
        return employeeRepository.save(employee);
    }


    ///---------- Find Employee by Database ID ----------///

    public Employee findEmployeeById(Long id) {

        // Find employee using database ID
        // Throw an error if the employee does not exist
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with Employee ID: " + id + " does not exist"));

    }


    ///---------- Find Employee by Official Employee ID ----------///

    public Employee findEmployeeByEmployeeId(String employeeId) {

        // Find employee using official Employee ID
        // Throw an error if the employee does not exist
        return employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with Employee ID: " + employeeId + " does not exist"));

    }


    ///---------- Update Employee ----------///

    @Transactional
    public Employee updateEmployee(String employeeId, Employee updatedEmployee) {

        // Find existing employee using official Employee ID
        Employee existingEmployee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with Employee ID: " + employeeId + " does not exist"));


        // Prevent using another employee's email or, new email already belongs to another employee or not
        if (employeeRepository.existsByEmailAndEmployeeIdNot(updatedEmployee.getEmail(), employeeId)) {
            throw new DuplicateEmployeeException("Employee Email already exists: " + updatedEmployee.getEmail());
        }

        // Update only editable employee fields
        // Keep database, status, audit, and deactivation fields unchanged
        BeanUtils.copyProperties(
                updatedEmployee,
                existingEmployee,
                "id",
                "employeeId",
                "employeeStatus",
                "createdAt",
                "updatedAt",
                "deactivationReason",
                "deactivatedAt"
        );

        // Step 3: Save updated employee
        return employeeRepository.save(existingEmployee);
    }

}