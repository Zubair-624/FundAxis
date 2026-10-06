package com.fundaxis.service;

import com.fundaxis.entity.Employee;
import com.fundaxis.entity.Loan;
import com.fundaxis.exception.DuplicateLoanException;
import com.fundaxis.exception.EmployeeNotFoundException;
import com.fundaxis.exception.LoanNotFoundException;
import com.fundaxis.repository.EmployeeRepository;
import com.fundaxis.repository.LoanRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final EmployeeRepository employeeRepository;

    public LoanService(LoanRepository loanRepository, EmployeeRepository employeeRepository) {
        this.loanRepository = loanRepository;
        this.employeeRepository = employeeRepository;

    }


    ///==================== Create Loan ====================///

    ///---------- Create A New Loan For An Existing Employee ----------
    @Transactional
    public Loan createLoan(String employeeId, Loan loan) {

        // Find the employee using the official Employee ID
        Employee employee = employeeRepository.findByEmployeeId(employeeId).orElseThrow(() -> new EmployeeNotFoundException("Employee with Employee ID: " + employeeId + " does not exist"));

        // Prevent duplicate Loan Application ID
        if (loanRepository.existsByLoanApplicationId(loan.getLoanApplicationId())) {

            throw new DuplicateLoanException("Loan Application ID already exists: " + loan.getLoanApplicationId());

        }

        // Connect the loan with the employee
        loan.setEmployee(employee);

        // Every new loan starts as DRAFT
        loan.setLoanStatus(Loan.LoanStatus.DRAFT);

        // Eligibility must be checked before approval
        loan.setEligibilityStatus(Loan.EligibilityStatus.PENDING);

        // Approval-controlled fields must start empty
        loan.setApprovedAmount(null);
        loan.setApprovalDate(null);
        loan.setInterestRate(null);

        // Rejection information must start empty
        loan.setRejectionReason(null);

        return loanRepository.save(loan);

    }


    ///==================== Read Loan ====================///

    ///---------- Get All Loans ----------
    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {

        return loanRepository.findAll();

    }


    ///---------- Get Loan By Database ID (id) ----------
    @Transactional(readOnly = true)
    public Loan getLoanById(Long id) {

        return loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException("Loan with database ID: " + id + " does not exist"));

    }


    ///---------- Get Loan By Application ID ----------
    @Transactional(readOnly = true)
    public Loan getLoanByApplicationId(String loanApplicationId) {

        return loanRepository.findByLoanApplicationId(loanApplicationId).orElseThrow(() -> new LoanNotFoundException("Loan with Loan Application ID: " + loanApplicationId + " does not exist"));

    }


    ///---------- Get All Loans Of An Employee ----------
    @Transactional(readOnly = true)
    public List<Loan> getEmployeeLoans(String employeeId) {

        // Verify that the employee exists
        if (!employeeRepository.existsByEmployeeId(employeeId)) {

            throw new EmployeeNotFoundException("Employee with Employee ID: " + employeeId + " does not exist");

        }

        return loanRepository.findByEmployeeEmployeeIdOrderByApplicationDateDesc(employeeId);

    }


    ///---------- Get Loans By Loan Status ----------
    @Transactional(readOnly = true)
    public List<Loan> getLoansByLoanStatus(Loan.LoanStatus loanStatus) {

        return loanRepository.findByLoanStatusOrderByApplicationDateDesc(loanStatus);

    }


    ///---------- Get Loans By Eligibility Status ----------
    @Transactional(readOnly = true)
    public List<Loan> getLoansByEligibilityStatus(Loan.EligibilityStatus eligibilityStatus) {

        return loanRepository.findByEligibilityStatusOrderByApplicationDateDesc(eligibilityStatus);

    }


    ///---------- Get Loans By Loan Type ----------
    @Transactional(readOnly = true)
    public List<Loan> getLoansByType(Loan.LoanType loanType) {

        return loanRepository.findByLoanTypeOrderByApplicationDateDesc(loanType);

    }


    ///---------- Get Employee Loans By Status ----------
    @Transactional(readOnly = true)
    public List<Loan> getEmployeeLoansByStatus(String employeeId, Loan.LoanStatus loanStatus) {

        // Verify that the employee exists
        if (!employeeRepository.existsByEmployeeId(employeeId)) {

            throw new EmployeeNotFoundException("Employee with Employee ID: " + employeeId + " does not exist");

        }

        return loanRepository.findByEmployeeEmployeeIdAndLoanStatusOrderByApplicationDateDesc(employeeId, loanStatus);

    }


    ///---------- Get Employee Loans By Eligibility Status ----------
    @Transactional(readOnly = true)
    public List<Loan> getEmployeeLoansByEligibilityStatus(String employeeId, Loan.EligibilityStatus eligibilityStatus) {

        // Verify that the employee exists
        if (!employeeRepository.existsByEmployeeId(employeeId)) {

            throw new EmployeeNotFoundException("Employee with Employee ID: " + employeeId + " does not exist");

        }

        return loanRepository.findByEmployeeEmployeeIdAndEligibilityStatusOrderByApplicationDateDesc(employeeId, eligibilityStatus);

    }


//==================== Update Loan ====================//

    // Update editable information of a DRAFT loan
    @Transactional
    public Loan updateLoan(Long id, Loan updatedLoan) {

        // Find the existing loan
        Loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan with database ID: " + id + " does not exist"));

        // Only DRAFT loans can be changed through normal update
        if (existingLoan.getLoanStatus() != Loan.LoanStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT loans can be updated: " + id);

        }

        // Copy editable fields while protecting system-controlled fields
        BeanUtils.copyProperties(
                updatedLoan,
                existingLoan,
                "id",
                "loanApplicationId",
                "employee",
                "approvedAmount",
                "approvalDate",
                "interestRate",
                "loanStatus",
                "eligibilityStatus",
                "rejectionReason",
                "createdAt",
                "updatedAt"
        );

        return loanRepository.save(existingLoan);

    }


    ///==================== Loan Workflow ====================///

    ///---------- Submit A DRAFT Loan For Review ----------
    @Transactional
    public Loan submitLoan(Long id) {

        Loan existingLoan = loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException("Loan with database ID: " + id + " does not exist"));

        // Only DRAFT loans can be submitted
        if (existingLoan.getLoanStatus() != Loan.LoanStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT loans can be submitted: " + id);

        }

        existingLoan.setLoanStatus(Loan.LoanStatus.SUBMITTED);

        return loanRepository.save(existingLoan);

    }


    ///---------- Move A Submitted Loan Into Review ----------
    @Transactional
    public Loan startLoanReview(Long id) {

        Loan existingLoan = loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException("Loan with database ID: " + id + " does not exist"));

        // Only SUBMITTED loans can enter review
        if (existingLoan.getLoanStatus() != Loan.LoanStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED loans can enter review: " + id);

        }

        existingLoan.setLoanStatus(Loan.LoanStatus.UNDER_REVIEW);

        return loanRepository.save(existingLoan);

    }


    ///---------- Update Eligibility While The Loan Is Under Review ----------
    @Transactional
    public Loan updateEligibilityStatus(Long id, Loan.EligibilityStatus eligibilityStatus) {

        Loan existingLoan = loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException("Loan with database ID: " + id + " does not exist"));

        // Eligibility is determined during loan review
        if (existingLoan.getLoanStatus() != Loan.LoanStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Eligibility can only be updated while the loan is UNDER_REVIEW: " + id);

        }

        // PENDING is the initial system state, not a review result
        if (eligibilityStatus == Loan.EligibilityStatus.PENDING) {
            throw new IllegalArgumentException("Eligibility review must result in ELIGIBLE or NOT_ELIGIBLE");

        }

        existingLoan.setEligibilityStatus(eligibilityStatus);

        return loanRepository.save(existingLoan);

    }


    ///---------- Approve An Eligible Loan That Is Under Review ----------
    @Transactional
    public Loan approveLoan(Long id, BigDecimal approvedAmount, BigDecimal interestRate) {

        Loan existingLoan = loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException("Loan with database ID: " + id + " does not exist"));

        // Only loans under review can be approved
        if (existingLoan.getLoanStatus() != Loan.LoanStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Only UNDER_REVIEW loans can be approved: " + id);

        }

        // Only eligible loans can be approved
        if (existingLoan.getEligibilityStatus() != Loan.EligibilityStatus.ELIGIBLE) {
            throw new IllegalStateException("Only ELIGIBLE loans can be approved: " + id);

        }

        // Approved amount is required
        if (approvedAmount == null) {
            throw new IllegalArgumentException("Approved amount is required");

        }

        // Approved amount must be greater than zero
        if (approvedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Approved amount must be greater than zero");

        }

        // Approved amount cannot exceed the requested amount
        if (approvedAmount.compareTo(existingLoan.getRequestedAmount()) > 0) {
            throw new IllegalArgumentException("Approved amount cannot exceed the requested amount");

        }

        // Interest rate is required during approval
        if (interestRate == null) {
            throw new IllegalArgumentException("Interest rate is required");

        }

        // Interest rate cannot be negative
        if (interestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");

        }

        // Store approval information
        existingLoan.setApprovedAmount(approvedAmount);
        existingLoan.setInterestRate(interestRate);
        existingLoan.setApprovalDate(LocalDate.now());

        // Clear any previous rejection information
        existingLoan.setRejectionReason(null);

        existingLoan.setLoanStatus(Loan.LoanStatus.APPROVED);

        return loanRepository.save(existingLoan);

    }


    ///---------- Reject A Loan That Is Currently Under Review ----------
    @Transactional
    public Loan rejectLoan(Long id, String rejectionReason) {

        Loan existingLoan = loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException("Loan with database ID: " + id + " does not exist"));

        // Only loans under review can be rejected
        if (existingLoan.getLoanStatus() != Loan.LoanStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Only UNDER_REVIEW loans can be rejected: " + id);

        }

        // Rejection reason is required
        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason is required");

        }

        // Clear approval-controlled information
        existingLoan.setApprovedAmount(null);
        existingLoan.setApprovalDate(null);
        existingLoan.setInterestRate(null);

        existingLoan.setRejectionReason(rejectionReason);

        existingLoan.setLoanStatus(Loan.LoanStatus.REJECTED);

        return loanRepository.save(existingLoan);

    }


    ///---------- Cancel A Loan Before It Enters Formal Review ----------
    @Transactional
    public Loan cancelLoan(Long id) {

        Loan existingLoan = loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException("Loan with database ID: " + id + " does not exist"));

        // Only DRAFT or SUBMITTED loans can be canceled here
        if (existingLoan.getLoanStatus() != Loan.LoanStatus.DRAFT && existingLoan.getLoanStatus() != Loan.LoanStatus.SUBMITTED) {
            throw new IllegalStateException("Only DRAFT or SUBMITTED loans can be canceled: " + id);

        }

        existingLoan.setLoanStatus(Loan.LoanStatus.CANCELLED);

        return loanRepository.save(existingLoan);


    }
}