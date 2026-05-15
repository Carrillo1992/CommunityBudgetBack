package com.communitybudget.modules.expenses.application.mapper;

import com.communitybudget.modules.expenses.application.dto.DebtDto;
import com.communitybudget.modules.expenses.application.dto.SettleUpRequest;
import com.communitybudget.modules.expenses.application.dto.UserBalanceDto;
import com.communitybudget.modules.expenses.application.dto.UserDto;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.model.ExpenseShare;
import com.communitybudget.modules.expenses.domain.valueobjects.Debt;
import com.communitybudget.modules.expenses.domain.valueobjects.UserBalance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", imports = { Collections.class, ExpenseShare.class })
public interface BalanceMapper {

    UserBalance toUserBalance(final UserBalanceDto userBalanceDto);

    @Mapping(target = "netBalance", expression = "java(userBalance.getBalance().doubleValue())")
    @Mapping(target = "user.id", source = "userId")
    UserBalanceDto toUserBalanceDto(final UserBalance userBalance);

    @Mapping(target = "fromUser.id", source = "fromUserId")
    @Mapping(target = "toUser.id", source = "toUserId")
    DebtDto toDebtDto(final Debt debt);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "amount", source = "settleUpRequest.amount")
    @Mapping(target = "description", expression = "java(descriptionFromSettleUpRequest(settleUpRequest))")
    @Mapping(target = "paidByUserId", source = "settleUpRequest.payerId")
    @Mapping(target = "shares", expression = "java(debtsShare(settleUpRequest))")
    Expense settleUpToExpense(final SettleUpRequest settleUpRequest, final Long groupId);
    
    default List<ExpenseShare> debtsShare(final SettleUpRequest settle){
        return Collections.singletonList(ExpenseShare.builder()
                .userId(settle.getReceiverId())
                .amount(BigDecimal.valueOf(settle.getAmount()))
                .build());
    }
    default String descriptionFromSettleUpRequest(final SettleUpRequest settleUpRequest){
        return String.format("Budget up: %s pays %s", settleUpRequest.getPayerId(), settleUpRequest.getReceiverId());
    }
}
