package com.communitybudget.modules.expenses.domain.valueobjects;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class UserBalance {

    private final Long userId;
    private final BigDecimal totalPaid;
    private final BigDecimal totalOwed;


    public BigDecimal getBalance() {
        return this.totalPaid.subtract(this.totalOwed);
    }

    public Boolean isCreditor() {
        return this.getBalance().compareTo(BigDecimal.ZERO) > 0;
    }

    public Boolean isDebtor() {
        return this.getBalance().compareTo(BigDecimal.ZERO) < 0;
    }
}