package com.communitybudget.modules.expenses.domain.model;

import com.communitybudget.common.exceptions.exception.BadRequestException;
import com.communitybudget.common.exceptions.exception.ExpenseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {

    private Expense.ExpenseBuilder expenseBuilder;

    @BeforeEach
    void setUp() {
        expenseBuilder = Expense.builder()
                .amount(new BigDecimal("100.00"))
                .shares(new ArrayList<>());
    }

    @Test
    void givenValidAmount_whenAddShare_thenShareIsAdded() {
        // Given
        Expense expense = expenseBuilder.build();
        
        // When
        expense.addShare(new BigDecimal("50.00"), 1L);
        
        // Then
        assertEquals(1, expense.getShares().size());
        assertEquals(new BigDecimal("50.00"), expense.getShares().get(0).getAmount());
    }

    @Test
    void givenZeroOrNegativeAmount_whenAddShare_thenThrowsIllegalArgumentException() {
        // Given
        Expense expense = expenseBuilder.build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> expense.addShare(BigDecimal.ZERO, 1L));
        assertThrows(IllegalArgumentException.class, () -> expense.addShare(new BigDecimal("-10.00"), 1L));
    }

    @Test
    void givenSharesEqualToAmount_whenValidateMath_thenDoesNotThrow() {
        // Given
        Expense expense = expenseBuilder.build();
        expense.addShare(new BigDecimal("50.00"), 1L);
        expense.addShare(new BigDecimal("50.00"), 2L);
        
        // When & Then
        assertDoesNotThrow(expense::validateMath);
    }

    @Test
    void givenSharesNotEqualToAmount_whenValidateMath_thenThrowsExpenseException() {
        // Given
        Expense expense = expenseBuilder.build();
        expense.addShare(new BigDecimal("50.00"), 1L);
        expense.addShare(new BigDecimal("40.00"), 2L);
        
        // When & Then
        assertThrows(BadRequestException.class, expense::validateMath);
    }

    @Test
    void givenNoShares_whenValidateMath_thenDoesNotThrow() {
        // Given
        Expense expense = expenseBuilder.build();
        
        // When & Then
        assertDoesNotThrow(expense::validateMath);
    }
}