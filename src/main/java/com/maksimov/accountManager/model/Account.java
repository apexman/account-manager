package com.maksimov.accountManager.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "account")
public class Account {
    @Id
    @Column(name = "account_id")
    private String id = UUID.randomUUID().toString().replace("-", "");

    @Column(name = "name")
    private String name;

    @Column(name = "balance")
    @Min(0)
    private BigDecimal balance;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fromAccount")
    private Set<AccountTransaction> withdrawalTransactions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "toAccount")
    private Set<AccountTransaction> depositTransactions;

    public Account() {
    }

    public Account(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.valueOf(0)) < 0) {
            this.balance = BigDecimal.ZERO;
        }
        this.balance = balance;
    }

    public Set<AccountTransaction> getWithdrawalTransactions() {
        return withdrawalTransactions;
    }

    public void setWithdrawalTransactions(Set<AccountTransaction> withdrawalTransactions) {
        this.withdrawalTransactions = withdrawalTransactions;
    }

    public Set<AccountTransaction> getDepositTransactions() {
        return depositTransactions;
    }

    public void setDepositTransactions(Set<AccountTransaction> depositTransactions) {
        this.depositTransactions = depositTransactions;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
