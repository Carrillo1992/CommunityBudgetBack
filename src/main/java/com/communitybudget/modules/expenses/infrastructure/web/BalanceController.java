package com.communitybudget.modules.expenses.infrastructure.web;

import com.communitybudget.modules.expenses.application.dto.DebtDto;
import com.communitybudget.modules.expenses.application.dto.SettleUpRequest;
import com.communitybudget.modules.expenses.application.dto.UserBalanceDto;
import com.communitybudget.modules.expenses.application.service.BalanceServiceApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
public class BalanceController {

    private final BalanceServiceApplication balanceServiceApplication;

    public BalanceController(BalanceServiceApplication balanceServiceApplication) {
        this.balanceServiceApplication = balanceServiceApplication;
    }

    @GetMapping("/{groupId}/balances")
    public ResponseEntity<List<UserBalanceDto>> getGroupBalance(@PathVariable final Long groupId) {
        List<UserBalanceDto> balances = balanceServiceApplication.obtainBalancesOfGroupId(groupId);
        return ResponseEntity.status(200).body(balances);
    }

    @GetMapping("/{groupId}/settlements")
    public ResponseEntity<List<DebtDto>> getGroupSettlements(@PathVariable final Long groupId) {
        List<DebtDto> debts = balanceServiceApplication.obtainDebtsOfGroupId(groupId);
        return ResponseEntity.status(200).body(debts);
    }

    @PostMapping("/{groupId}/settlements")
    public ResponseEntity<Void> settleDebt(@PathVariable final Long groupId, @RequestBody final SettleUpRequest settleUpRequest) {
        balanceServiceApplication.createSettle(settleUpRequest, groupId);
        return ResponseEntity.ok().build();
    }

}
