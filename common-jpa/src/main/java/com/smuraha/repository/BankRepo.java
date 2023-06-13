package com.smuraha.repository;

import com.smuraha.model.Bank;
import com.smuraha.model.enums.Currencies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepo extends JpaRepository<Bank, Long> {
    Bank findBankByBankName(String name);

    @Query("""
            select b from Bank b join fetch b.rates c
            where c.lastUpdate in
            (select max(cr.lastUpdate) from CurrencyRate cr
            group by cr.bank,cr.currency)
            and c.currency = ?1
            order by c.rateSell asc
            """)
    List<Bank> getBankRatesByCur(Currencies currency);

    @Query("""
            select b from Bank b join fetch b.rates c
            where c.lastUpdate in
            (select max(cr.lastUpdate) from CurrencyRate cr
            where cr.bank.id = :id)
            and c.currency = :cur
            and b.id = :id
            """)
    Bank getBankByIdAndCur(@Param("id") Long id, @Param("cur") Currencies currency);
}
