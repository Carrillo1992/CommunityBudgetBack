package com.communitybudget.modules.expenses.application.mapper;

import com.communitybudget.modules.expenses.application.dto.CreateExpenseRequest;
import com.communitybudget.modules.expenses.application.dto.ExpenseDto;
import com.communitybudget.modules.expenses.application.dto.ExpenseSplitDto;
import com.communitybudget.modules.expenses.application.dto.UpdateExpenseRequest;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.valueobjects.ExpenseShare;
import com.communitybudget.modules.expenses.infrastructure.persistence.entity.ExpenseEntity;
import com.communitybudget.modules.expenses.infrastructure.persistence.entity.ExpenseShareEntity;
import org.mapstruct.AfterMapping;
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

    ExpenseDto toDto(final Expense expense);

    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "id", source = "expenseId")
    @Mapping(target = "amount", source = "expenseDTO.amount")
    @Mapping(target = "description", source = "expenseDTO.description")
    @Mapping(target = "dateTime", source = "expenseDTO.date")
    @Mapping(target = "paidByUserId", source = "expenseDTO.paidBy")
    @Mapping(target = "shares", source = "expenseDTO.splits")
    Expense UpdateDtoToDomain(final UpdateExpenseRequest expenseDTO, final Long groupId, final Long expenseId);

    List<ExpenseDto> entitiesToDtos(final List<ExpenseEntity> expenses);
    ExpenseDto entityToDto(final ExpenseEntity expense);

    ExpenseEntity dtoToEntity(final ExpenseDto expenseDTO);

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
    ExpenseShareEntity shareDomainToEntity(ExpenseShare expenseShare);

    @Mapping(target = "userId", source = "user.id")
    ExpenseShare shareEntityToDomain(ExpenseShareEntity expenseShareEntity);

    default List<ExpenseShare> splitDtoListToDomainList(final List<ExpenseSplitDto> splitDtos) {
        if (splitDtos == null) {
            return null;
        }
        return splitDtos.stream()
                .map(this::splitDtoToDomain)
                .toList();
    }

    default ExpenseShare splitDtoToDomain(final ExpenseSplitDto splitDto) {
        if (splitDto == null || splitDto.getUser() == null || splitDto.getUser().getId() == null) {
            return null;
        }
        return ExpenseShare.builder()
                .userId(splitDto.getUser().getId())
                .amount(BigDecimal.valueOf(splitDto.getAmount()))
                .build();
    }
}
