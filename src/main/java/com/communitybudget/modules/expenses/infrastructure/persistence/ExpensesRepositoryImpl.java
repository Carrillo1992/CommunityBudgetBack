package com.communitybudget.modules.expenses.infrastructure.persistence;

import com.communitybudget.modules.expenses.application.mapper.ExpenseMapper;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.repository.ExpensesRepository;
import com.communitybudget.modules.expenses.infrastructure.persistence.common.JpaSpringExpenseRepository;
import com.communitybudget.modules.expenses.infrastructure.persistence.entity.ExpenseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExpensesRepositoryImpl implements ExpensesRepository {

    final JpaSpringExpenseRepository expenseRepository;

    final ExpenseMapper expenseMapper;
    public ExpensesRepositoryImpl(final JpaSpringExpenseRepository jpaSpringExpenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = jpaSpringExpenseRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    public Expense save(final Expense expense) {
        ExpenseEntity entity =  expenseRepository.save(expenseMapper.domainToEntity(expense));
        return expenseMapper.entityToDomain(entity);
    }

    @Override
    public Optional<Expense> findById(final Long expenseId) {
        return expenseRepository.findById(expenseId).map(expenseMapper::entityToDomain);
    }

    @Override
    public List<Expense> findByGroupId(final Long groupId) {
        return expenseRepository.findAll().stream()
                .filter(expenseEntity -> expenseEntity.getGroup().getId().equals(groupId))
                .map(expenseMapper::entityToDomain)
                .toList();
    }

    @Override
    public void delete(final Expense expense) {
        expenseRepository.delete(expenseMapper.domainToEntity(expense));
    }
}
