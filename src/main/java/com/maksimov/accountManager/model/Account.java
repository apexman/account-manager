package com.maksimov.accountManager.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "account")
@Data
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

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
