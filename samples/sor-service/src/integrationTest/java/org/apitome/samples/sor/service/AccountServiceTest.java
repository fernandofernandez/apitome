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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {AccountServiceTest.Initializer.class})
@Testcontainers
public class AccountServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Container
    public static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("integration-test-db")
            .withUsername("sa")
            .withPassword("sa");

    @Autowired
    public AccountService accountService;

    @Test
    public void testDebitAccount() {
        BigDecimal balance = accountService.debitAccount(1L, new BigDecimal("100.00"));
        assertEquals(new BigDecimal("12245.670"), balance);
    }

    @Test
    public void testDebitAccountDoesNotExist() {
        Exception result = Assertions.assertThrows(RuntimeException.class,
                () -> accountService.debitAccount(77L, new BigDecimal("100.00")));
        assertEquals("Account not found", result.getMessage());
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            System.setProperty("SOR_DB_URL", postgreSQLContainer.getJdbcUrl());
            System.setProperty("SOR_DB_USER", postgreSQLContainer.getUsername());
            System.setProperty("SOR_DB_PASS", postgreSQLContainer.getPassword());
            TestPropertyValues.of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                            "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                            "spring.datasource.password=" + postgreSQLContainer.getPassword())
                    .applyTo(applicationContext.getEnvironment());
        }
    }
}
