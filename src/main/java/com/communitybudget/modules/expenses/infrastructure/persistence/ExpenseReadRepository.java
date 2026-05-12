package com.communitybudget.modules.expenses.infrastructure.persistence;

import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.expenses.infrastructure.persistence.common.JpaSpringExpenseRepository;
import com.communitybudget.modules.expenses.infrastructure.persistence.entity.ExpenseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpenseReadRepository {

    private final JpaSpringExpenseRepository jpaSpringExpenseRepository;

    public ExpenseReadRepository(JpaSpringExpenseRepository jpaSpringExpenseRepository) {
        this.jpaSpringExpenseRepository = jpaSpringExpenseRepository;
    }

    public ExpenseEntity findById(Long id) {
        return jpaSpringExpenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
    }

    public List<ExpenseEntity> findAllByGroupId(Long groupId) {
        return jpaSpringExpenseRepository.findAll().stream()
                .filter(expense -> expense.getGroup().getId().equals(groupId))
                .toList();
    }
}
