package com.communitybudget.modules.expenses.application.mapper;

import com.communitybudget.modules.expenses.application.dto.DebtDto;
import com.communitybudget.modules.expenses.application.dto.SettleUpRequest;
import com.communitybudget.modules.expenses.application.dto.UserBalanceDto;
import com.communitybudget.modules.expenses.application.dto.UserDto;
import com.communitybudget.modules.expenses.domain.model.Expense;
import com.communitybudget.modules.expenses.domain.model.ExpenseShare;
import com.communitybudget.modules.expenses.domain.valueobjects.Debt;
import com.communitybudget.modules.expenses.domain.valueobjects.UserBalance;
import com.communitybudget.modules.user.domain.model.User;
import org.mapstruct.Context;
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


    @Mapping(target = "fromUser", expression = "java(mapUserIdToUserDto(debt.getFromUserId(), users))")
    @Mapping(target = "toUser", expression = "java(mapUserIdToUserDto(debt.getToUserId(), users))")
    DebtDto toDebtDto(final Debt debt, final List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "amount", source = "settleUpRequest.amount")
    @Mapping(target = "description", expression = "java(descriptionFromSettleUpRequest(settleUpRequest, users))")
    @Mapping(target = "paidByUserId", source = "settleUpRequest.payerId")
    @Mapping(target = "shares", expression = "java(debtsShare(settleUpRequest))")
    Expense settleUpToExpense(final SettleUpRequest settleUpRequest, final Long groupId, @Context final List<User> users);
    
    default List<ExpenseShare> debtsShare(final SettleUpRequest settle){
        return Collections.singletonList(ExpenseShare.builder()
                .userId(settle.getReceiverId())
                .amount(BigDecimal.valueOf(settle.getAmount()))
                .build());
    }

    UserDto userToUserDto(final User user);

    default String descriptionFromSettleUpRequest(final SettleUpRequest settleUpRequest, @Context final List<User> users){
        String payerName = String.valueOf(settleUpRequest.getPayerId());
        String receiverName = String.valueOf(settleUpRequest.getReceiverId());

        if (users != null) {
            for (User user : users) {
                if (user.getId().equals(settleUpRequest.getPayerId())) {
                    payerName = user.getName();
                } else if (user.getId().equals(settleUpRequest.getReceiverId())) {
                    receiverName = user.getName();
                }
            }
        }

        return String.format("Budget up: %s pays %s", payerName, receiverName);
    }



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
}