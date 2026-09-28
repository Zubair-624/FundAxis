package com.fundaxis.security;

import com.fundaxis.entity.Employee;
import com.fundaxis.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
        Employee existEmployeeInDatabase = findEmployeeByEmployeeId.orElseThrow(() -> new RuntimeException("Employee is not found"));

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
    public Employee reactivteBankdEmployee(String employeeId){

        // Step 1: Find the employee by ID
        Optional<Employee> findEmployeeByEmployeeId = employeeRepository.findByEmployeeId(employeeId);

        // Step 2: Make sure the employee exists
        Employee existEmployeeInDatabase = findEmployeeByEmployeeId.orElseThrow(() -> new RuntimeException("Employee is not found"));

        // Step 3: Change the employee status to ACTIVE
        existEmployeeInDatabase.setEmployeeStatus(Employee.EmployeeStatus.ACTIVE);

        // Step 4: Store the reason for deactivation (Null)
        existEmployeeInDatabase.setDeactivationReason(null);

        // Step 5: Store the date and time of deactivation (Null)
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
    public Employee createBankEMployee(Employee employee){

        // Step 1: Check if the employee ID already exists
        boolean employeeExistsByEmployeeId = employeeRepository.existsByEmployeeId(employee.getEmployeeId());

        if (employeeExistsByEmployeeId){
            throw new RuntimeException("Employee ID is already exists: " + employee.getEmployeeId());
        }

        // Step 2: Check if the email already exists
        boolean employeeExistsByEmail = employeeRepository.existsByEmail(employee.getEmail());

        if (employeeExistsByEmail){
            throw new RuntimeException("Employee Email is already exists: " + employee.getEmail());
        }

        // Step 3: Set the initial employee status (ACTIVE)
        employee.setEmployeeStatus(Employee.EmployeeStatus.ACTIVE);

        // Step 4: Save the new employee
        return employeeRepository.save(employee);

    }


    ///---------- Read ----------

    // Find a Employee by -> id
    public Optional<Employee> findEmployeeById(Long id){
        return employeeRepository.findById(id);
    }

    // Find a employee by id -> employeeId
    public Optional<Employee> findEmployeeByEmployeeId(String employeeId){
        return employeeRepository.findByEmployeeId(employeeId);
    }



    ///---------- Update ----------

    // Update an existing Employee by -> employeeId
    public Employee updateEmployeeByEmployeeId(String employeeId, Employee employee){

        if (employee.getEmployeeId() == null){
            throw new RuntimeException("Employee ID is required for update");
        }
        if (!employeeRepository.existsById(employee.getId())){
            throw new RuntimeException("Employee does not exist");
        }

        return employeeRepository.save(employee);
    }





}
