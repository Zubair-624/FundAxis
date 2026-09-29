package com.fundaxis.service;

import com.fundaxis.entity.Contribution;
import com.fundaxis.entity.Employee;
import com.fundaxis.repository.ContributionRepository;
import com.fundaxis.repository.EmployeeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ContributionService {

    ///---------- Constructor Dependency Injection ----------

    private final ContributionRepository contributionRepository;
    private final EmployeeRepository employeeRepository;

    // ContributionService needs ContributionRepository to communicate with the database
    public ContributionService(ContributionRepository contributionRepository, EmployeeRepository employeeRepository){
        this.contributionRepository = contributionRepository;
        this.employeeRepository = employeeRepository;
    }



    ///---------- Read ----------

    // Get all Contributions List
    public List<Contribution> getAllContributionsList(){

        return contributionRepository.findAll();
    }

    // Get contribution by -> id
    public Optional<Contribution> getContributionById(Long id){

        return contributionRepository.findById(id);
    }

    // Get contribution by -> employeeId
    public Optional<Contribution> getEmployeeContributionsByEmployeeId(String employeeId) {

        return contributionRepository.findByEmployeeId(employeeId);
    }


    ///---------- Create ----------

    @Transactional
    public Contribution createContribution(String employeeId, Contribution contribution){

        // Find the employee using official Employee ID
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee ID is not found " + employeeId));


        // Connect the Employee with the Contribution
        contribution.setEmployee(employee);

        // Save Contribution
        return contributionRepository.save(contribution);

    }



    ///---------- Update ----------

    @Transactional
    public Contribution updateContributionByEmployeeId(String EmployeeId, Contribution updatedContribution) {

        // Find existing Contribution
        Contribution existingContribution = contributionRepository.findByEmployeeId(EmployeeId)
                        .orElseThrow(() -> new RuntimeException("Contribution not found: " + EmployeeId));

        // Dynamically update editable fields
        //
        // Excluded fields:
        // id          -> Database primary key
        // employee    -> Employee relationship should not change here
        // createdAt   -> Original creation time
        // updatedAt   -> Managed automatically by Hibernate

        BeanUtils.copyProperties(
                updatedContribution,
                existingContribution,
                "id",
                "employee",
                "createdAt",
                "updatedAt"
        );

        // Save updated Contribution
        return contributionRepository.save(existingContribution);
    }



    ///---------- Cancel Contribution ----------///

    @Transactional
    public Contribution cancelContribution(String employeeId) {

        // Find existing Contribution
        Contribution existingContribution = contributionRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() -> new RuntimeException("Contribution not found: " + employeeId));

        // Mark Contribution as CANCELLED
        // We do not physically delete the financial record.
        existingContribution.setContributionStatus(Contribution.ContributionStatus.CANCELED);

        // Save the canceled Contribution
        return contributionRepository.save(existingContribution);
    }



}
