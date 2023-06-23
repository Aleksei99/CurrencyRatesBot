package com.smuraha.repository;

import com.smuraha.model.AppUser;
import com.smuraha.model.enums.Currencies;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AppUserRepoTest {

    @Autowired
    private AppUserRepo userRepo;

    @Test
    void findByTelegramUserId() {
        Optional<AppUser> user = userRepo.findByTelegramUserId(926023838L);
        assertThat(user).isNotNull().get().extracting("username").isEqualTo("alexiandr009");
    }

    @Test
    void findByTelegramUserIdWithJPQLFetch() {
        AppUser user = userRepo.findByTelegramUserIdWithJPQLFetch(926023838L);
        assertThat(user).isNotNull().extracting("username").isEqualTo("alexiandr009");
        assertThat(user.getSubscriptions()).hasSize(2);
    }

    @Test
    void findByTelegramUserIdAndCurrencyWithJPQLFetch() {
        AppUser user = userRepo.findByTelegramUserIdAndCurrencyWithJPQLFetch(926023838L, Currencies.USD);
        assertThat(user).isNotNull().extracting("username").isEqualTo("alexiandr009");
        assertThat(user.getSubscriptions()).hasSize(1);
    }
}