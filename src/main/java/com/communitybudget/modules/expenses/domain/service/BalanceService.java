package com.communitybudget.modules.expenses.domain.service;

import com.communitybudget.modules.expenses.domain.valueobjects.Debt;
import com.communitybudget.modules.expenses.domain.valueobjects.UserBalance;

import java.util.List;

public interface BalanceService {

    List<UserBalance> calculateBalancesForGroup(final Long groupId);


    List<Debt> calculateDebtsForGroup(final Long groupId);


}
