package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.Bank;
import com.smuraha.model.enums.Currencies;
import com.smuraha.service.enums.CallBackKeys;
import com.smuraha.service.enums.CallBackParams;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TelegramUI {
    SendMessage getMessageWithButtons(List<List<InlineKeyboardButton>> buttons, String text);

    String getBankFormedRates(Bank bank);

    List<List<InlineKeyboardButton>> getCurrencyButtons(List<Currencies> currencies,
                                                        CallBackKeys cbs) throws JsonProcessingException;

    List<InlineKeyboardButton> getCustomPager(CallBackKeys key, Map<CallBackParams,
            String> params, int page, int totalPages) throws JsonProcessingException;

    void drawChart(Map<LocalDate, List<BigDecimal>> data, String chatId) throws IOException;
}
