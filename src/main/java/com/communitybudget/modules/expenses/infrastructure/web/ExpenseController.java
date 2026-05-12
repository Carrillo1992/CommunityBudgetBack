package com.communitybudget.modules.expenses.infrastructure.web;

import com.communitybudget.modules.expenses.application.dto.CreateExpenseRequest;
import com.communitybudget.modules.expenses.application.dto.ExpenseDto;
import com.communitybudget.modules.expenses.application.dto.UpdateExpenseRequest;
import com.communitybudget.modules.expenses.application.service.ExpenseServiceApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
public class ExpenseController {

    private final ExpenseServiceApplication expenseServiceApplication;

    public ExpenseController(ExpenseServiceApplication expenseServiceApplication) {
        this.expenseServiceApplication = expenseServiceApplication;
    }

    @PostMapping("/{groupId}/expenses")
    public ResponseEntity<ExpenseDto> createExpense(@PathVariable final Long groupId, @RequestBody final CreateExpenseRequest createExpenseRequest) {
        final ExpenseDto createdExpense = expenseServiceApplication.createExpense(createExpenseRequest , groupId);
        return ResponseEntity.ok(createdExpense);
    }

    @PutMapping("/{groupId}/expenses/{expenseId}")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable final Long groupId, @PathVariable final Long expenseId, @RequestBody final UpdateExpenseRequest updateExpenseRequest) {
        final ExpenseDto updatedExpense = expenseServiceApplication.updateExpense(updateExpenseRequest, groupId , expenseId);
        return ResponseEntity.ok(updatedExpense);
    }

    @DeleteMapping("/{groupId}/expenses/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable final Long groupId, @PathVariable final Long expenseId) {
        expenseServiceApplication.deleteExpense(expenseId, groupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/expenses")
    public ResponseEntity<List<ExpenseDto>> getAllExpenses(@PathVariable final Long groupId) {
        final List<ExpenseDto> expenses = expenseServiceApplication.obtainExpensesOfGroupId(groupId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{groupId}/expenses/{expenseId}")
    public ResponseEntity<ExpenseDto> getExpenseById(@PathVariable final Long expenseId, @PathVariable final Long groupId) {
        final ExpenseDto expense = expenseServiceApplication.obtainExpenseById(expenseId, groupId);
        return ResponseEntity.ok(expense);
    }

}
