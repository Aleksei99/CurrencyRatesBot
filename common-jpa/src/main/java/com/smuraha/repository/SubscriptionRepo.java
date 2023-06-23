package com.smuraha.repository;

import com.smuraha.model.AppUser;
import com.smuraha.model.Subscription;
import com.smuraha.model.enums.Currencies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepo extends JpaRepository<Subscription,Long> {
    @Query("select s from Subscription s where s.user = :user and s.timeNotify is null")
    Subscription findByUserAndAndTimeNotifyIsNull(@Param("user") AppUser user);

    @Query("select s from Subscription s where s.user = ?1 and s.currency = ?2")
    Subscription findByUserAndCurrency(AppUser user, Currencies currency);
}
