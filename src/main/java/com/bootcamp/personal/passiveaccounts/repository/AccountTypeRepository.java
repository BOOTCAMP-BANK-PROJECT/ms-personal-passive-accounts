package com.bootcamp.personal.passiveaccounts.repository;

import com.bootcamp.personal.passiveaccounts.entity.AccountType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTypeRepository extends ReactiveMongoRepository<AccountType, String> {

}