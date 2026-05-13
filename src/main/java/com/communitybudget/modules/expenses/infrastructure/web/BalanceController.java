package com.communitybudget.modules.expenses.infrastructure.web;

import com.communitybudget.modules.expenses.application.dto.DebtDto;
import com.communitybudget.modules.expenses.application.dto.SettleUpRequest;
import com.communitybudget.modules.expenses.application.dto.UserBalanceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
public class BalanceController {

    @GetMapping("/{groupId}/balance")
    public ResponseEntity<UserBalanceDto> getGroupBalance(@PathVariable final Long groupId) {
        return null;
    }

    @GetMapping("/{groupId}/settlements")
    public ResponseEntity<List<DebtDto>> getGroupSettlements(@PathVariable final Long groupId) {
        return null;
    }

    @PostMapping("{groupId}/settlements")
    public ResponseEntity<Void> settleDebt(@PathVariable final Long groupId, @RequestBody final SettleUpRequest settleUpRequest) {
        return null;
    }


}
