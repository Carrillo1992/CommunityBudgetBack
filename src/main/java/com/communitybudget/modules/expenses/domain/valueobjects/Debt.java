package com.communitybudget.modules.expenses.domain.valueobjects;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Debt {

    private Long fromUserId;

    private Long toUserId;

    private BigDecimal amount;

}
