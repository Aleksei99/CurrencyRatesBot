package com.smuraha.service;

import com.smuraha.model.enums.Currencies;

import java.io.IOException;

public interface ChartService {
    void drawChartByCurrency(String chatId, Currencies currency) throws IOException;
}
