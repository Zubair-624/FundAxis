package com.fundaxis.service;

import com.fundaxis.entity.Loan;
import com.fundaxis.entity.LoanExtension;
import com.fundaxis.exception.DuplicateLoanExtensionException;
import com.fundaxis.exception.LoanExtensionNotFoundException;
import com.fundaxis.exception.LoanNotFoundException;
import com.fundaxis.repository.LoanExtensionRepository;
import com.fundaxis.repository.LoanRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanExtensionService {

    private final LoanExtensionRepository loanExtensionRepository;
    private final LoanRepository loanRepository;

    public LoanExtensionService(LoanExtensionRepository loanExtensionRepository, LoanRepository loanRepository) {
        this.loanExtensionRepository = loanExtensionRepository;
        this.loanRepository = loanRepository;

    }


    ///==================== Create Loan Extension ====================///

    ///---------- Create A New Extension Request For A Loan ----------
    @Transactional
    public LoanExtension createExtensionRequest(Long loanId, LoanExtension loanExtension) {

        // Find the loan
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new LoanNotFoundException("Loan with database ID: " + loanId + " does not exist"));

        // Prevent multiple pending extension requests for the same loan
        boolean pendingExtensionExists = loanExtensionRepository.existsByLoanIdAndExtensionStatus(loanId, LoanExtension.ExtensionStatus.PENDING);

        if (pendingExtensionExists) {
            throw new DuplicateLoanExtensionException("A pending extension request already exists for loan ID: " + loanId);

        }

        // Requested end date must be after the original end date
        validateExtensionDates(loanExtension.getOriginalEndDate(), loanExtension.getRequestedEndDate());

        // Connect the extension request with the loan
        loanExtension.setLoan(loan);

        // New extension requests always start as PENDING
        loanExtension.setExtensionStatus(LoanExtension.ExtensionStatus.PENDING);

        // Approval information is controlled by the approval workflow
        loanExtension.setApprovalDate(null);
        loanExtension.setApprovedBy(null);

        // Rejection information is controlled by the rejection workflow
        loanExtension.setRejectionReason(null);

        return loanExtensionRepository.save(loanExtension);

    }


    ///==================== Read Loan Extension ====================///

    ///---------- Get All Extension Requests ----------
    public List<LoanExtension> getAllExtensions() {

        return loanExtensionRepository.findAll(Sort.by(Sort.Direction.DESC, "requestDate"));

    }


    ///---------- Get Extension Request By Database ID (id) ----------
    public LoanExtension getExtensionById(Long id) {

        return loanExtensionRepository.findById(id).orElseThrow(() -> new LoanExtensionNotFoundException("Loan extension with database ID: " + id + " does not exist"));

    }


    ///---------- Get All Extension Requests For A Specific Loan ----------
    public List<LoanExtension> getExtensionsByLoanId(Long loanId) {

        // Confirm the loan exists
        if (!loanRepository.existsById(loanId)) {
            throw new LoanNotFoundException("Loan with database ID: " + loanId + " does not exist");

        }

        return loanExtensionRepository.findByLoanIdOrderByRequestDateDesc(loanId);

    }


    ///---------- Get Extension Requests By Status ----------
    public List<LoanExtension> getExtensionsByStatus(LoanExtension.ExtensionStatus extensionStatus) {

        return loanExtensionRepository.findByExtensionStatusOrderByRequestDateDesc(extensionStatus);

    }


    ///---------- Get All Extension Requests For An Employee's Loans ----------
    public List<LoanExtension> getEmployeeExtensions(String employeeId) {

        return loanExtensionRepository.findByLoanEmployeeEmployeeIdOrderByRequestDateDesc(employeeId);

    }


    ///---------- Get An Employee's Extension Requests By Status ----------
    public List<LoanExtension> getEmployeeExtensionsByStatus(String employeeId, LoanExtension.ExtensionStatus extensionStatus) {

        return loanExtensionRepository.findByLoanEmployeeEmployeeIdAndExtensionStatusOrderByRequestDateDesc(employeeId, extensionStatus);

    }


    ///==================== Update Loan Extension ====================///

    ///---------- Update A Pending Extension Request ----------
    @Transactional
    public LoanExtension updateExtension(Long id, LoanExtension updatedExtension) {

        LoanExtension existingExtension = loanExtensionRepository.findById(id).orElseThrow(() -> new LoanExtensionNotFoundException("Loan extension with database ID: " + id + " does not exist"));

        // Only pending extension requests can be updated
        if (existingExtension.getExtensionStatus() != LoanExtension.ExtensionStatus.PENDING) {
            throw new IllegalStateException("Only PENDING extension requests can be updated: " + id);

        }

        // Requested end date must be after the original end date
        validateExtensionDates(updatedExtension.getOriginalEndDate(), updatedExtension.getRequestedEndDate());

        // Copy editable fields while protecting system-controlled fields
        BeanUtils.copyProperties(
                updatedExtension,
                existingExtension,
                "id",
                "loan",
                "extensionStatus",
                "approvalDate",
                "approvedBy",
                "rejectionReason",
                "createdAt",
                "updatedAt");

        return loanExtensionRepository.save(existingExtension);

    }


    ///==================== Loan Extension Workflow ====================///

    ///---------- Approve A Pending Extension Request ----------
    @Transactional
    public LoanExtension approveExtension(Long id, String approvedBy) {

        LoanExtension extension = loanExtensionRepository.findById(id).orElseThrow(() -> new LoanExtensionNotFoundException("Loan extension with database ID: " + id + " does not exist"));

        // Only pending extension requests can be approved
        if (extension.getExtensionStatus() != LoanExtension.ExtensionStatus.PENDING) {
            throw new IllegalStateException("Only PENDING extension requests can be approved: " + id);

        }

        // Approver information is required
        if (approvedBy == null || approvedBy.isBlank()) {
            throw new IllegalArgumentException("Approved by is required");

        }

        extension.setExtensionStatus(LoanExtension.ExtensionStatus.APPROVED);

        extension.setApprovedBy(approvedBy.trim());

        extension.setApprovalDate(LocalDate.now());

        // Approved extensions must not contain rejection information
        extension.setRejectionReason(null);

        return loanExtensionRepository.save(extension);

    }


    ///---------- Reject A Pending Extension Request ----------
    @Transactional
    public LoanExtension rejectExtension(Long id, String rejectionReason) {

        LoanExtension extension = loanExtensionRepository.findById(id).orElseThrow(() -> new LoanExtensionNotFoundException("Loan extension with database ID: " + id + " does not exist"));

        // Only pending extension requests can be rejected
        if (extension.getExtensionStatus() != LoanExtension.ExtensionStatus.PENDING) {
            throw new IllegalStateException("Only PENDING extension requests can be rejected: " + id);

        }

        // Rejection reason is required
        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason is required");

        }

        extension.setExtensionStatus(LoanExtension.ExtensionStatus.REJECTED);

        extension.setRejectionReason(rejectionReason.trim());

        // Rejected extensions must not contain approval information
        extension.setApprovalDate(null);
        extension.setApprovedBy(null);

        return loanExtensionRepository.save(extension);

    }


    ///---------- Cancel A Pending Extension Request ----------
    @Transactional
    public LoanExtension cancelExtension(Long id) {

        LoanExtension extension = loanExtensionRepository.findById(id).orElseThrow(() -> new LoanExtensionNotFoundException("Loan extension with database ID: " + id + " does not exist"));

        // Only pending extension requests can be cancelled
        if (extension.getExtensionStatus() != LoanExtension.ExtensionStatus.PENDING) {
            throw new IllegalStateException("Only PENDING extension requests can be cancelled: " + id);

        }

        extension.setExtensionStatus(LoanExtension.ExtensionStatus.CANCELLED);

        // Canceled requests must not contain approval information
        extension.setApprovalDate(null);
        extension.setApprovedBy(null);

        // Canceled requests are not rejected requests
        extension.setRejectionReason(null);

        return loanExtensionRepository.save(extension);

    }


    ///==================== Helper Methods ====================///

    ///---------- Validate Extension Date Consistency ----------
    private void validateExtensionDates(LocalDate originalEndDate, LocalDate requestedEndDate) {

        if (originalEndDate == null || requestedEndDate == null) {

            return;

        }

        if (!requestedEndDate.isAfter(originalEndDate)) {
            throw new IllegalArgumentException("Requested end date must be after the original end date");

        }
    }
}