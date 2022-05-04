package com.bootcamp.personal.passiveaccounts.controlller;

import com.bootcamp.personal.passiveaccounts.entity.Account;
import com.bootcamp.personal.passiveaccounts.entity.PersonalClient;
import com.bootcamp.personal.passiveaccounts.service.impl.AccountServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("personal/passive/accounts")
@Tag(name = "Personal Passive Product Saving Account", description = "Manage Personal Passive Product saving accounts type")
@CrossOrigin(value = {"*"})
@RequiredArgsConstructor
public class AccountController {

    public final AccountServiceImpl service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Account>>> getAll() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.getAll())
        );
    }


    @PostMapping
    public Mono<ResponseEntity<Account>> create(@RequestBody Account account) {

        return service.save(account).map(p -> ResponseEntity
                .created(URI.create("/Account/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(p)
        );
    }

    @PutMapping
    public Mono<ResponseEntity<Account>> update(@RequestBody Account account) {
        return service.update(account)
                .map(p -> ResponseEntity.created(URI.create("/Account/"
                                .concat(p.getId())
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<ResponseEntity<Account>> delete(@RequestBody String id) {
        return service.delete(id)
                .map(p -> ResponseEntity.created(URI.create("/Account/"
                                .concat(p.getId())
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
