package com.communitybudget.modules.expenses.domain.service.impl;

import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.repository.ExpensesRepository;
import com.communitybudget.modules.expenses.domain.service.ExpensesService;

import java.util.List;

public class ExpensesServiceImpl implements ExpensesService {

    private final ExpensesRepository expensesRepository;

    public ExpensesServiceImpl(final ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }
    @Override
    public Expense createExpense(final Expense expense) {
        expense.validateMath();
        expense.setIsSettled(false);
        return expensesRepository.save(expense);

    }

    @Override
    public Expense updateExpense(final Expense updateExpense) {
        Expense existingExpense = getExpenseOrThrow(updateExpense.getId());
        updateExpense.validateMath();
        existingExpense.updateData(updateExpense);
        return expensesRepository.save(existingExpense);
    }

    @Override
    public void deleteExpense(final Expense expense) {
        expensesRepository.delete(expense);
    }

    @Override
    public List<Expense> getExpensesForGroup(final Long groupId) {
        return expensesRepository.findByGroupId(groupId);
    }

    @Override
    public Expense getExpenseById(final Long expenseId) {
        return expensesRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + expenseId));
    }

    private Expense getExpenseOrThrow(final Long groupId) {
        return expensesRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("No expenses found for group id: " + groupId));
    }
}
