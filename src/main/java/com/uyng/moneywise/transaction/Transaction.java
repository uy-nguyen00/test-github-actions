package com.uyng.moneywise.transaction;

import com.uyng.moneywise.category.Category;
import com.uyng.moneywise.common.entity.BaseEntityWithUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction extends BaseEntityWithUser {

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate date;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "transaction_links",
            joinColumns = @JoinColumn(name = "source_transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "linked_transaction_id")
    )
    @Builder.Default
    private Set<Transaction> linkedTransactions = new HashSet<>();
}
