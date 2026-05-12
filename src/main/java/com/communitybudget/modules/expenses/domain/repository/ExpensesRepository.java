package com.communitybudget.modules.expenses.domain.repository;

import com.communitybudget.modules.expenses.domain.model.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpensesRepository {

    Expense save(final Expense expense);

    Optional<Expense> findById(final Long expenseId);

    List<Expense> findByGroupId(final Long groupId);

    void delete(final Expense expense);
}
