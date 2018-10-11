package com.maksimov.accountManager.repository;

import com.maksimov.accountManager.model.Account;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select acc from Account acc where acc.id = :id")
    Account findOneAndLock(@Param("id") String id);

    List<Account> findAllByClientId(Long clientId);

    List<Account> findAccountsByNameIgnoreCaseContaining(String namePart);
}
