package com.sharing.account;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    List<Account> findAll() {
        return accountRepository.findAll();
    }

    @GetMapping("/{id}")
    public Account findById(@PathVariable Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return account.get();
    }

    @PostMapping
    Long create(@RequestBody @Valid Account account) {
        accountRepository.save(account);
        return account.getId();
    }

    @PutMapping("/{id}")
    void update(@RequestBody @Valid Account account, @PathVariable Long id) {
        Optional<Account> result = accountRepository.findById(id);

        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        LocalDateTime now = LocalDateTime.now();
        Account existingAccount = result.get();
        existingAccount.setName(account.getName());
        accountRepository.save(existingAccount);
    }

    @DeleteMapping("/{id}")
    public Account deleteById(@PathVariable Long id) {
        Optional<Account> result = accountRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        accountRepository.deleteById(id);
        return result.get();
    }

}
