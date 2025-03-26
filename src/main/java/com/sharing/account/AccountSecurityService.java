package com.sharing.account;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountSecurityService implements UserDetailsService {
    AccountRepository accountRepository;

    public AccountSecurityService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByName(username).orElseThrow(() ->
                new UsernameNotFoundException("Username does not exist " + username));
        return new AccountPrincipal(account);
    }
}
