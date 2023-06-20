package com.smuraha.repository;

import com.smuraha.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramUserId(Long id);

    @Query("""
            select a from AppUser a
            left join fetch a.subscriptions
            where a.telegramUserId = :id
            """)
    AppUser findByTelegramUserIdWithJPQLFetch(@Param("id") Long id);
}
