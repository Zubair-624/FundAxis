package com.fundaxis.service;

import com.fundaxis.entity.Employee;
import com.fundaxis.repository.EmployeeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {


    ///---------- Constructor Dependency Injection ----------

    private final EmployeeRepository employeeRepository;

    // EmployeeService needs EmployeeRepository to communicate with the database
    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }


    ///---------- deactivate A employee ----------

    @Transactional
    public Employee deactivatedBankEmployee(String employeeId, String reasonOfDeactivation){

        // Step 1: Find the employee by ID
        Optional<Employee> findEmployeeByEmployeeId = employeeRepository.findByEmployeeId(employeeId);

        // Step 2: Make sure the employee exists
        Employee existEmployeeInDatabase = findEmployeeByEmployeeId
                .orElseThrow(() -> new RuntimeException("Employee is not found"));

        // Step 3: Change the employee status to INACTIVE
        existEmployeeInDatabase.setEmployeeStatus(Employee.EmployeeStatus.INACTIVE);

        // Step 4: Store the reason for deactivation
        existEmployeeInDatabase.setDeactivationReason(reasonOfDeactivation);

        // Step 5: Store the date and time of deactivation
        LocalDateTime employeeDeactivationDateAndTime = LocalDateTime.now();

        existEmployeeInDatabase.setDeactivatedAt(employeeDeactivationDateAndTime);

        // Step 6: Save the updated employee
        return employeeRepository.save(existEmployeeInDatabase);

    }


    ///---------- reactivate A employee ----------

    @Transactional
    public Employee reactivateBankEmployee(String employeeId){

        // Step 1: Find the employee by ID
        Optional<Employee> findEmployeeByEmployeeId = employeeRepository.findByEmployeeId(employeeId);

        // Step 2: Make sure the employee exists
        Employee existEmployeeInDatabase = findEmployeeByEmployeeId
                .orElseThrow(() -> new RuntimeException("Employee is not found"));

        // Step 3: Change the employee status to ACTIVE
        existEmployeeInDatabase.setEmployeeStatus(Employee.EmployeeStatus.ACTIVE);

        // Step 4: Store the reason for deactivation (Null)
        existEmployeeInDatabase.setDeactivationReason(null);

        // Step 5: Store the deactivation time (Null)
        existEmployeeInDatabase.setDeactivatedAt(null);

        // Step 6: Save the updated employee
        return employeeRepository.save(existEmployeeInDatabase);

    }



    ///==================== Business Logic - CRUD Operation ====================///


    ///---------- List ----------

    // Get all(List) Employee List
    public List<Employee> getAllEmployeeList(){

        return employeeRepository.findAll();
    }

    // Get Active Employee List
    public List<Employee> getAllActiveEmployeeList(){

        return employeeRepository.findByEmployeeStatus(Employee.EmployeeStatus.ACTIVE);
    }

    // Get Deactivate Employee List
    public List<Employee> getAllDeactivateEmployeeList(){

        return employeeRepository.findByEmployeeStatus(Employee.EmployeeStatus.INACTIVE);
    }


    ///---------- Create ----------

    // Create a new Employee
    @Transactional
    public Employee createBankEmployee(Employee employee){

        // find exist employee by -> employeeId
        if (employeeRepository.existsByEmployeeId(employee.getEmployeeId())){
            throw new RuntimeException("Employee ID already exists: " + employee.getEmployeeId());
        }

        // find exist employee by -> email
        if (employeeRepository.existsByEmail(employee.getEmail())){
            throw new RuntimeException("Employee Email already exists: " + employee.getEmail());
        }

        // Set the initial employee status (ACTIVE)
        employee.setEmployeeStatus(Employee.EmployeeStatus.ACTIVE);

        // Save the new employee
        return employeeRepository.save(employee);

    }


    ///---------- Read ----------

    // Find an Employee by -> id
    public Optional<Employee> findEmployeeById(Long id){

        return employeeRepository.findById(id);
    }

    // Find an employee by id -> employeeId
    public Optional<Employee> findEmployeeByEmployeeId(String employeeId){

        return employeeRepository.findByEmployeeId(employeeId);
    }



    ///---------- Update ----------

    // Update an existing Employee by -> employeeId
    @Transactional
    public Employee updateEmployeeByEmployeeId(String employeeId, Employee updateEmployee){

        // Step 1: Find the existing employee
        Employee existEmployeeInDatabase = employeeRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() -> new RuntimeException("Employee with Employee ID: " + employeeId + " does not exist"));

        // Step 2: Check whether the new email
        // is already being used by another employee
        if (employeeRepository.existsByEmailAndEmployeeIdNot(updateEmployee.getEmail(), employeeId)){

            throw new RuntimeException("Employee Email already exists: " + updateEmployee.getEmail());
        }

        // Step 3: Copy editable fields dynamically
        //
        // The fields below are excluded because
        // they are controlled by the system/workflow.
        BeanUtils.copyProperties(
                updateEmployee,
                existEmployeeInDatabase,
                "id",
                "employeeId",
                "employeeStatus",
                "createdAt",
                "updatedAt",
                "deactivationReason",
                "deactivatedAt"
        );

        // Step 4: Save the updated employee
        return employeeRepository.save(existEmployeeInDatabase);

    }


}