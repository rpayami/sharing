package com.sharing.account;

import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface AccountRepository extends ListCrudRepository<Account, Long> {
    Optional<Account> findByName(String name);
}
