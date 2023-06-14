package com.smuraha.repository;

import com.smuraha.config.TestConfig;
import com.smuraha.model.Bank;
import com.smuraha.model.enums.Currencies;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;




@DataJpaTest
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(locations="classpath:test.properties")
class BankRepoTest {

    @Autowired
    private BankRepo bankRepo;

    @Test
    void getBanksByCur() {
        List<Bank> banksByCur = bankRepo.getBanksByCur(Currencies.EUR);
        assertThat(banksByCur).isNull();
        System.out.println("ujdyj");
    }

    @Test
    void getBankByIdAndCur() {
    }

    @Test
    void findBankByBankName() {

    }
}