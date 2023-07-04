package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.enums.Currencies;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.smuraha.service.enums.CallBackKeys.CCB;

@Service
@RequiredArgsConstructor
public class CurrencyButtonsRates implements CurrencyButtons {

    private final TelegramUI telegramUI;

    @Override
    public List<List<InlineKeyboardButton>> getCurrencyButtons(Update update) throws JsonProcessingException {
        Currencies[] currencies = Currencies.values();
        return telegramUI.getCurrencyButtons(List.of(currencies), CCB);
    }
}
