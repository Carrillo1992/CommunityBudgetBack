package com.communitybudget.modules.expenses.application.service;


import com.communitybudget.common.exceptions.exception.ExpenseException;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.expenses.application.dto.CreateExpenseRequest;
import com.communitybudget.modules.expenses.application.dto.ExpenseDto;
import com.communitybudget.modules.expenses.application.dto.UpdateExpenseRequest;
import com.communitybudget.modules.expenses.application.mapper.ExpenseMapper;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.service.ExpensesService;
import com.communitybudget.modules.expenses.domain.valueobjects.ExpenseShare;
import com.communitybudget.modules.expenses.infrastructure.persistence.ExpenseReadRepository;
import com.communitybudget.modules.expenses.infrastructure.persistence.entity.ExpenseEntity;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseServiceApplication {

    private final ExpensesService expensesService;
    private final ExpenseReadRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final UserService userService;

    public ExpenseServiceApplication(final ExpensesService expensesService, ExpenseReadRepository expenseRepository, ExpenseMapper expenseMapper, UserService userService) {
        this.expensesService = expensesService;
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.userService = userService;
    }

    @Transactional
    public ExpenseDto createExpense(final CreateExpenseRequest expenseDto , final Long groupId) {
        return Optional.ofNullable(expenseDto)
                .map(request -> expenseMapper.createExpenseToDomain(expenseDto, groupId))
                .map(expensesService::createExpense)
                .flatMap(this::toDto)
                .orElseThrow(() -> new ExpenseException("Expense data cannot be null"));
    }

    @Transactional
    public ExpenseDto updateExpense(final UpdateExpenseRequest expenseRequest, final Long groupId , final Long expenseId) {
        return Optional.ofNullable(expenseRequest)
                .map(request -> expenseMapper.UpdateDtoToDomain(request , groupId, expenseId))
                .map(expensesService::updateExpense)
                .flatMap(this::toDto)
                .orElseThrow(() -> new ExpenseException("Expense data cannot be null"));
    }

    @Transactional
    public void deleteExpense(final Long expenseId, final Long groupId) {
        ExpenseEntity expense = expenseRepository.findById(expenseId);
        if (!expense.getGroup().getId().equals(groupId)) {
            throw new ExpenseException("Expense does not belong to the specified group");
        }
        expensesService.deleteExpense(expenseMapper.entityToDomain(expense));
    }

    @Transactional(readOnly = true)
    public ExpenseDto obtainExpenseById(final Long expenseId, final Long groupId) {
        return Optional.ofNullable(expenseRepository.findById(expenseId))
                .filter(expenseEntity -> expenseEntity.getGroup().getId().equals(groupId))
                .map(expenseMapper::entityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
    }

    @Transactional(readOnly = true)
    public List<ExpenseDto> obtainExpensesOfGroupId(final Long groupId) {
        return Optional.ofNullable(expenseRepository.findAllByGroupId(groupId))
                .map(expenseMapper::entitiesToDtos)
                .orElseThrow(() -> new ResourceNotFoundException("No expenses found for group id: " + groupId));
    }


    private  Optional<ExpenseDto> toDto(final Expense expense) {
        List<Long> userIds = new java.util.ArrayList<>((expense.getShares().stream().map(ExpenseShare::getUserId).toList()));
        if (!userIds.contains(expense.getPaidByUserId())) {
            userIds.add(expense.getPaidByUserId());
        }
        List<User> users = userService.findAllByIds(userIds);
        return Optional.ofNullable(expenseMapper.toDto(expense, users));
    }
}
