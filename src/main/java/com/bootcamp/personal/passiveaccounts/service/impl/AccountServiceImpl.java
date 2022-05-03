package com.bootcamp.personal.passiveaccounts.service.impl;


import com.bootcamp.personal.passiveaccounts.MsPersonalPassiveAccountsApplication;
import com.bootcamp.personal.passiveaccounts.entity.Account;
import com.bootcamp.personal.passiveaccounts.entity.CreditCard;
import com.bootcamp.personal.passiveaccounts.entity.PersonalClient;
import com.bootcamp.personal.passiveaccounts.repository.AccountRepository;
import com.bootcamp.personal.passiveaccounts.repository.AccountTypeRepository;
import com.bootcamp.personal.passiveaccounts.service.AccountService;
import com.bootcamp.personal.passiveaccounts.service.WebClientService;
import com.bootcamp.personal.passiveaccounts.util.Constant;
import com.bootcamp.personal.passiveaccounts.util.handler.exceptions.BadRequestException;
import com.bootcamp.personal.passiveaccounts.util.handler.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    public final AccountRepository repository;

    public final AccountTypeRepository accountTypeRepository;

    public final WebClientService webClient;


    @Override
    public Flux<Account> getAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Account> getById(String id) {
        return repository.findById(id);
    }

    public Mono<PersonalClient> getPersonalClient() {
        return webClient
                .getWebClient()
                .get()
                .uri("/client/personal/find/626b2053ad42f83f0c443e14")
                .retrieve()
                .bodyToMono(PersonalClient.class);
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
                .switchIfEmpty(accountTypeRepository.findById(account.getIdAccountType())
                        .map(at -> {
                            return webClient
                                    .getWebClient()
                                    .get()
                                    .uri("/client/personal/find/" + account.getIdClient())
                                    .retrieve()
                                    .bodyToMono(PersonalClient.class)
                                    .map(c -> {

                                        account.setId(null);
                                        account.setInsertionDate(new Date());
                                        account.setRegistrationStatus((short) 1);

                                        if (at.getAbbreviation().equals(Constant.ACCOUNT_TYPE_VIP)) {
                                            if (c.getProfile().equals(Constant.CLIENT_TYPE_VIP)) {

                                                return webClient
                                                        .getWebClient()
                                                        .get()
                                                        .uri("personal/active/credit_card/" + c.getId())
                                                        .retrieve()
                                                        .bodyToMono(CreditCard.class)
                                                        .map(card -> repository.save(account))
                                                        .switchIfEmpty(Mono.error(new NotFoundException(
                                                                "ID",
                                                                "Client haven't one credit card",
                                                                account.getIdClient(),
                                                                AccountServiceImpl.class,
                                                                "save.notFoundException"
                                                        )));
                                            } else {
                                               return Mono.error(new NotFoundException(
                                                        "ID",
                                                        "Client is not VIP",
                                                        account.getIdClient(),
                                                        AccountServiceImpl.class,
                                                        "save.notFoundException"
                                                ));
                                            }
                                        } else {
                                            return repository.save(account);
                                        }
                                    });
                        }
                ))
                .onErrorResume(e -> Mono.error(e))
                .cast(Account.class);
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