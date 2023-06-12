package com.smuraha.service.dto;

import com.smuraha.model.enums.Currencies;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankCurrencyDto {

    private Currencies currency;

    private BigDecimal rateBuy;

    private BigDecimal rateSell;

}
