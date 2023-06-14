package com.smuraha.model;

import com.smuraha.model.enums.Currencies;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "currency_rate")
public class CurrencyRate extends BaseEntity{

    @Enumerated(value = EnumType.STRING)
    private Currencies currency;

    @Column(name = "rate_buy")
    private BigDecimal rateBuy;

    @Column(name = "rate_sell")
    private BigDecimal rateSell;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;
}
