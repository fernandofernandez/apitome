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

package org.apitome.samples.sor.repository;

import org.apitome.samples.sor.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Modifying
    @Query(value = "UPDATE account acnt SET balance = balance - :amount WHERE acnt.account_number = :account_number RETURNING *", nativeQuery = true)
    void debitAccount(@Param("account_number") Long accountNumber, @Param("amount") BigDecimal amount);

    @Modifying
    @Query(value = "UPDATE account acnt SET balance = balance + :amount WHERE acnt.account_number = :account_number RETURNING *", nativeQuery = true)
    void creditAccount(@Param("account_number") Long accountNumber, @Param("amount") BigDecimal amount);
}
