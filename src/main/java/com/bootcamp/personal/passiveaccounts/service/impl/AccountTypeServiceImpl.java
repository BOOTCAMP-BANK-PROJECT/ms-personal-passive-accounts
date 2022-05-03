package com.bootcamp.personal.passiveaccounts.service.impl;

import com.bootcamp.personal.passiveaccounts.entity.AccountType;
import com.bootcamp.personal.passiveaccounts.repository.AccountTypeRepository;
import com.bootcamp.personal.passiveaccounts.service.AccountTypeService;
import com.bootcamp.personal.passiveaccounts.util.Constant;
import com.bootcamp.personal.passiveaccounts.util.handler.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccountTypeServiceImpl implements AccountTypeService {

    public final AccountTypeRepository repository;

    @Override
    public Flux<AccountType> getAll() {
        return repository.findAll();
    }

    @Override
    public Mono<AccountType> getById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<AccountType> save(AccountType accountType) {
        return repository.findById(accountType.getId())
                .map(sa -> {
                    throw new BadRequestException(
                            "ID",
                            "Client have one ore more accountTypes",
                            sa.getId(),
                            AccountTypeServiceImpl.class,
                            "save.onErrorResume"
                    );
                })
                .switchIfEmpty(Mono.defer(() -> {

                            accountType.setId(null);
                            accountType.setInsertionDate(new Date());
                            accountType.setRegistrationStatus((short) 1);

                            return repository.save(accountType);
                        }
                ))
                .onErrorResume(e -> Mono.error(e)).cast(AccountType.class);
    }

    @Override
    public Mono<AccountType> update(AccountType accountType) {

        return repository.findById(accountType.getId())
                .switchIfEmpty(Mono.error(new Exception("An item with the id " + accountType.getId() + " was not found. >> switchIfEmpty")))
                .flatMap(p -> repository.save(accountType))
                .onErrorResume(e -> Mono.error(new BadRequestException(
                        "ID",
                        "An error occurred while trying to update an item.",
                        e.getMessage(),
                        AccountTypeServiceImpl.class,
                        "update.onErrorResume"
                )));
    }

    @Override
    public Mono<AccountType> delete(String id) {
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
                        AccountTypeServiceImpl.class,
                        "update.onErrorResume"
                )));
    }
}