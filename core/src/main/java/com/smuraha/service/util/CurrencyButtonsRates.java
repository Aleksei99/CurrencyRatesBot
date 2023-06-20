package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.enums.Currencies;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.enums.CallBackParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.smuraha.service.enums.CallBackKeys.CCB;
import static com.smuraha.service.enums.CallBackParams.C;

@Service
@RequiredArgsConstructor
public class CurrencyButtonsRates implements CurrencyButtons{

    private final JsonMapper jsonMapper;

    @Override
    public List<List<InlineKeyboardButton>> getCurrencyButtons(Update update) throws JsonProcessingException {

        List<InlineKeyboardButton> currenciesKeyBoard = new ArrayList<>();
        for (Currencies currency : Currencies.values()) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(currency.name());
            HashMap<CallBackParams, String> params = new HashMap<>();
            params.put(C, currency.name());
            inlineKeyboardButton.setCallbackData(jsonMapper.writeCustomCallBackAsString(
                    new CustomCallBack(CCB, params)
            ));
            currenciesKeyBoard.add(inlineKeyboardButton);
        }
        List<List<InlineKeyboardButton>> currencyButtons = new ArrayList<>();
        currencyButtons.add(currenciesKeyBoard);
        return currencyButtons;
    }
}
