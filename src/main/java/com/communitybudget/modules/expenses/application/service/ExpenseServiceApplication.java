package com.communitybudget.modules.expenses.application.service;


import com.communitybudget.modules.expenses.application.dto.CreateExpenseRequest;
import com.communitybudget.modules.expenses.application.dto.ExpenseDto;
import com.communitybudget.modules.expenses.application.dto.UpdateExpenseRequest;
import com.communitybudget.modules.expenses.application.mapper.ExpenseMapper;
import com.communitybudget.modules.expenses.domain.service.ExpensesService;
import com.communitybudget.modules.expenses.infrastructure.persistence.ExpenseReadRepository;
import com.communitybudget.modules.expenses.infrastructure.persistence.entity.ExpenseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseServiceApplication {

    private final ExpensesService expensesService;
    private final ExpenseReadRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseServiceApplication(final ExpensesService expensesService, ExpenseReadRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expensesService = expensesService;
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    @Transactional
    public ExpenseDto createExpense(final CreateExpenseRequest expenseDto , final Long groupId) {
        return Optional.ofNullable(expenseDto)
                .map(request -> expenseMapper.toDomain(expenseDto, groupId))
                .map(expensesService::createExpense)
                .map(expenseMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Expense data cannot be null"));
    }

    @Transactional
    public ExpenseDto updateExpense(final UpdateExpenseRequest expenseRequest, final Long groupId , final Long expenseId) {
        return Optional.ofNullable(expenseRequest)
                .map(request -> expenseMapper.UpdateDtoToDomain(request , groupId, expenseId))
                .map(expensesService::updateExpense)
                .map(expenseMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Expense data cannot be null"));
    }

    @Transactional
    public void deleteExpense(final Long expenseId, final Long groupId) {
        ExpenseEntity expense = expenseRepository.findById(expenseId);
        if (!expense.getGroup().getId().equals(groupId)) {
            throw new IllegalArgumentException("Expense does not belong to the specified group");
        }
        expensesService.deleteExpense(expenseMapper.entityToDomain(expense));
    }

    @Transactional(readOnly = true)
    public ExpenseDto obtainExpenseById(final Long expenseId, final Long groupId) {
        return Optional.ofNullable(expenseRepository.findById(expenseId))
                .filter(expenseEntity -> expenseEntity.getGroup().getId().equals(groupId))
                .map(expenseMapper::entityToDto)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + expenseId));
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto> obtainExpensesOfGroupId(final Long groupId) {
        return Optional.ofNullable(expenseRepository.findAllByGroupId(groupId))
                .map(expenseMapper::entitiesToDtos)
                .orElseThrow(() -> new IllegalArgumentException("No expenses found for group id: " + groupId));
    }





}
