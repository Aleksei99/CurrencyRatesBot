package com.smuraha.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CurrencyRateInfo {
    LocalDate getLastUpdate();

    BigDecimal getBuy();

    BigDecimal getSell();
}
