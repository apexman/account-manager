package com.maksimov.accountManager.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transaction")
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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
