package com.communitybudget.modules.expenses.domain.service.impl;

import com.communitybudget.common.exceptions.exception.GroupRequestException;
import com.communitybudget.common.exceptions.exception.ResourceNotFoundException;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.repository.ExpensesRepository;
import com.communitybudget.modules.expenses.domain.service.ExpensesService;
import com.communitybudget.modules.group.domain.model.Group;
import com.communitybudget.modules.group.domain.repository.GroupRepository;
import com.communitybudget.modules.group.domain.valueobjects.GroupMember;

import java.util.List;

public class ExpensesServiceImpl implements ExpensesService {

    private final ExpensesRepository expensesRepository;
    private final GroupRepository groupRepository;

    public ExpensesServiceImpl(final ExpensesRepository expensesRepository, final GroupRepository groupRepository) {
        this.expensesRepository = expensesRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public Expense createExpense(final Expense expense) {
        validateGroupAndMembers(expense);

        expense.validateMath();
        expense.setIsSettled(false);
        return expensesRepository.save(expense);

    }

    @Override
    public Expense updateExpense(final Expense updateExpense) {
        validateGroupAndMembers(updateExpense);
        Expense existingExpense = getExpenseOrThrow(updateExpense.getId());
        updateExpense.validateMath();
        existingExpense.updateData(updateExpense);
        return expensesRepository.save(existingExpense);
    }

    @Override
    public void deleteExpense(final Expense expense) {
        expensesRepository.delete(expense);
    }

    @Override
    public List<Expense> getExpensesForGroup(final Long groupId) {
        return expensesRepository.findByGroupId(groupId);
    }

    @Override
    public Expense getExpenseById(final Long expenseId) {
        return expensesRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
    }

    private Expense getExpenseOrThrow(final Long groupId) {
        return expensesRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("No expenses found for group id: " + groupId));
    }

    private void validateGroupAndMembers(final Expense expense) {
        Group group = groupRepository.findById(expense.getGroupId());

        List<Long> memberIds = group.getMembers().stream()
                .map(GroupMember::getUserId)
                .toList();

        if (!memberIds.contains(expense.getPaidByUserId())) {
            throw new GroupRequestException("Payer is not a member of the group");
        }

        expense.getShares().forEach(share -> {
            if (!memberIds.contains(share.getUserId())) {
                throw new GroupRequestException("User in shares is not a member of the group: " + share.getUserId());
            }
        });
    }
}
