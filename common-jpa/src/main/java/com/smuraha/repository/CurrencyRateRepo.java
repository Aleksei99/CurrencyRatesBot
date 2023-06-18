package com.smuraha.repository;

import com.smuraha.model.CurrencyRate;
import com.smuraha.model.CurrencyRateInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRateRepo extends JpaRepository<CurrencyRate, Long> {

    @Query(value = """
            select date(last_update) as lastUpdate,
                max(rate_buy) as buy,
                max(rate_sell) as sell
            from currency_rate
            where currency = :curr and
                last_update >= current_date - INTERVAL '30' DAY
            group by date(last_update)
            """, nativeQuery = true)
    List<CurrencyRateInfo> getRatesDataForCurrencyFor30Days(@Param("curr") String currency);
}
