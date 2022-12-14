/*
 * Copyright (c) 2022-2023. Fernando Fernandez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apitome.samples.sor.service;

import org.apitome.samples.sor.repository.AccountRepository;
import org.apitome.samples.sor.model.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public BigDecimal debitAccount(Long accountNumber, BigDecimal amount) {
        Optional<Account> optionalAccount = accountRepository.findById(accountNumber);
        if (!optionalAccount.isPresent()) {
            throw new RuntimeException("Account not found");
        }
        Account account = optionalAccount.get();
        BigDecimal balance = account.getBalance();
        balance = balance.subtract(amount);
        account.setBalance(balance);
        accountRepository.saveAndFlush(account);
        return balance;
    }
}
