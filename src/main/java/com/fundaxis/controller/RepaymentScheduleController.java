package com.fundaxis.controller;

import com.fundaxis.entity.RepaymentSchedule;
import com.fundaxis.service.RepaymentScheduleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/repayment-schedules")
public class RepaymentScheduleController {

    private final RepaymentScheduleService repaymentScheduleService;

    ///---------- Constructor Injection ----------
    public RepaymentScheduleController(RepaymentScheduleService repaymentScheduleService) {
        this.repaymentScheduleService = repaymentScheduleService;

    }


    ///==================== Create LoanRepayment Schedule ====================///

    ///---------- Create A LoanRepayment Schedule Installment For A Loan ----------
    @PostMapping("/loan/{loanId}")
    public ResponseEntity<RepaymentSchedule> createRepaymentSchedule(@PathVariable Long loanId, @Valid @RequestBody RepaymentSchedule repaymentSchedule) {

        RepaymentSchedule createdSchedule = repaymentScheduleService.createRepaymentSchedule(loanId, repaymentSchedule);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);

    }


    ///==================== Read LoanRepayment Schedule ====================///

    ///---------- Get All LoanRepayment Schedules ----------
    @GetMapping
    public ResponseEntity<List<RepaymentSchedule>> getAllRepaymentSchedulesList() {

        return ResponseEntity.ok(repaymentScheduleService.getAllRepaymentSchedulesList());

    }


    ///---------- Get LoanRepayment Schedule By Database ID (id) ----------
    @GetMapping("/id/{id}")
    public ResponseEntity<RepaymentSchedule> getRepaymentScheduleById(@PathVariable Long id) {

        return ResponseEntity.ok(repaymentScheduleService.getRepaymentScheduleById(id));

    }


    ///---------- Get All LoanRepayment Schedules For A Loan ----------
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<RepaymentSchedule>> getRepaymentSchedulesListByLoanId(@PathVariable Long loanId) {

        return ResponseEntity.ok(repaymentScheduleService.getRepaymentSchedulesListByLoanId(loanId));

    }


    ///---------- Get A Specific Installment For A Loan ----------
    @GetMapping("/loan/{loanId}/installment/{installmentNumber}")
    public ResponseEntity<RepaymentSchedule> getRepaymentScheduleByLoanAndInstallmentNumber(@PathVariable Long loanId, @PathVariable Integer installmentNumber) {

        return ResponseEntity.ok(repaymentScheduleService.getRepaymentScheduleByLoanAndInstallmentNumber(loanId, installmentNumber));

    }


    ///---------- Get LoanRepayment Schedules By Installment Status ----------
    @GetMapping("/status/{installmentStatus}")
    public ResponseEntity<List<RepaymentSchedule>> getRepaymentSchedulesListByStatus(@PathVariable RepaymentSchedule.InstallmentStatus installmentStatus) {

        return ResponseEntity.ok(repaymentScheduleService.getRepaymentSchedulesListByStatus(installmentStatus));

    }


    ///---------- Get LoanRepayment Schedules For A Loan By Installment Status ----------
    @GetMapping("/loan/{loanId}/status/{installmentStatus}")
    public ResponseEntity<List<RepaymentSchedule>> getLoanRepaymentSchedulesListByStatus(@PathVariable Long loanId, @PathVariable RepaymentSchedule.InstallmentStatus installmentStatus) {

        return ResponseEntity.ok(repaymentScheduleService.getLoanRepaymentSchedulesListByStatus(loanId, installmentStatus));

    }


    ///---------- Get LoanRepayment Schedules With A Specific Due Date ----------
    @GetMapping("/due-date/{dueDate}")
    public ResponseEntity<List<RepaymentSchedule>> getRepaymentSchedulesListByDueDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {

        return ResponseEntity.ok(repaymentScheduleService.getRepaymentSchedulesListByDueDate(dueDate));

    }


    ///---------- Get LoanRepayment Schedules Due Before A Specific Date ----------
    @GetMapping("/due-before/{date}")
    public ResponseEntity<List<RepaymentSchedule>> getRepaymentSchedulesBeforeDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(repaymentScheduleService.getRepaymentSchedulesBeforeDate(date));

    }


    ///---------- Get LoanRepayment Schedules Already Marked As OVERDUE ----------
    @GetMapping("/overdue")
    public ResponseEntity<List<RepaymentSchedule>> getOverdueRepaymentSchedulesList() {

        return ResponseEntity.ok(repaymentScheduleService.getOverdueRepaymentSchedulesList(LocalDate.now()));

    }


    ///---------- Get All LoanRepayment Schedules Belonging To An Employee ----------
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<RepaymentSchedule>> getEmployeeRepaymentSchedulesList(@PathVariable String employeeId) {

        return ResponseEntity.ok(repaymentScheduleService.getEmployeeRepaymentSchedulesList(employeeId));

    }


    ///---------- Get Employee LoanRepayment Schedules By Installment Status ----------
    @GetMapping("/employee/{employeeId}/status/{installmentStatus}")
    public ResponseEntity<List<RepaymentSchedule>> getEmployeeRepaymentSchedulesListByStatus(@PathVariable String employeeId, @PathVariable RepaymentSchedule.InstallmentStatus installmentStatus) {

        return ResponseEntity.ok(repaymentScheduleService.getEmployeeRepaymentSchedulesListByStatus(employeeId, installmentStatus));

    }


    ///==================== Update LoanRepayment Schedule ====================///

    ///---------- Update A PENDING LoanRepayment Schedule ----------
    @PutMapping("/{id}")
    public ResponseEntity<RepaymentSchedule> updateRepaymentSchedule(@PathVariable Long id, @Valid @RequestBody RepaymentSchedule repaymentSchedule) {

        RepaymentSchedule updatedSchedule = repaymentScheduleService.updateRepaymentSchedule(id, repaymentSchedule);

        return ResponseEntity.ok(updatedSchedule);

    }


    ///==================== LoanRepayment Schedule Workflow ====================///

    ///---------- Mark An Unpaid Installment As OVERDUE ----------
    @PatchMapping("/{id}/overdue")
    public ResponseEntity<RepaymentSchedule> markAsOverdue(@PathVariable Long id) {

        RepaymentSchedule updatedSchedule = repaymentScheduleService.markAsOverdue(id);

        return ResponseEntity.ok(updatedSchedule);

    }


    ///---------- Cancel A PENDING LoanRepayment Schedule ----------
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<RepaymentSchedule> cancelRepaymentSchedule(@PathVariable Long id) {

        RepaymentSchedule cancelledSchedule = repaymentScheduleService.cancelRepaymentSchedule(id);

        return ResponseEntity.ok(cancelledSchedule);

    }
}