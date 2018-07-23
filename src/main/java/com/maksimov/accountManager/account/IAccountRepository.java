package com.maksimov.accountManager.account;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface IAccountRepository extends CrudRepository<Account, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select acc from Account acc where acc.id = :id")
    Account findOneAndLock(@Param("id") String id);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("select acc from Account acc where acc.id = :id1 or acc.id = :id2")
//    void findTwoAndLock(@Param("id1") String id1, @Param("id2") String id2);
}
