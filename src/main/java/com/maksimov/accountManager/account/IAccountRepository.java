package com.maksimov.accountManager.account;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface IAccountRepository extends CrudRepository<Account, String> {

}
