package com.maksimov.moneyManager;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {
    @Query("UPDATE Account a_u\n" +
            "   SET balance = balance + :money\n" +
            "WHERE id = :id")
    void deposit(@Param("id") String id, @Param("money")BigDecimal money);

    @Query("UPDATE Account a_u\n" +
            "   SET balance = balance - :money\n" +
            "WHERE a_u.id = :id AND a_u.balance >= :money")
    void withdraw(@Param("id") String id, @Param("money")BigDecimal money);
}
