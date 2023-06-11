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

    private BigDecimal rateBuy;

    private BigDecimal rateSell;

    private LocalDateTime lastUpdate;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;
}
