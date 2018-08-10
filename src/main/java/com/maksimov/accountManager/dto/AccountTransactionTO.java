package com.maksimov.accountManager.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AccountTransactionTO {
    private String id;
    private String fromAccountId;
    private String toAccountId;
    @NotNull
    @Min(0)
    private BigDecimal amount;
}
