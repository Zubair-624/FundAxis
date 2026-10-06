package com.fundaxis.repository;

import com.fundaxis.entity.LoanExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanExtensionRepository extends JpaRepository<LoanExtension, Long> {


    ///==================== Read Loan Extension ====================///

    ///---------- Get All Extension Requests For A Specific Loan ----------
    List<LoanExtension> findByLoanIdOrderByRequestDateDesc(Long loanId);


    ///---------- Get All Extension Requests With A Specific Status ----------
    List<LoanExtension> findByExtensionStatusOrderByRequestDateDesc(LoanExtension.ExtensionStatus extensionStatus);


    ///---------- Get All Extension Requests For An Employee's Loans ----------
    List<LoanExtension> findByLoanEmployeeEmployeeIdOrderByRequestDateDesc(String employeeId);


    ///---------- Get An Employee's Extension Requests With A Specific Status ----------
    List<LoanExtension> findByLoanEmployeeEmployeeIdAndExtensionStatusOrderByRequestDateDesc(String employeeId, LoanExtension.ExtensionStatus extensionStatus);


    ///==================== Check Loan Extension ====================///

    ///---------- Check Whether A Loan Already Has An Extension With The Given Status ----------
    boolean existsByLoanIdAndExtensionStatus(Long loanId, LoanExtension.ExtensionStatus extensionStatus);
}