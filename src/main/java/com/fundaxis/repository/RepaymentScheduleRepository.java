package com.fundaxis.repository;

import com.fundaxis.entity.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, Long> {


    ///==================== Read Repayment Schedule ====================///

    ///---------- Get All Repayment Schedules For A Specific Loan ----------
    List<RepaymentSchedule> findByLoanIdOrderByInstallmentNumberAsc(Long loanId);


    ///---------- Get A Specific Installment For A Specific Loan ----------
    Optional<RepaymentSchedule> findByLoanIdAndInstallmentNumber(Long loanId, Integer installmentNumber);


    ///---------- Get Repayment Schedules With A Specific Installment Status ----------
    List<RepaymentSchedule> findByInstallmentStatusOrderByDueDateAsc(RepaymentSchedule.InstallmentStatus installmentStatus);


    ///---------- Get Repayment Schedules For A Loan With A Specific Status ----------
    List<RepaymentSchedule> findByLoanIdAndInstallmentStatusOrderByInstallmentNumberAsc(Long loanId, RepaymentSchedule.InstallmentStatus installmentStatus);


    ///---------- Get Repayment Schedules With A Specific Due Date ----------
    List<RepaymentSchedule> findByDueDateOrderByInstallmentNumberAsc(LocalDate dueDate);


    ///---------- Get Repayment Schedules Due Before A Specific Date ----------
    List<RepaymentSchedule> findByDueDateBeforeOrderByDueDateAsc(LocalDate date);


    ///---------- Get Overdue Repayment Schedules Before A Specific Date ----------
    List<RepaymentSchedule> findByDueDateBeforeAndInstallmentStatusOrderByDueDateAsc(LocalDate date, RepaymentSchedule.InstallmentStatus installmentStatus);


    ///---------- Get All Repayment Schedules Belonging To An Employee's Loans ----------
    List<RepaymentSchedule> findByLoanEmployeeEmployeeIdOrderByDueDateAsc(String employeeId);


    ///---------- Get An Employee's Repayment Schedules With A Specific Status ----------
    List<RepaymentSchedule> findByLoanEmployeeEmployeeIdAndInstallmentStatusOrderByDueDateAsc(String employeeId, RepaymentSchedule.InstallmentStatus installmentStatus);


    ///==================== Check Repayment Schedule ====================///

    ///---------- Check Whether The Loan Already Has The Given Installment Number ----------
    boolean existsByLoanIdAndInstallmentNumber(Long loanId, Integer installmentNumber);
}