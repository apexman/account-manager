package com.maksimov.accountManager.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AccountTO {
    private String id;
    private String name;
    @NotNull
    @Min(0)
    private BigDecimal balance;
}
