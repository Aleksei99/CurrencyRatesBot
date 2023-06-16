package com.smuraha.repository;

import com.smuraha.model.CurrencyUpdateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CurrencyUpdateHistoryRepo extends JpaRepository<CurrencyUpdateHistory,Long> {
    Optional<CurrencyUpdateHistory> findByUpdateTimeIsGreaterThanEqual(LocalDateTime nowMinusHour);
}
