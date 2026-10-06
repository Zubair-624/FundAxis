package com.fundaxis.dto;

import com.fundaxis.entity.Loan;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanEligibilityRequest {

    @NotNull(message = "Eligibility status is required")
    private Loan.EligibilityStatus eligibilityStatus;

}