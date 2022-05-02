package com.bootcamp.personal.passiveaccounts.service.impl;


import com.bootcamp.personal.passiveaccounts.entity.Account;
import com.bootcamp.personal.passiveaccounts.repository.AccountRepository;
import com.bootcamp.personal.passiveaccounts.service.AccountService;
import com.bootcamp.personal.passiveaccounts.util.Constant;
import com.bootcamp.personal.passiveaccounts.util.handler.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    public final AccountRepository repository;

    @Override
    public Flux<Account> getAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Account> getById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Account> save(Account account) {
        return repository.findByIdClient(account.getIdClient())
                .map(sa -> {
                    throw new BadRequestException(
                            "ID",
                            "Client have one ore more accounts",
                            sa.getId(),
                            AccountServiceImpl.class,
                            "save.onErrorResume"
                    );
                })
                .switchIfEmpty(Mono.defer(() -> {
                            account.setId(null);
                            account.setInsertionDate(new Date());
                            return repository.save(account);
                        }
                ))
                .onErrorResume(e -> Mono.error(e)).cast(Account.class);
    }

    @Override
    public Mono<Account> update(Account account) {

        return repository.findById(account.getId())
                .switchIfEmpty(Mono.error(new Exception("An item with the id " + account.getId() + " was not found. >> switchIfEmpty")))
                .flatMap(p -> repository.save(account))
                .onErrorResume(e -> Mono.error(new BadRequestException(
                        "ID",
                        "An error occurred while trying to update an item.",
                        e.getMessage(),
                        AccountServiceImpl.class,
                        "update.onErrorResume"
                )));
    }

    @Override
    public Mono<Account> delete(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new Exception("An item with the id " + id + " was not found. >> switchIfEmpty")))
                .flatMap(p -> {
                    p.setRegistrationStatus(Constant.STATUS_INACTIVE);
                    return repository.save(p);
                })
                .onErrorResume(e -> Mono.error(new BadRequestException(
                        "ID",
                        "An error occurred while trying to delete an item.",
                        e.getMessage(),
                        AccountServiceImpl.class,
                        "update.onErrorResume"
                )));
    }
}