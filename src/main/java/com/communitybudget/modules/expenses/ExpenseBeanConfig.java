package com.communitybudget.modules.expenses;

import com.communitybudget.modules.expenses.domain.service.ExpensesService;
import com.communitybudget.modules.expenses.domain.service.impl.ExpensesServiceImpl;
import com.communitybudget.modules.expenses.infrastructure.persistence.ExpensesRepositoryImpl;
import com.communitybudget.modules.group.infrastructure.persistence.GroupRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpenseBeanConfig {

    @Bean
    public ExpensesService expenseService(final ExpensesRepositoryImpl expenseRepository, final GroupRepositoryImpl groupRepository) {
        return new ExpensesServiceImpl(expenseRepository, groupRepository);
    }
}
