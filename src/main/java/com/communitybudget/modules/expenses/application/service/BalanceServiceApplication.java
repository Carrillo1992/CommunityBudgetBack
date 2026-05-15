package com.communitybudget.modules.expenses.application.service;

import com.communitybudget.modules.expenses.application.dto.DebtDto;
import com.communitybudget.modules.expenses.application.dto.SettleUpRequest;
import com.communitybudget.modules.expenses.application.dto.UserBalanceDto;
import com.communitybudget.modules.expenses.application.dto.UserDto;
import com.communitybudget.modules.expenses.application.mapper.BalanceMapper;
import com.communitybudget.modules.expenses.domain.service.BalanceService;
import com.communitybudget.modules.expenses.domain.service.ExpensesService;
import com.communitybudget.modules.user.application.dto.UserDTO;
import com.communitybudget.modules.user.application.service.UserApplicationService;
import com.communitybudget.modules.user.domain.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BalanceServiceApplication {
    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;
    private final ExpensesService expensesService;
    private final UserApplicationService userService;

    public BalanceServiceApplication(final BalanceService balanceService, final BalanceMapper balanceMapper, final ExpensesService expensesService, final UserApplicationService userService) {
        this.balanceService = balanceService;
        this.balanceMapper = balanceMapper;
        this.expensesService = expensesService;
        this.userService = userService;
    }

    public List<UserBalanceDto> obtainBalancesOfGroupId(final Long groupId) {
        return balanceService.calculateBalancesForGroup(groupId)
                .stream()
                .map(balanceMapper::toUserBalanceDto)
                .toList();
    }

    public List<DebtDto> obtainDebtsOfGroupId(final Long groupId) {
        return balanceService.calculateDebtsForGroup(groupId)
                .stream()
                .map(balanceMapper::toDebtDto)
                .toList();
    }

    @Transactional
    public void createSettle(final SettleUpRequest settleUpRequest, final Long groupId) {
        expensesService.createExpense(balanceMapper.settleUpToExpense(settleUpRequest, groupId));
    }

    private UserDTO obtainUserNameById(final Long userId) {
        return userService.getUserById(userId);
    }
}