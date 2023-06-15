package com.smuraha.repository;

import com.smuraha.model.Bank;
import com.smuraha.model.enums.Currencies;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BankRepoTest {

    @Autowired
    private BankRepo bankRepo;

    @Test
    void getBanksByCur() {
        Currencies expectedCurrency = Currencies.EUR;
        List<Bank> banksByCur = bankRepo.getBanksByCur(expectedCurrency, PageRequest.of(1, 20));
        Condition<Bank> hasOnlyOneCurrency = new Condition<>(s -> s.getRates().size() == 1, "has only one currency");
        Condition<Bank> hasOnlyEUR = new Condition<>(s -> s.getRates().get(0).getCurrency() == expectedCurrency, "has only a EUR");

        assertThat(banksByCur).hasSize(8);
        assertThat(banksByCur).are(hasOnlyOneCurrency);
        assertThat(banksByCur).are(hasOnlyEUR);
    }

    @Test
    void getBankByIdAndCur() {
        Currencies expectedCurrency = Currencies.USD;
        Bank bankByIdAndCur = bankRepo.getBankByIdAndCur(3L, expectedCurrency);
        Condition<Bank> hasOnlyOneCurrency = new Condition<>(s -> s.getRates().size() == 1, "has only one currency");
        Condition<Bank> hasOnlyUSD = new Condition<>(s -> s.getRates().get(0).getCurrency() == expectedCurrency, "has only a USD");

        assertThat(bankByIdAndCur).isNotNull();
        assertThat(bankByIdAndCur.getBankName()).isEqualTo("БЕЛАРУСБАНК");
        assertThat(bankByIdAndCur).has(hasOnlyOneCurrency);
        assertThat(bankByIdAndCur).has(hasOnlyUSD);
    }

    @Test
    void findBankByBankName() {
        Bank bank = bankRepo.findBankByBankName("PARITETBANK");
        assertThat(bank).isNotNull();
        assertThat(bank.getBankName()).isEqualTo("PARITETBANK");
    }
}