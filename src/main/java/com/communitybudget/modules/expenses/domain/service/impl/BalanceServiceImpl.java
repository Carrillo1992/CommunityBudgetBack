package com.communitybudget.modules.expenses.domain.service.impl;

import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.expenses.domain.valueobjects.Debt;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.valueobjects.UserBalance;
import com.communitybudget.modules.expenses.domain.repository.ExpensesRepository;
import com.communitybudget.modules.expenses.domain.service.BalanceService;
import com.communitybudget.modules.expenses.domain.model.ExpenseShare;
import com.communitybudget.modules.group.domain.model.Group;
import com.communitybudget.modules.group.domain.repository.GroupRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BalanceServiceImpl implements BalanceService {

    private final ExpensesRepository expensesRepository;
    private final GroupRepository groupRepository;

    public BalanceServiceImpl(final ExpensesRepository expensesRepository,final GroupRepository groupRepository) {
        this.expensesRepository = expensesRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public List<UserBalance> calculateBalancesForGroup(final Long groupId) {
        validateGroup(groupId);
        Group group = groupRepository.findById(groupId);
        List<Expense> expenses = expensesRepository.findByGroupId(groupId);

        return group.getMembers().stream().map(member -> {
            Long userId = member.getUserId();

            BigDecimal totalPaid = expenses.stream()
                    .filter(e -> e.getPaidByUserId().equals(userId))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalOwed = expenses.stream()
                    .flatMap(e -> e.getShares().stream())
                    .filter(s -> s.getUserId().equals(userId))
                    .map(ExpenseShare::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return UserBalance.builder()
                    .userId(userId)
                    .totalPaid(totalPaid)
                    .totalOwed(totalOwed)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<Debt> calculateDebtsForGroup(final Long groupId) {
        validateGroup(groupId);
        List<UserBalance> balances = calculateBalancesForGroup(groupId);

        List<UserBalance> debtors = getSortedDebtors(balances);

        List<UserBalance> creditors = getSortedCreditors(balances);

        return resolveDebts(debtors, creditors);
    }

    private static List<Debt> resolveDebts(List<UserBalance> debtors, List<UserBalance> creditors) {
        List<Debt> debts = new ArrayList<>();

        if (debtors.isEmpty() || creditors.isEmpty()) {
            return debts;
        }

        int i = 0, j = 0;

        BigDecimal currentDebt = debtors.get(0).getBalance().abs();
        BigDecimal currentCredit = creditors.get(0).getBalance();

        while (i < debtors.size() && j < creditors.size()) {
            BigDecimal minAmount = currentDebt.min(currentCredit);

            debts.add(createDebt(debtors.get(i).getUserId(), creditors.get(j).getUserId(), minAmount));

            currentDebt = currentDebt.subtract(minAmount);
            currentCredit = currentCredit.subtract(minAmount);

            if (currentDebt.compareTo(BigDecimal.ZERO) == 0) {
                i++;
                if (i < debtors.size()) {
                    currentDebt = debtors.get(i).getBalance().abs();
                }
            }

            if (currentCredit.compareTo(BigDecimal.ZERO) == 0) {
                j++;
                if (j < creditors.size()) {
                    currentCredit = creditors.get(j).getBalance();
                }
            }
        }
        return debts;
    }

    private static Debt createDebt(Long fromUserId, Long toUserId, BigDecimal amount) {
        return Debt.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .amount(amount)
                .build();
    }

    private static  List<UserBalance> getSortedCreditors(List<UserBalance> balances) {
        return balances.stream()
                .filter(UserBalance::isCreditor)
                .sorted(Comparator.comparing(UserBalance::getBalance).reversed())
                .toList();
    }

    private static List<UserBalance> getSortedDebtors(List<UserBalance> balances) {
        return balances.stream()
                .filter(UserBalance::isDebtor)
                .sorted(Comparator.comparing((UserBalance b) -> b.getBalance().abs()).reversed())
                .toList();
    }

    private void validateGroup(final Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group with id " + groupId + " does not exist.");
        }
    }
}