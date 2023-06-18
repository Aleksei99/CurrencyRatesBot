package com.smuraha.service.impl;

import com.smuraha.model.CurrencyRateInfo;
import com.smuraha.repository.CurrencyRateRepo;
import com.smuraha.service.ChartService;
import com.smuraha.service.util.TelegramUI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final TelegramUI telegramUI;
    private final CurrencyRateRepo currencyRateRepo;

    @Override
    public void drawChart(String chatId) throws IOException {
        List<CurrencyRateInfo> rawData = currencyRateRepo.getRatesDataForCurrencyFor30Days("USD");
        Map<LocalDate, List<BigDecimal>> data = new TreeMap<>();
        for (CurrencyRateInfo info:rawData) {
            data.put(info.getLastUpdate(),List.of(info.getBuy(),info.getSell()));
        }
        telegramUI.drawChart(data,chatId);
    }
}
