package com.communitybudget.modules.expenses.domain.service;

import com.communitybudget.modules.expenses.domain.model.Expense;

import java.util.List;

public interface ExpensesService {

    Expense createExpense(final Expense expense);

    Expense updateExpense(final Expense expense);

    void deleteExpense(final Expense expense);

    List<Expense> getExpensesForGroup(final Long groupId);

    Expense getExpenseById(final Long expenseId);
}
