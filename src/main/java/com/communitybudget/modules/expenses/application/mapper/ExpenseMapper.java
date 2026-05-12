package com.communitybudget.modules.expenses.application.mapper;

import com.communitybudget.modules.expenses.application.dto.CreateExpenseRequest;
import com.communitybudget.modules.expenses.application.dto.ExpenseDto;
import com.communitybudget.modules.expenses.application.dto.UpdateExpenseRequest;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.infrastructure.persistence.entity.ExpenseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {


//    @Mapping(target = "id", source = "expenseDTO.id")

    @Mapping(target = "groupId", source = "groupId")
    Expense toDomain(final CreateExpenseRequest expenseDTO , final Long groupId);

    ExpenseDto toDto(final Expense expense);

    @Mapping(target = "id", source = "expenseId")
    @Mapping(target = "groupId", source = "groupId")
    Expense UpdateDtoToDomain(final UpdateExpenseRequest expenseDTO, final Long groupId, final Long expenseId);

    List<ExpenseDto> entitiesToDtos(final List<ExpenseEntity> expenses);
    ExpenseDto entityToDto(final ExpenseEntity expense);

    ExpenseEntity dtoToEntity(final ExpenseDto expenseDTO);


    ExpenseEntity domainToEntity(final Expense expense);


    Expense entityToDomain(final ExpenseEntity expenseEntity);
}
