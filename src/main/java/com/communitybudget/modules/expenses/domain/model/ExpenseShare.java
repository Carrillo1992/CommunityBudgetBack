package com.communitybudget.modules.expenses.domain.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ExpenseShare {

    private final Long id;
    private BigDecimal amount;
    private Long userId;
}
