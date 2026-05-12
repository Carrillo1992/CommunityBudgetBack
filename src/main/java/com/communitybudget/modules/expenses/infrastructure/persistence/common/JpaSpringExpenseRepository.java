package com.communitybudget.modules.expenses.infrastructure.persistence.common;

import com.communitybudget.modules.expenses.infrastructure.persistence.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaSpringExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

}
