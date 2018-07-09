package com.maksimov.moneyManager;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "account")
public class Account {
    @Id
    @Column(name = "account_id")
    private String id = UUID.randomUUID().toString().replace("-", "");

    @Column(name = "balance", precision = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", balance=" + balance +
                '}';
    }
}
