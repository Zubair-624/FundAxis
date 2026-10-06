package com.fundaxis.controller;

import com.fundaxis.dto.DeactivateRequest;
import com.fundaxis.entity.Employee;
import com.fundaxis.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {


    private final EmployeeService employeeService;

    // Inject EmployeeService into the Service
    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    ///==================== Deactivate an Employee ====================///
    @PatchMapping("/{employeeId}/deactivate")
    public ResponseEntity<Employee> deactivateEmployee(@PathVariable String employeeId, @Valid @RequestBody DeactivateRequest deactivateRequest){

        Employee deactivateEmployee = employeeService.deactivateEmployee(employeeId, deactivateRequest.getDeactivateReason());

        return ResponseEntity.ok(deactivateEmployee);

    }

    ///==================== Activate an Employee ====================///
    @PatchMapping("/{employeeId}/activate")
    public ResponseEntity<Employee> activateEmployee(@PathVariable String employeeId){

        Employee activateEmployee = employeeService.activateEmployee(employeeId);

        return ResponseEntity.ok(activateEmployee);
    }

    ///==================== List ====================///

    // Get all Employee List and return with HTTP 200 OK
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployeeList(){

        return ResponseEntity.ok(employeeService.getAllEmployeeList());
    }

    @GetMapping("/active")
    // Get all Active Employee List and return with HTTP 200 OK
    public ResponseEntity<List<Employee>> getAllActiveEmployeeList(){

        return ResponseEntity.ok(employeeService.getAllActiveEmployeeList());
    }

    @GetMapping("/inactive")
    // Get all deactivate Employee List and return with HTTP 200 OK
    public ResponseEntity<List<Employee>> getAllInactiveEmployeeList(){

        return ResponseEntity.ok(employeeService.getAllInactiveEmployeeList());
    }


    ///---------- Get Employees By Status ----------
    @GetMapping("/status/{employeeStatus}")
    public ResponseEntity<List<Employee>> getAllEmployeeByEmployeeStatus(@PathVariable Employee.EmployeeStatus employeeStatus){

        return ResponseEntity.ok(employeeService.getAllEmployeeByEmployeeStatus(employeeStatus));

    }


    ///==================== Create ====================///

    // Create a new Employee
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee){

        // Create and save a new employee
        Employee createNewEmployee = employeeService.createEmployee(employee);

        // Return HTTP 201 CREATED after successful employee creation
        return ResponseEntity.status(HttpStatus.CREATED).body(createNewEmployee);

    }


    ///==================== Read ====================///

    // Get Employee by -> id
    @GetMapping("/id/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id){

        // Return employee using database ID
        return ResponseEntity.ok(employeeService.findEmployeeById(id));

    }

    // Get Employee by -> employeeId
    @GetMapping("/employeeId/{employeeId}")
    public ResponseEntity<Employee> getEmployeeByEmployeeId(@PathVariable String employeeId){

        // Return employee using official Employee ID
        return ResponseEntity.ok(employeeService.findEmployeeByEmployeeId(employeeId));

    }


    ///==================== Update ====================///

    // Update an existing employee with -> employeeId
    @PutMapping("/{employeeId}")
    public ResponseEntity<Employee> updateEmployeeByEmployeeId(@PathVariable String employeeId, @Valid @RequestBody Employee employee){

        Employee updateEmployee = employeeService.updateEmployee(employeeId, employee);

        return ResponseEntity.ok(updateEmployee);
    }




}