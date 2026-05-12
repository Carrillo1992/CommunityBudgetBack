package com.communitybudget.modules.expenses.infrastructure.persistence.entity;

import com.communitybudget.modules.user.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "expense_shares")
@Entity
public class ExpenseShareEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")

    private UserEntity user;
    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "expense_id")
    private ExpenseEntity expense;

    @Column(name = "owed_amount")
    private BigDecimal amount;

}
