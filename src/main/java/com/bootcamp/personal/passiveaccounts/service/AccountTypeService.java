package com.bootcamp.personal.passiveaccounts.service;

import com.bootcamp.personal.passiveaccounts.entity.AccountType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface AccountTypeService {

    public Flux<AccountType> getAll();

    public Mono<AccountType> getById(String id);

    public Mono<AccountType> save(AccountType accountType);

    public Mono<AccountType> update(AccountType accountType);

    public Mono<AccountType> delete(String id);
}