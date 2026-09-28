package com.fundaxis.controller;

import com.fundaxis.dto.DeactivateRequest;
import com.fundaxis.entity.Employee;
import com.fundaxis.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {


    private final EmployeeService employeeService;

    // Inject EmployeeService into the Service
    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    ///---------- Deactivate an Employee ----------///
    @PatchMapping("/{employeeId}/deactivate")
    public ResponseEntity<Employee> deactivateEmployee(@PathVariable String employeeId, @Valid @RequestBody DeactivateRequest deactivateRequest){

        Employee deactivateEmployee = employeeService.deactivatedBankEmployee(employeeId, deactivateRequest.getDeactivateReason());

        return ResponseEntity.ok(deactivateEmployee);
    }

    ///---------- Activate an Employee ----------///
    @PatchMapping("/{employeeId}/activate")
    public ResponseEntity<Employee> activateEmployee(@PathVariable String employeeId){

        Employee activateEmployee = employeeService.reactivateBankEmployee(employeeId);

        return ResponseEntity.ok(activateEmployee);
    }

    ///---------- List ----------///

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
    public ResponseEntity<List<Employee>> getAllInDeactivateEmployeeList(){

        return ResponseEntity.ok(employeeService.getAllDeactivateEmployeeList());
    }


    ///---------- Create ----------///

    // Create a new Employee
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee){

        Employee createNewEmployee = employeeService.createBankEmployee(employee);

        return ResponseEntity.ok(createNewEmployee);
    }


    ///---------- Read ----------///

    // Get Employee by -> id
    @GetMapping("/id/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable Long id){

        return employeeService.findEmployeeById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Get Employee by -> employeeId
    @GetMapping("/employeeId/{employeeId}")
    public ResponseEntity<Employee> getEmployeeByEmployeeId(
            @PathVariable String employeeId){

        return employeeService.findEmployeeByEmployeeId(employeeId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    ///---------- Update ----------///

    // Update an existing employee with -> employeeId
    @PutMapping("/{employeeId}")
    public ResponseEntity<Employee> updateEmployeeByEmployeeId(@PathVariable String employeeId, @Valid @RequestBody Employee employee){

        Employee updateEmployee = employeeService.updateEmployeeByEmployeeId(employeeId, employee);

        return ResponseEntity.ok(updateEmployee);
    }




}