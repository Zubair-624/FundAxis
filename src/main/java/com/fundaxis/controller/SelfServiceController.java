package com.fundaxis.controller;

import com.fundaxis.entity.Contribution;
import com.fundaxis.entity.Employee;
import com.fundaxis.entity.Loan;
import com.fundaxis.entity.LoanDisbursement;
import com.fundaxis.entity.LoanExtension;
import com.fundaxis.entity.LoanRepayment;
import com.fundaxis.entity.RepaymentSchedule;
import com.fundaxis.security.CurrentUserService;
import com.fundaxis.service.ContributionService;
import com.fundaxis.service.LoanDisbursementService;
import com.fundaxis.service.LoanExtensionService;
import com.fundaxis.service.LoanRepaymentService;
import com.fundaxis.service.LoanService;
import com.fundaxis.service.RepaymentScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

///==================== Self Service Controller ====================///
// Access: USER
// Every endpoint returns only the data of the currently authenticated employee
// The client never sends an Employee ID, the backend gets it from the logged-in UserAccount
// This prevents a normal user from changing an ID in the URL to see another employee's data
@RestController
@RequestMapping("/api/me")
@PreAuthorize("hasRole('USER')")
public class SelfServiceController {

    private final CurrentUserService currentUserService;
    private final ContributionService contributionService;
    private final LoanService loanService;
    private final LoanExtensionService loanExtensionService;
    private final LoanDisbursementService loanDisbursementService;
    private final RepaymentScheduleService repaymentScheduleService;
    private final LoanRepaymentService loanRepaymentService;

    ///---------- Constructor Injection ----------
    public SelfServiceController(CurrentUserService currentUserService, ContributionService contributionService, LoanService loanService, LoanExtensionService loanExtensionService, LoanDisbursementService loanDisbursementService, RepaymentScheduleService repaymentScheduleService, LoanRepaymentService loanRepaymentService) {
        this.currentUserService = currentUserService;
        this.contributionService = contributionService;
        this.loanService = loanService;
        this.loanExtensionService = loanExtensionService;
        this.loanDisbursementService = loanDisbursementService;
        this.repaymentScheduleService = repaymentScheduleService;
        this.loanRepaymentService = loanRepaymentService;

    }


    ///==================== My Profile ====================///

    ///---------- Return The Employee Profile Linked To The Currently Authenticated User ----------
    // The Employee is determined from the authenticated UserAccount
    @GetMapping("/profile")
    public ResponseEntity<Employee> getMyProfile() {

        Employee employee = currentUserService.getCurrentEmployee();

        return ResponseEntity.ok(employee);

    }


    ///==================== My Contributions ====================///

    ///---------- Return Contributions Belonging Only To The Currently Authenticated Employee ----------
    @GetMapping("/contributions")
    public ResponseEntity<List<Contribution>> getMyContributions() {

        String employeeId = currentUserService.getCurrentEmployeeId();

        List<Contribution> contributions = contributionService.getContributionsByEmployeeId(employeeId);

        return ResponseEntity.ok(contributions);

    }


    ///==================== My Loans ====================///

    ///---------- Return Loans Belonging Only To The Currently Authenticated Employee ----------
    @GetMapping("/loans")
    public ResponseEntity<List<Loan>> getMyLoans() {

        String employeeId = currentUserService.getCurrentEmployeeId();

        List<Loan> loans = loanService.getEmployeeLoans(employeeId);

        return ResponseEntity.ok(loans);

    }


    ///---------- Return Loan Extension Requests Belonging Only To The Currently Authenticated Employee ----------
    @GetMapping("/loan-extensions")
    public ResponseEntity<List<LoanExtension>> getMyLoanExtensions() {

        String employeeId = currentUserService.getCurrentEmployeeId();

        List<LoanExtension> loanExtensions = loanExtensionService.getEmployeeExtensions(employeeId);

        return ResponseEntity.ok(loanExtensions);

    }


    ///---------- Return Loan Disbursement Records Belonging Only To The Currently Authenticated Employee ----------
    @GetMapping("/disbursements")
    public ResponseEntity<List<LoanDisbursement>> getMyDisbursements() {

        String employeeId = currentUserService.getCurrentEmployeeId();

        List<LoanDisbursement> disbursements = loanDisbursementService.getEmployeeDisbursements(employeeId);

        return ResponseEntity.ok(disbursements);

    }


    ///==================== My Repayments ====================///

    ///---------- Return Repayment Schedules Belonging Only To Loans Of The Currently Authenticated Employee ----------
    @GetMapping("/repayment-schedules")
    public ResponseEntity<List<RepaymentSchedule>> getMyRepaymentSchedules() {

        String employeeId = currentUserService.getCurrentEmployeeId();

        List<RepaymentSchedule> repaymentSchedules = repaymentScheduleService.getEmployeeRepaymentSchedulesList(employeeId);

        return ResponseEntity.ok(repaymentSchedules);

    }


    ///---------- Return Actual Loan Repayment Transactions Belonging Only To The Currently Authenticated Employee ----------
    @GetMapping("/loan-repayments")
    public ResponseEntity<List<LoanRepayment>> getMyLoanRepayments() {

        String employeeId = currentUserService.getCurrentEmployeeId();

        List<LoanRepayment> loanRepayments = loanRepaymentService.getEmployeeLoanRepayments(employeeId);

        return ResponseEntity.ok(loanRepayments);

    }
}