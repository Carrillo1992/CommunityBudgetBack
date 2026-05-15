package com.communitybudget.modules.expenses.application.service;

import com.communitybudget.common.exceptions.exception.ExpenseException;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.expenses.application.dto.CreateExpenseRequest;
import com.communitybudget.modules.expenses.application.dto.ExpenseDto;
import com.communitybudget.modules.expenses.application.dto.UpdateExpenseRequest;
import com.communitybudget.modules.expenses.application.mapper.ExpenseMapper;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.model.ExpenseShare;
import com.communitybudget.modules.expenses.domain.service.ExpensesService;
import com.communitybudget.modules.expenses.infrastructure.persistence.ExpenseReadRepository;
import com.communitybudget.modules.expenses.infrastructure.persistence.entity.ExpenseEntity;
import com.communitybudget.modules.group.infrastructure.persistence.entity.GroupEntity;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceApplicationTest {

    @Mock
    private ExpensesService expensesService;

    @Mock
    private ExpenseReadRepository expenseRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseServiceApplication expenseServiceApplication;

    private CreateExpenseRequest createRequest;
    private UpdateExpenseRequest updateRequest;
    private Expense expense;
    private ExpenseDto expenseDto;
    private ExpenseEntity expenseEntity;
    private Long groupId = 1L;
    private Long expenseId = 1L;

    @BeforeEach
    void setUp() {
        createRequest = new CreateExpenseRequest();
        updateRequest = new UpdateExpenseRequest();
        
        expense = Expense.builder()
                .id(expenseId)
                .groupId(groupId)
                .paidByUserId(1L)
                .amount(new BigDecimal("100"))
                .shares(List.of(ExpenseShare.builder().userId(1L).amount(new BigDecimal("50")).build(),
                        ExpenseShare.builder().userId(2L).amount(new BigDecimal("50")).build()))
                .build();
                
        expenseDto = new ExpenseDto();
        
        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setId(groupId);
        
        expenseEntity = new ExpenseEntity();
        expenseEntity.setId(expenseId);
        expenseEntity.setGroup(groupEntity);
    }

    @Test
    void givenValidRequest_whenCreateExpense_thenExpenseDtoIsReturned() {
        // Given
        when(expenseMapper.createExpenseToDomain(any(), eq(groupId))).thenReturn(expense);
        when(expensesService.createExpense(any())).thenReturn(expense);
        when(userService.findAllByIds(anyList())).thenReturn(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()));
        when(expenseMapper.toDto(any(), anyList())).thenReturn(expenseDto);

        // When
        ExpenseDto result = expenseServiceApplication.createExpense(createRequest, groupId);

        // Then
        assertNotNull(result);
        verify(expensesService).createExpense(expense);
    }

    @Test
    void givenNullRequest_whenCreateExpense_thenThrowsExpenseException() {
        // Given
        CreateExpenseRequest nullRequest = null;
        
        // When & Then
        assertThrows(ExpenseException.class, () -> expenseServiceApplication.createExpense(nullRequest, groupId));
    }

    @Test
    void givenValidRequest_whenUpdateExpense_thenExpenseDtoIsReturned() {
        // Given
        when(expenseMapper.UpdateDtoToDomain(any(), eq(groupId), eq(expenseId))).thenReturn(expense);
        when(expensesService.updateExpense(any())).thenReturn(expense);
        when(userService.findAllByIds(anyList())).thenReturn(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()));
        when(expenseMapper.toDto(any(), anyList())).thenReturn(expenseDto);

        // When
        ExpenseDto result = expenseServiceApplication.updateExpense(updateRequest, groupId, expenseId);

        // Then
        assertNotNull(result);
        verify(expensesService).updateExpense(expense);
    }
    
    @Test
    void givenNullRequest_whenUpdateExpense_thenThrowsExpenseException() {
        // Given
        UpdateExpenseRequest nullRequest = null;
        
        // When & Then
        assertThrows(ExpenseException.class, () -> expenseServiceApplication.updateExpense(nullRequest, groupId, expenseId));
    }

    @Test
    void givenExistingExpense_whenDeleteExpense_thenExpenseIsDeleted() {
        // Given
        when(expenseRepository.findById(expenseId)).thenReturn(expenseEntity);
        when(expenseMapper.entityToDomain(any())).thenReturn(expense);

        // When
        expenseServiceApplication.deleteExpense(expenseId, groupId);

        // Then
        verify(expensesService).deleteExpense(expense);
    }

    @Test
    void givenExpenseInDifferentGroup_whenDeleteExpense_thenThrowsExpenseException() {
        // Given
        when(expenseRepository.findById(expenseId)).thenReturn(expenseEntity);
        Long differentGroupId = 2L;
        
        // When & Then
        assertThrows(ExpenseException.class, () -> expenseServiceApplication.deleteExpense(expenseId, differentGroupId));
    }

    @Test
    void givenExistingExpenseId_whenObtainExpenseById_thenExpenseDtoIsReturned() {
        // Given
        when(expenseRepository.findById(expenseId)).thenReturn(expenseEntity);
        when(expenseMapper.entityToDto(expenseEntity)).thenReturn(expenseDto);

        // When
        ExpenseDto result = expenseServiceApplication.obtainExpenseById(expenseId, groupId);

        // Then
        assertNotNull(result);
        assertEquals(expenseDto, result);
    }

    @Test
    void givenExpenseInDifferentGroup_whenObtainExpenseById_thenThrowsResourceNotFoundException() {
        // Given
        when(expenseRepository.findById(expenseId)).thenReturn(expenseEntity);
        Long differentGroupId = 2L;

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> expenseServiceApplication.obtainExpenseById(expenseId, differentGroupId));
    }

    @Test
    void givenExistingGroup_whenObtainExpensesOfGroupId_thenListOfExpenseDtoIsReturned() {
        // Given
        when(expenseRepository.findAllByGroupId(groupId)).thenReturn(List.of(expenseEntity));
        when(expenseMapper.entitiesToDtos(anyList())).thenReturn(List.of(expenseDto));

        // When
        List<ExpenseDto> result = expenseServiceApplication.obtainExpensesOfGroupId(groupId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}