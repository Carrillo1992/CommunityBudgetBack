package com.communitybudget.modules.expenses.application.mapper;

import com.communitybudget.modules.expenses.application.dto.CreateExpenseRequest;
import com.communitybudget.modules.expenses.application.dto.ExpenseDto;
import com.communitybudget.modules.expenses.application.dto.ExpenseSplitDto;
import com.communitybudget.modules.expenses.application.dto.UpdateExpenseRequest;
import com.communitybudget.modules.expenses.application.dto.UserDto;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.valueobjects.ExpenseShare;
import com.communitybudget.modules.expenses.infrastructure.persistence.entity.ExpenseEntity;
import com.communitybudget.modules.expenses.infrastructure.persistence.entity.ExpenseShareEntity;
import com.communitybudget.modules.user.domain.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "amount", source = "expenseDTO.amount")
    @Mapping(target = "description", source = "expenseDTO.description")
    @Mapping(target = "dateTime", source = "expenseDTO.date")
    @Mapping(target = "paidByUserId", source = "expenseDTO.paidBy")
    @Mapping(target = "shares", source = "expenseDTO.splits")
    Expense createExpenseToDomain(final CreateExpenseRequest expenseDTO, final Long groupId);

    @Mapping(target = "id", source = "expense.id")
    @Mapping(target = "paidBy", source = "expense.paidByUserId")
    @Mapping(target = "amount", source = "expense.amount")
    @Mapping(target = "description", source = "expense.description")
    @Mapping(target = "date", source = "expense.dateTime")
    @Mapping(target = "splits", source = "expense.shares")
    ExpenseDto toDto(final Expense expense, @Context final List<User> users);

    @Mapping(target = "user", source = "share.userId")
    @Mapping(target = "amount", source = "share.amount")
    ExpenseSplitDto shareToSplitDto(final ExpenseShare share, @Context final List<User> users);

    default UserDto mapUserIdToUserDto(final Long userId, @Context final List<User> users) {
        if (userId == null || users == null) {
            return null;
        }
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .map(this::userToUserDto)
                .orElse(null);
    }

    UserDto userToUserDto(User user);

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "id", source = "expenseId")
    @Mapping(target = "amount", source = "expenseDTO.amount")
    @Mapping(target = "description", source = "expenseDTO.description")
    @Mapping(target = "dateTime", source = "expenseDTO.date")
    @Mapping(target = "paidByUserId", source = "expenseDTO.paidBy")
    @Mapping(target = "shares", source = "expenseDTO.splits")
    Expense UpdateDtoToDomain(final UpdateExpenseRequest expenseDTO, final Long groupId, final Long expenseId);

    List<ExpenseDto> entitiesToDtos(final List<ExpenseEntity> expenses);

    @Mapping(target = "splits", source = "shares")
    ExpenseDto entityToDto(final ExpenseEntity expense);

    @Mapping(target = "date", source = "dateTime")
    @Mapping(target = "group.id", source = "groupId")
    @Mapping(target = "paidBy.id", source = "paidByUserId")
    ExpenseEntity domainToEntity(final Expense expense);

    @AfterMapping
    default void linkExpenseShares(@MappingTarget ExpenseEntity entity) {
        if (entity != null && entity.getShares() != null) {
            entity.getShares().forEach(share -> share.setExpense(entity));
        }
    }

    @Mapping(target = "dateTime", source = "date")
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "paidByUserId", source = "paidBy.id")
    Expense entityToDomain(final ExpenseEntity expenseEntity);

    @Mapping(target = "user.id", source = "userId")
    ExpenseShareEntity shareDomainToEntity(final ExpenseShare expenseShare);

    @Mapping(target = "userId", source = "user.id")
    ExpenseShare shareEntityToDomain(final ExpenseShareEntity expenseShareEntity);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "amount", source = "amount")
    ExpenseSplitDto shareEntityToSplitDto(final ExpenseShareEntity shareEntity);

    default List<ExpenseShare> splitDtoListToDomainList(final List<ExpenseSplitDto> splitDtos) {
        if (splitDtos == null) {
            return null;
        }
        return splitDtos.stream().map(this::splitDtoToDomain).toList();
    }

    default ExpenseShare splitDtoToDomain(final ExpenseSplitDto splitDto) {
        if (splitDto == null || splitDto.getUser() == null || splitDto.getUser().getId() == null) {
            return null;
        }
        return ExpenseShare.builder().userId(splitDto.getUser().getId()).amount(BigDecimal.valueOf(splitDto.getAmount())).build();
    }
}