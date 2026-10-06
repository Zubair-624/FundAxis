package com.fundaxis.service;

import com.fundaxis.entity.Contribution;
import com.fundaxis.entity.Employee;
import com.fundaxis.exception.ContributionNotFoundException;
import com.fundaxis.exception.DuplicateContributionException;
import com.fundaxis.exception.EmployeeNotFoundException;
import com.fundaxis.repository.ContributionRepository;
import com.fundaxis.repository.EmployeeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class ContributionService {

    ///---------- Constructor Dependency Injection ----------

    private final ContributionRepository contributionRepository;
    private final EmployeeRepository employeeRepository;

    // ContributionService needs both repositories
    // to communicate with the database
    public ContributionService(ContributionRepository contributionRepository, EmployeeRepository employeeRepository) {
        this.contributionRepository = contributionRepository;
        this.employeeRepository = employeeRepository;

    }


    ///---------- Read ----------///

    // Get all Contributions
    public List<Contribution> getAllContributionsList() {

        return contributionRepository.findAll();
    }


    // Get one Contribution by Contribution ID
    public Contribution getContributionById(Long id) {

        return contributionRepository.findById(id)
                .orElseThrow(() -> new ContributionNotFoundException("Contribution with ID: " + id + " does not exist"));
    }


    // Get all Contributions belonging to an Employee
    // Example: EMP001
    public List<Contribution> getEmployeeContributionsByEmployeeId(String employeeId) {

        return contributionRepository.findByEmployeeEmployeeId(employeeId);
    }


    ///---------- Create ----------///

    @Transactional
    public Contribution createContribution(String employeeId, Contribution contribution) {

        // Find the Employee using the official Employee ID
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with Employee ID: " + employeeId + " does not exist"));


        // Store contribution month using the first day of the month
        contribution.setContributionMonth(contribution.getContributionMonth().withDayOfMonth(1));


        // Prevent more than one contribution for the same employee and month
        if (contributionRepository.existsByEmployeeEmployeeIdAndContributionMonth(employeeId, contribution.getContributionMonth()))
            throw new DuplicateContributionException("Contribution already exists for Employee ID " + employeeId + " for month " + contribution.getContributionMonth());


        // Connect the Employee with the Contribution
        contribution.setEmployee(employee);


        // New contributions always start as pending
        contribution.setContributionStatus(Contribution.ContributionStatus.PENDING);


        // Pending contributions do not have a payment date
        contribution.setPaymentDate(null);

        // Save the Contribution
        return contributionRepository.save(contribution);

    }




    ///========== Mark a contribution as paid / Payment  ==========///
    @Transactional
    public Contribution markContributionAsPaid(Long id, LocalDate paymentDate) {

        // Find the Contribution
        Contribution existingContribution = contributionRepository.findById(id)
                .orElseThrow(() -> new ContributionNotFoundException("Contribution with ID: " + id + " does not exist"));

        // Only pending contributions can be marked as paid
        if (existingContribution.getContributionStatus() != Contribution.ContributionStatus.PENDING) {
            throw new IllegalStateException("Only pending contributions can be marked as paid: " + id);

        }

        // Payment date is required when marking a contribution as paid
        if (paymentDate == null) {
            throw new IllegalArgumentException("Payment date is required");

        }

        // Payment date cannot be in the future
        if (paymentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Payment date cannot be in the future");

        }

        // Change the contribution status from PENDING to PAID
        existingContribution.setContributionStatus(Contribution.ContributionStatus.PAID);

        // Record the date the payment was made
        existingContribution.setPaymentDate(paymentDate);

        // Save the updated contribution to the database and return it
        return contributionRepository.save(existingContribution);

    }


    ///---------- Update ----------///

    @Transactional
    public Contribution updateContribution(Long id, Contribution updatedContribution) {

        // Find the existing Contribution
        Contribution existingContribution = contributionRepository.findById(id)
                        .orElseThrow(() -> new ContributionNotFoundException("Contribution with ID: " + id + " does not exist"));


        // Store contribution month using the first day of the month
        updatedContribution.setContributionMonth(updatedContribution.getContributionMonth().withDayOfMonth(1));


        // Only pending contributions can be updated
        if (existingContribution.getContributionStatus() != Contribution.ContributionStatus.PENDING) {
            throw new IllegalStateException("Only pending contributions can be updated: " + id);

        }

        // Get the Employee ID already connected to this Contribution
        String employeeId = existingContribution.getEmployee().getEmployeeId();

        // Prevent another contribution for the same employee and month
        if (contributionRepository.existsByEmployeeEmployeeIdAndContributionMonthAndIdNot(employeeId, updatedContribution.getContributionMonth(), id)) {
            throw new DuplicateContributionException("Contribution already exists for Employee ID " + employeeId + " for month " + updatedContribution.getContributionMonth());

        }

        // Update editable fields while protecting system-controlled fields
        BeanUtils.copyProperties(
                updatedContribution,
                existingContribution,
                "id",
                "employee",
                "contributionStatus",
                "paymentDate",
                "createdAt",
                "updatedAt"
        );

        return contributionRepository.save(existingContribution);

    }


    ///---------- Cancel Contribution ----------///

    @Transactional
    public Contribution cancelContribution(Long id) {

        // Find the Contribution
        Contribution existingContribution = contributionRepository.findById(id)
                        .orElseThrow(() -> new ContributionNotFoundException("Contribution with ID: " + id + " does not exist"));

        // Only pending contributions can be canceled
        if (existingContribution.getContributionStatus() != Contribution.ContributionStatus.PENDING) {
            throw new IllegalStateException("Only pending contributions can be canceled: " + id);

        }

        // Change the contribution status to CANCELED
        existingContribution.setContributionStatus(Contribution.ContributionStatus.CANCELED);

        // Canceled contributions do not have a payment date
        existingContribution.setPaymentDate(null);

        return contributionRepository.save(existingContribution);

    }

}