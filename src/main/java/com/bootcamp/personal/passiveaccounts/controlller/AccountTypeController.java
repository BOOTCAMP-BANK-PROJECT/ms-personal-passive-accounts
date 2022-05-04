package com.bootcamp.personal.passiveaccounts.controlller;

import com.bootcamp.personal.passiveaccounts.entity.AccountType;
import com.bootcamp.personal.passiveaccounts.service.AccountTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("personal/passive/accounts/accountType")
@Tag(name = "Personal Passive Product Saving AccountType Type", description = "Manage Personal Passive Product saving accountTypes type")
@CrossOrigin(value = {"*"})
@RequiredArgsConstructor
public class AccountTypeController {

    public final AccountTypeService service;

    @GetMapping//(value = "/fully")
    public Mono<ResponseEntity<Flux<AccountType>>> getAll() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.getAll())
        );
    }

    @PostMapping
    public Mono<ResponseEntity<AccountType>> create(@RequestBody AccountType accountType) {

        return service.save(accountType).map(p -> ResponseEntity
                .created(URI.create("/accountType/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(p)
        );
    }

    @PutMapping
    public Mono<ResponseEntity<AccountType>> update(@RequestBody AccountType accountType) {
        return service.update(accountType)
                .map(p -> ResponseEntity.created(URI.create("/accountType/"
                                .concat(p.getId())
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<ResponseEntity<AccountType>> delete(@RequestBody String id) {
        return service.delete(id)
                .map(p -> ResponseEntity.created(URI.create("/accountType/"
                                .concat(p.getId())
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
