package com.communitybudget.modules.expenses.application.service;

import com.communitybudget.modules.expenses.application.dto.DebtDto;
import com.communitybudget.modules.expenses.application.dto.ExpenseDto;
import com.communitybudget.modules.expenses.application.dto.SettleUpRequest;
import com.communitybudget.modules.expenses.application.dto.UserBalanceDto;
import com.communitybudget.modules.expenses.application.mapper.BalanceMapper;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.model.ExpenseShare;
import com.communitybudget.modules.expenses.domain.service.BalanceService;
import com.communitybudget.modules.expenses.domain.service.ExpensesService;
import com.communitybudget.modules.expenses.domain.valueobjects.Debt;
import com.communitybudget.modules.user.domain.model.User;
import com.communitybudget.modules.user.domain.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BalanceServiceApplication {
    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;
    private final ExpensesService expensesService;
    private final UserService userService;

    public BalanceServiceApplication(final BalanceService balanceService, final BalanceMapper balanceMapper, final ExpensesService expensesService,final UserService userService) {
        this.balanceService = balanceService;
        this.balanceMapper = balanceMapper;
        this.expensesService = expensesService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<UserBalanceDto> obtainBalancesOfGroupId(final Long groupId) {
        return balanceService.calculateBalancesForGroup(groupId)
                .stream()
                .map(balanceMapper::toUserBalanceDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DebtDto> obtainDebtsOfGroupId(final Long groupId) {
        return balanceService.calculateDebtsForGroup(groupId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void createSettle(final SettleUpRequest settleUpRequest, final Long groupId) {
        List<User> users = userService.findAllByIds(List.of(settleUpRequest.getPayerId(), settleUpRequest.getReceiverId()));
        expensesService.createExpense(balanceMapper.settleUpToExpense(settleUpRequest, groupId, users));
    }

    private DebtDto toDto(final Debt debt) {
        List<Long> userIds = List.of(debt.getFromUserId(), debt.getToUserId());
        List<User> users = userService.findAllByIds(userIds);
        return balanceMapper.toDebtDto(debt, users);
    }

}