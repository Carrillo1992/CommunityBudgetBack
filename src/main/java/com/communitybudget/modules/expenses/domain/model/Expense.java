package com.communitybudget.modules.expenses.domain.model;

import com.communitybudget.common.exceptions.exception.BadRequestException;
import com.communitybudget.modules.expenses.domain.valueobjects.Category;
import com.communitybudget.modules.expenses.domain.valueobjects.ExpenseShare;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Expense {

    private final Long id;
    private String description;
    private BigDecimal amount;
    private Long groupId;
    private Long paidByUserId;
    private LocalDateTime dateTime;
    private Category category;
    private Boolean isSettled;

    @Builder.Default
    private List<ExpenseShare> shares = new ArrayList<>();


    public void addShare(final BigDecimal owedAmount, final Long userId) {
        if (owedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Owed amount must be greater than zero");
        }
        shares.add(ExpenseShare.builder().amount(owedAmount).userId(userId).build());
    }

    public void validateMath() {
        BigDecimal totalShares = shares.stream()
                .map(ExpenseShare::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalShares.compareTo(this.amount) != 0) {
            throw new BadRequestException("Total shares must equal the total expense amount");
        }
    }

    public void updateData(final Expense updatedExpense) {
        this.description = updatedExpense.getDescription() == null ? this.description : updatedExpense.getDescription();
        this.amount = updatedExpense.getAmount() == null ? this.amount : updatedExpense.getAmount();
        this.groupId = updatedExpense.getGroupId() == null ? this.groupId : updatedExpense.getGroupId();
        this.paidByUserId = updatedExpense.getPaidByUserId() == null ? this.paidByUserId : updatedExpense.getPaidByUserId();
        if (updatedExpense.getShares() != null && !updatedExpense.getShares().isEmpty()) {
            this.shares.clear();
            this.shares.addAll(updatedExpense.getShares());
        }
        this.category = updatedExpense.getCategory() == null ? this.category : updatedExpense.getCategory();
        this.dateTime = updatedExpense.getDateTime() == null ? this.dateTime : updatedExpense.getDateTime();
        this.isSettled = updatedExpense.getIsSettled() == null ? this.isSettled : updatedExpense.getIsSettled();
    }
}
