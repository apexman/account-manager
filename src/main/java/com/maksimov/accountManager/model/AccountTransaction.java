package com.maksimov.accountManager.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@Data
public class AccountTransaction {
    @Id
    @Column(name = "transaction_id")
    private String id = UUID.randomUUID().toString().replace("-", "");

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Column
    private BigDecimal amount;

    public AccountTransaction() {
    }

    public AccountTransaction(Account fromAccount, Account toAccount, BigDecimal amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "AccountTransaction{" +
                "id='" + id + '\'' +
                ", fromAccount=" + fromAccount.getId() +
                ", toAccount=" + toAccount.getId() +
                ", amount=" + amount +
                '}';
    }
}
