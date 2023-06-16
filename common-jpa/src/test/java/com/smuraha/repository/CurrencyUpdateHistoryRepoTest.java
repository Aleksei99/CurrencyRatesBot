package com.smuraha.repository;

import com.smuraha.model.CurrencyUpdateHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CurrencyUpdateHistoryRepoTest {

    @Autowired
    private CurrencyUpdateHistoryRepo historyRepo;

    @Test
    void findByUpdateTimeIsGreaterThanEqual() {
        Optional<CurrencyUpdateHistory> history = historyRepo.findByUpdateTimeIsGreaterThanEqual(LocalDateTime.now().minusHours(1));
        assertThat(history).isEmpty();
    }
}