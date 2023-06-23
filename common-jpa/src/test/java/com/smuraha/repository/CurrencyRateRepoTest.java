package com.smuraha.repository;

import com.smuraha.model.CurrencyRateInfo;
import lombok.*;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CurrencyRateRepoTest {

    @Autowired
    private CurrencyRateRepo currencyRateRepo;

    @Test
    void getRatesDataForCurrencyFor30Days() {
        List<CurrencyRateInfo> rates = currencyRateRepo.getRatesDataForCurrencyFor30Days("USD");
        Condition<CurrencyRateInfo> hasDataOnlyForLast30Days = new Condition<>(cri -> cri.getLastUpdate().compareTo(LocalDate.now().minusDays(30)) >= 0, "has only data for last 30 days");
        assertThat(rates).are(hasDataOnlyForLast30Days);
    }

}