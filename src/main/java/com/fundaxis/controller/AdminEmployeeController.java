package com.fundaxis.controller;

// Employee entity, exception, and service
import com.fundaxis.entity.Employee;
import com.fundaxis.exception.DuplicateEmployeeException;
import com.fundaxis.service.EmployeeService;
// Runs the validation rules (@NotBlank, etc.) on the form object
import jakarta.validation.Valid;
// Spring classes
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

///==================== Admin Employee Controller ====================///
// Serves the admin employee pages (HTML), not a REST API

// @Controller returns template names, not JSON
@Controller
// Only ADMIN can open any page in this class
@PreAuthorize("hasRole('ADMIN')")
public class AdminEmployeeController {

    // Used to load and save employee data
    private final EmployeeService employeeService;

    ///---------- Constructor Injection ----------
    // Spring passes the service in automatically
    public AdminEmployeeController(EmployeeService employeeService) {
        // Save it for later use
        this.employeeService = employeeService;

    }


    ///==================== Employee Management List ====================///

    ///---------- Show The Admin Employee Management Page ----------

    // Handles GET requests to /admin/employees
    @GetMapping("/admin/employees")
    public String employeeManagementPage(
            @RequestParam(required = false) String keyword,   // Search text (optional)
            @RequestParam(required = false) String status,    // Status filter: ALL / ACTIVE / INACTIVE (optional)
            Model model) {                                    // Used to send data to the Thymeleaf page

        // Get the complete employee list from the database
        List<Employee> allEmployees = employeeService.getAllEmployeeList();

        // ---------- Summary cards (always use the FULL list) ----------

        // Total number of employees
        long totalEmployees = allEmployees.size();

        // Count employees whose status is ACTIVE
        long activeEmployees = allEmployees.stream()
                .filter(e -> e.getEmployeeStatus() == Employee.EmployeeStatus.ACTIVE)   // Keep only ACTIVE
                .count();                                                               // Count them

        // Count employees whose status is INACTIVE
        long inactiveEmployees = allEmployees.stream()
                .filter(e -> e.getEmployeeStatus() == Employee.EmployeeStatus.INACTIVE) // Keep only INACTIVE
                .count();                                                               // Count them

        // Send the summary numbers to the page
        model.addAttribute("totalEmployees", totalEmployees);       // Total card
        model.addAttribute("activeEmployees", activeEmployees);     // Active card
        model.addAttribute("inactiveEmployees", inactiveEmployees); // Inactive card

        // ---------- Table filtering ----------

        // Start with all employees, then narrow the list step by step
        List<Employee> filteredEmployees = allEmployees;

        // Filter 1: search by Employee ID, Full Name or Email
        if (keyword != null && !keyword.trim().isEmpty()) {         // Only run if user typed something

            // Remove extra spaces and make lowercase for case-insensitive search
            String searchKeyword = keyword.trim().toLowerCase();

            filteredEmployees = filteredEmployees.stream()
                    .filter(e ->
                            // Match by official bank Employee ID (null check avoids errors)
                            (e.getEmployeeId() != null
                                    && e.getEmployeeId().toLowerCase().contains(searchKeyword))

                                    // OR match by Full Name
                                    || (e.getFullName() != null
                                    && e.getFullName().toLowerCase().contains(searchKeyword))

                                    // OR match by Email
                                    || (e.getEmail() != null
                                    && e.getEmail().toLowerCase().contains(searchKeyword)))
                    .toList();                                      // Collect the matching employees
        }

        // Filter 2: employee status (skipped when empty or "ALL")
        if (status != null                                          // Status was provided
                && !status.isBlank()                                // and is not empty
                && !status.equalsIgnoreCase("ALL")) {               // and is not "ALL"

            filteredEmployees = filteredEmployees.stream()
                    .filter(e -> e.getEmployeeStatus() != null      // Avoid null status errors
                            && e.getEmployeeStatus().name().equalsIgnoreCase(status)) // Compare with selected status
                    .toList();                                      // Collect the matching employees
        }

        // ---------- Send data to the Thymeleaf view ----------

        // The employee rows shown in the table
        model.addAttribute("employees", filteredEmployees);

        // Keep the search box value after page reload
        model.addAttribute("keyword", keyword == null ? "" : keyword);

        // Keep the status dropdown value after page reload (default is ALL)
        model.addAttribute("status", status == null ? "ALL" : status);

        // Number of rows currently displayed in the table
        model.addAttribute("resultCount", filteredEmployees.size());

        // Open the template: templates/admin/employees.html
        return "admin/employees";
    }


    ///==================== Add Employee ====================///

    ///---------- Show The Add Employee Form ----------
    // Opens at /admin/employees/new
    @GetMapping("/admin/employees/new")
    public String showAddEmployeeForm(Model model) {

        // Empty employee for the form to fill in
        Employee employee = new Employee();

        // Send it to the template (the form uses th:object="${employee}")
        model.addAttribute("employee", employee);

        // Tell the shared form this is "add" mode
        model.addAttribute("editMode", false);

        // Show templates/admin/employee-form.html
        return "admin/employee-form";

    }


    ///---------- Create An Employee From The Form ----------
    // Form submit (POST) to /admin/employees
    @PostMapping("/admin/employees")
    // @ModelAttribute fills the Employee from the form fields, @Valid checks the validation rules
    // BindingResult holds the validation errors, RedirectAttributes carries a message to the next page
    public String createEmployee(@Valid @ModelAttribute("employee") Employee employee, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        // Validation failed: show the form again with the field errors
        if (bindingResult.hasErrors()) {

            // Keep the page in "add" mode
            model.addAttribute("editMode", false);

            // Show templates/admin/employee-form.html
            return "admin/employee-form";

        }

        try {

            // The service checks duplicate Employee ID and email, and sets the starting ACTIVE status
            employeeService.createEmployee(employee);

            // Message for the next page, it disappears after one redirect
            redirectAttributes.addFlashAttribute("successMessage", "Employee created successfully.");

            // Success: go back to the employee list
            return "redirect:/admin/employees";

        } catch (DuplicateEmployeeException exception) {

            // Duplicate Employee ID or email: send the message to the form
            model.addAttribute("errorMessage", exception.getMessage());

            // Keep the page in "add" mode
            model.addAttribute("editMode", false);

            // Show the form again with the error message
            return "admin/employee-form";

        }

    }


    ///==================== View Employee Details ====================///

    ///---------- Show One Employee's Details Page ----------
    // Opens at /admin/employees/{employeeId}, for example /admin/employees/EMP001
    @GetMapping("/admin/employees/{employeeId}")
    // @PathVariable reads the {employeeId} from the URL
    public String viewEmployeeDetails(@PathVariable String employeeId, Model model) {

        // Find the employee by the official Employee ID (like EMP001)
        Employee employee = employeeService.findEmployeeByEmployeeId(employeeId);

        // Send the employee to the details page
        model.addAttribute("employee", employee);

        // Show templates/admin/employee-details.html
        return "admin/employee-details";

    }


    ///==================== Edit Employee ====================///

    ///---------- Show The Edit Employee Form ----------
    // Opens at /admin/employees/{employeeId}/edit, for example /admin/employees/EMP001/edit
    @GetMapping("/admin/employees/{employeeId}/edit")
    public String showEditEmployeeForm(@PathVariable String employeeId, Model model) {

        // Find the employee by the official Employee ID
        Employee employee = employeeService.findEmployeeByEmployeeId(employeeId);

        // Send the employee to the form so its fields are pre-filled
        model.addAttribute("employee", employee);

        // Tell the shared form this is "edit" mode
        model.addAttribute("editMode", true);

        // Reuse templates/admin/employee-form.html, the same form as "add employee"
        return "admin/employee-form";

    }


    ///---------- Update An Employee From The Edit Form ----------
    // Form submit (POST) to /admin/employees/{employeeId}/update
    @PostMapping("/admin/employees/{employeeId}/update")
    public String updateEmployee(@PathVariable String employeeId, @Valid @ModelAttribute("employee") Employee employee, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        // Validation failed: show the form again with the field errors
        if (bindingResult.hasErrors()) {

            // Keep the page in "edit" mode
            model.addAttribute("editMode", true);

            // Show templates/admin/employee-form.html
            return "admin/employee-form";

        }

        try {

            // The service updates only the editable fields
            employeeService.updateEmployee(employeeId, employee);

            // Message for the next page, it disappears after one redirect
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully.");

            // Success: go to this employee's details page
            return "redirect:/admin/employees/" + employeeId;

        } catch (DuplicateEmployeeException exception) {

            // Duplicate email or another business error: send the message to the form
            model.addAttribute("errorMessage", exception.getMessage());

            // Keep the page in "edit" mode
            model.addAttribute("editMode", true);

            // Show the form again with the error message
            return "admin/employee-form";

        }

    }



    // ==================== Activate Employee ==================== //

    // POST /admin/employees/{employeeId}/activate
    @PostMapping("/admin/employees/{employeeId}/activate")
    public String activateEmployee(
            @PathVariable String employeeId,
            RedirectAttributes redirectAttributes
    ) {

        try {

            // Activate the employee using the official Employee ID
            employeeService.activateEmployee(employeeId);

            // Success message shown on the next page
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Employee activated successfully."
            );

        } catch (IllegalStateException exception) {

            // Error message from the service (for example, already active)
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        // Go back to the employee details page
        return "redirect:/admin/employees/" + employeeId;
    }


    ///==================== Deactivate Employee ====================///

    ///---------- Deactivate An Employee ----------
    // Form submit (POST) to /admin/employees/{employeeId}/deactivate, for example /admin/employees/EMP001/deactivate
    @PostMapping("/admin/employees/{employeeId}/deactivate")
    // @PathVariable reads {employeeId} from the URL, @RequestParam reads the reason from the form field
    public String deactivateEmployee(@PathVariable String employeeId, @RequestParam String reasonOfDeactivation, RedirectAttributes redirectAttributes) {

        try {

            // The reason is required: stop if it is empty or only spaces
            if (reasonOfDeactivation == null || reasonOfDeactivation.trim().isEmpty()) {

                // Error message for the next page, it disappears after one redirect
                redirectAttributes.addFlashAttribute("errorMessage", "Deactivation reason is required.");

                // Go back to this employee's details page
                return "redirect:/admin/employees/" + employeeId;

            }

            // Deactivate using the official bank Employee ID, the reason is saved without extra spaces
            employeeService.deactivateEmployee(employeeId, reasonOfDeactivation.trim());

            // Success message for the next page
            redirectAttributes.addFlashAttribute("successMessage", "Employee deactivated successfully.");

            // Go back to this employee's details page
            return "redirect:/admin/employees/" + employeeId;

        } catch (IllegalStateException exception) {

            // The service refused (for example the employee is already inactive): show its message
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());

            // Go back to this employee's details page
            return "redirect:/admin/employees/" + employeeId;

        }

    }
}