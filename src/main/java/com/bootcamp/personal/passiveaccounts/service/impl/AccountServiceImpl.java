package com.bootcamp.personal.passiveaccounts.service.impl;

import com.bootcamp.personal.passiveaccounts.entity.Account;
import com.bootcamp.personal.passiveaccounts.entity.CreditCard;
import com.bootcamp.personal.passiveaccounts.entity.GenericAccount;
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
import org.springframework.web.reactive.function.BodyInserters;
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
                            "Client has one or more accounts",
                            sa.getId(),
                            AccountServiceImpl.class,
                            "save.onErrorResume"
                    );
                })
                .switchIfEmpty(
                        accountTypeRepository.findById(account.getIdAccountType())
                                .flatMap(at ->
                                        webClient
                                                .getWebClient("entrando desde c>>"+account.getIdClient())
                                                .get()
                                                .uri("/client/personal/find/" + account.getIdClient())
                                                .retrieve()
                                                .bodyToMono(PersonalClient.class)
                                                .flatMap(c -> {

                                                    account.setId(null);
                                                    account.setInsertionDate(new Date());
                                                    account.setRegistrationStatus((short) 1);

                                                    if (at.getAbbreviation().equals(Constant.ACCOUNT_TYPE_VIP)) {
                                                        if (!c.getProfile().equals(Constant.CLIENT_TYPE_VIP)) {
                                                            return Mono.error(new NotFoundException(
                                                                    "ID",
                                                                    "Client is not VIP",
                                                                    account.getIdClient(),
                                                                    AccountServiceImpl.class,
                                                                    "save.notFoundException"
                                                            ));
                                                        } else {

                                                            return webClient
                                                                    .getWebClient()
                                                                    .get()
                                                                    .uri("personal/active/credit_card/" + c.getId())
                                                                    .retrieve()
                                                                    .bodyToMono(CreditCard.class)
                                                                    .switchIfEmpty(Mono.error(new NotFoundException(
                                                                            "ID",
                                                                            "Client doesn't have one credit card",
                                                                            account.getIdClient(),
                                                                            AccountServiceImpl.class,
                                                                            "save.notFoundException"
                                                                    )))
                                                                    .flatMap(card ->
                                                                            repository.save(account)
                                                                                    .flatMap( acc -> {
                                                                                        GenericAccount _acc = new GenericAccount();

                                                                                        _acc.setAccountId(acc.getId());
                                                                                        _acc.setAccountType(at.getAbbreviation());
                                                                                        _acc.setAccountUrl("personal/passive/saving_account");
                                                                                        _acc.setAccountIsoCurrencyCode("USD");
                                                                                        _acc.setAccountFk_insertionUser(acc.getFk_insertionUser());
                                                                                        _acc.setAccountInsertionTerminal(acc.getInsertionTerminal());

                                                                                        return webClient
                                                                                                .getWebClient()
                                                                                                .post()
                                                                                                .uri("/client/personal/"+c.getDocumentNumber()+"/accounts")
                                                                                                .body(BodyInserters.fromValue(_acc))
                                                                                                .retrieve()
                                                                                                .bodyToMono(PersonalClient.class)
                                                                                                .then(Mono.just(account));
                                                                                    })
                                                                    );
                                                        }
                                                    } else {
                                                        return repository.save(account);
                                                    }
                                                })

                                )
                                .switchIfEmpty(
                                        Mono.error(new NotFoundException(
                                        "ID",
                                        "Account Type with id "+account.getIdAccountType()+" does not exists.",
                                        account.getIdClient(),
                                        AccountServiceImpl.class,
                                        "save.notFoundException"
                                )))
                )
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