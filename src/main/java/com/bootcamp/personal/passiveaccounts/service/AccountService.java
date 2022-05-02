package com.bootcamp.personal.passiveaccounts.service;

import com.bootcamp.personal.passiveaccounts.entity.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface AccountService {

    public Flux<Account> getAll();

    public Mono<Account> getById(String id);

    public Mono<Account> save(Account account);

    public Mono<Account> update(Account account);

    public Mono<Account> delete(String id);
}