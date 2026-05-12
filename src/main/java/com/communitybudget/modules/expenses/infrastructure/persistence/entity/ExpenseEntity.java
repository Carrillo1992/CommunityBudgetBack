package com.communitybudget.modules.expenses.infrastructure.persistence.entity;

import com.communitybudget.modules.group.infrastructure.persistence.entity.GroupEntity;
import com.communitybudget.modules.user.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "expenses")
public class ExpenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id")
    private UserEntity paidBy;

    private String description;

    private BigDecimal amount;

    private LocalDateTime date;

    private String category;

    @Column(name = "is_settlement")
    private Boolean isSettled;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseShareEntity> shares = new ArrayList<>();


}