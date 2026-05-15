package com.communitybudget.modules.expenses.domain.service.impl;

import com.communitybudget.common.exceptions.exception.GroupRequestException;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.model.ExpenseShare;
import com.communitybudget.modules.expenses.domain.repository.ExpensesRepository;
import com.communitybudget.modules.group.domain.model.Group;
import com.communitybudget.modules.group.domain.repository.GroupRepository;
import com.communitybudget.modules.group.domain.valueobjects.GroupMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpensesServiceImplTest {

    @Mock
    private ExpensesRepository expensesRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private ExpensesServiceImpl expensesService;

    private Expense expense;
    private Group group;

    @BeforeEach
    void setUp() {
        group = Group.builder()
                .id(1L)
                .name("Test Group")
                .members(List.of(GroupMember.builder().userId(1L).build(), GroupMember.builder().userId(2L).build()))
                .build();

        expense = Expense.builder()
                .id(1L)
                .groupId(1L)
                .paidByUserId(1L)
                .amount(new BigDecimal("100.00"))
                .shares(new ArrayList<>(List.of(
                        ExpenseShare.builder().userId(1L).amount(new BigDecimal("50.00")).build(),
                        ExpenseShare.builder().userId(2L).amount(new BigDecimal("50.00")).build()
                )))
                .build();
    }

    @Test
    void givenValidExpense_whenCreateExpense_thenExpenseIsSaved() {
        // Given
        when(groupRepository.findById(1L)).thenReturn(group);
        when(expensesRepository.save(any(Expense.class))).thenReturn(expense);

        // When
        Expense createdExpense = expensesService.createExpense(expense);

        // Then
        assertNotNull(createdExpense);
        assertEquals(expense.getAmount(), createdExpense.getAmount());
        verify(expensesRepository, times(1)).save(expense);
    }

    @Test
    void givenPayerNotInGroup_whenCreateExpense_thenThrowsGroupRequestException() {
        // Given
        expense.setPaidByUserId(3L);
        when(groupRepository.findById(1L)).thenReturn(group);

        // When & Then
        assertThrows(GroupRequestException.class, () -> expensesService.createExpense(expense));
    }

    @Test
    void givenSharedUserNotInGroup_whenCreateExpense_thenThrowsGroupRequestException() {
        // Given
        expense.setShares(new ArrayList<>(Collections.singletonList(ExpenseShare.builder().userId(3L).amount(new BigDecimal("100.00")).build())));
        when(groupRepository.findById(1L)).thenReturn(group);

        // When & Then
        assertThrows(GroupRequestException.class, () -> expensesService.createExpense(expense));
    }

    @Test
    void givenValidExpense_whenUpdateExpense_thenExpenseIsUpdated() {
        // Given
        Expense existingExpense = Expense.builder()
                .id(1L)
                .groupId(1L)
                .paidByUserId(1L)
                .amount(new BigDecimal("100.00"))
                .shares(new ArrayList<>(List.of(
                        ExpenseShare.builder().userId(1L).amount(new BigDecimal("50.00")).build(),
                        ExpenseShare.builder().userId(2L).amount(new BigDecimal("50.00")).build()
                )))
                .build();

        Expense requestExpense = Expense.builder()
                .id(1L)
                .groupId(1L)
                .paidByUserId(1L)
                .amount(new BigDecimal("200.00"))
                .description("Updated")
                .shares(new ArrayList<>(List.of(
                        ExpenseShare.builder().userId(1L).amount(new BigDecimal("100.00")).build(),
                        ExpenseShare.builder().userId(2L).amount(new BigDecimal("100.00")).build()
                )))
                .build();

        when(groupRepository.findById(1L)).thenReturn(group);
        when(expensesRepository.findById(1L)).thenReturn(Optional.of(existingExpense));
        when(expensesRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Expense updatedExpense = expensesService.updateExpense(requestExpense);

        // Then
        assertNotNull(updatedExpense);
        assertEquals(new BigDecimal("200.00"), updatedExpense.getAmount());
        assertEquals("Updated", updatedExpense.getDescription());
        verify(expensesRepository, times(1)).save(existingExpense);
    }

    @Test
    void givenExistingExpense_whenDeleteExpense_thenExpenseIsDeleted() {
        // When
        expensesService.deleteExpense(expense);
        
        // Then
        verify(expensesRepository, times(1)).delete(expense);
    }
}