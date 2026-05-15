package com.communitybudget.modules.expenses.domain.service.impl;

import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.model.ExpenseShare;
import com.communitybudget.modules.expenses.domain.repository.ExpensesRepository;
import com.communitybudget.modules.expenses.domain.valueobjects.Debt;
import com.communitybudget.modules.expenses.domain.valueobjects.UserBalance;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @Mock
    private ExpensesRepository expensesRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Long groupId = 1L;
    private Group group;

    @BeforeEach
    void setUp() {
        group = Group.builder()
                .id(groupId)
                .members(List.of(
                        GroupMember.builder().userId(1L).build(),
                        GroupMember.builder().userId(2L).build(),
                        GroupMember.builder().userId(3L).build()
                ))
                .build();
    }

    @Test
    void givenExistingGroupWithExpenses_whenCalculateBalancesForGroup_thenReturnsCorrectBalances() {
        // Given
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(group);

        Expense expense1 = Expense.builder()
                .paidByUserId(1L)
                .amount(new BigDecimal("90.00"))
                .shares(List.of(
                        ExpenseShare.builder().userId(1L).amount(new BigDecimal("30.00")).build(),
                        ExpenseShare.builder().userId(2L).amount(new BigDecimal("30.00")).build(),
                        ExpenseShare.builder().userId(3L).amount(new BigDecimal("30.00")).build()
                ))
                .build();

        Expense expense2 = Expense.builder()
                .paidByUserId(2L)
                .amount(new BigDecimal("30.00"))
                .shares(List.of(
                        ExpenseShare.builder().userId(1L).amount(new BigDecimal("10.00")).build(),
                        ExpenseShare.builder().userId(2L).amount(new BigDecimal("10.00")).build(),
                        ExpenseShare.builder().userId(3L).amount(new BigDecimal("10.00")).build()
                ))
                .build();

        when(expensesRepository.findByGroupId(groupId)).thenReturn(List.of(expense1, expense2));

        // When
        List<UserBalance> balances = balanceService.calculateBalancesForGroup(groupId);

        // Then
        assertEquals(3, balances.size());

        UserBalance balance1 = balances.stream().filter(b -> b.getUserId().equals(1L)).findFirst().orElseThrow();
        assertEquals(new BigDecimal("90.00"), balance1.getTotalPaid());
        assertEquals(new BigDecimal("40.00"), balance1.getTotalOwed());
        assertEquals(new BigDecimal("50.00"), balance1.getBalance());

        UserBalance balance2 = balances.stream().filter(b -> b.getUserId().equals(2L)).findFirst().orElseThrow();
        assertEquals(new BigDecimal("30.00"), balance2.getTotalPaid());
        assertEquals(new BigDecimal("40.00"), balance2.getTotalOwed());
        assertEquals(new BigDecimal("-10.00"), balance2.getBalance());

        UserBalance balance3 = balances.stream().filter(b -> b.getUserId().equals(3L)).findFirst().orElseThrow();
        assertEquals(BigDecimal.ZERO, balance3.getTotalPaid());
        assertEquals(new BigDecimal("40.00"), balance3.getTotalOwed());
        assertEquals(new BigDecimal("-40.00"), balance3.getBalance());
    }

    @Test
    void givenNonExistentGroup_whenCalculateBalancesForGroup_thenThrowsResourceNotFoundException() {
        // Given
        when(groupRepository.existsById(groupId)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> balanceService.calculateBalancesForGroup(groupId));
    }

    @Test
    void givenSimpleExpense_whenCalculateDebtsForGroup_thenReturnsCorrectDebts() {
        // Given
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(group);

        Expense expense = Expense.builder()
                .paidByUserId(1L)
                .amount(new BigDecimal("90.00"))
                .shares(List.of(
                        ExpenseShare.builder().userId(1L).amount(new BigDecimal("30.00")).build(),
                        ExpenseShare.builder().userId(2L).amount(new BigDecimal("30.00")).build(),
                        ExpenseShare.builder().userId(3L).amount(new BigDecimal("30.00")).build()
                ))
                .build();

        when(expensesRepository.findByGroupId(groupId)).thenReturn(List.of(expense));

        // When
        List<Debt> debts = balanceService.calculateDebtsForGroup(groupId);

        // Then
        assertEquals(2, debts.size());
        
        boolean debt2to1 = debts.stream().anyMatch(d -> d.getFromUserId().equals(2L) && d.getToUserId().equals(1L) && d.getAmount().compareTo(new BigDecimal("30.00")) == 0);
        boolean debt3to1 = debts.stream().anyMatch(d -> d.getFromUserId().equals(3L) && d.getToUserId().equals(1L) && d.getAmount().compareTo(new BigDecimal("30.00")) == 0);

        assertTrue(debt2to1);
        assertTrue(debt3to1);
    }
    
    @Test
    void givenComplexExpenseScenario_whenCalculateDebtsForGroup_thenReturnsCorrectDebts() {
        // Given
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(group);

        Expense expense1 = Expense.builder()
                .paidByUserId(1L)
                .amount(new BigDecimal("90.00"))
                .shares(List.of(
                        ExpenseShare.builder().userId(1L).amount(new BigDecimal("30.00")).build(),
                        ExpenseShare.builder().userId(2L).amount(new BigDecimal("30.00")).build(),
                        ExpenseShare.builder().userId(3L).amount(new BigDecimal("30.00")).build()
                ))
                .build();

        Expense expense2 = Expense.builder()
                .paidByUserId(2L)
                .amount(new BigDecimal("30.00"))
                .shares(List.of(
                        ExpenseShare.builder().userId(1L).amount(new BigDecimal("10.00")).build(),
                        ExpenseShare.builder().userId(2L).amount(new BigDecimal("10.00")).build(),
                        ExpenseShare.builder().userId(3L).amount(new BigDecimal("10.00")).build()
                ))
                .build();

        when(expensesRepository.findByGroupId(groupId)).thenReturn(List.of(expense1, expense2));
        
        // When
        List<Debt> debts = balanceService.calculateDebtsForGroup(groupId);
        
        // Then
        assertEquals(2, debts.size());
        
        boolean debt3to1 = debts.stream().anyMatch(d -> d.getFromUserId().equals(3L) && d.getToUserId().equals(1L) && d.getAmount().compareTo(new BigDecimal("40.00")) == 0);
        boolean debt2to1 = debts.stream().anyMatch(d -> d.getFromUserId().equals(2L) && d.getToUserId().equals(1L) && d.getAmount().compareTo(new BigDecimal("10.00")) == 0);

        assertTrue(debt3to1);
        assertTrue(debt2to1);
    }

    @Test
    void givenGroupWithNoExpenses_whenCalculateDebtsForGroup_thenReturnsEmptyList() {
        // Given
        when(groupRepository.existsById(groupId)).thenReturn(true);
        when(groupRepository.findById(groupId)).thenReturn(group);
        when(expensesRepository.findByGroupId(groupId)).thenReturn(List.of());

        // When
        List<Debt> debts = balanceService.calculateDebtsForGroup(groupId);

        // Then
        assertTrue(debts.isEmpty());
    }
}