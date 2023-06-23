package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.AppUser;
import com.smuraha.model.Subscription;
import com.smuraha.model.enums.Currencies;
import com.smuraha.repository.AppUserRepo;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.enums.CallBackParams;
import com.smuraha.service.scheduler.SchedulerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.smuraha.service.enums.CallBackKeys.UNS;
import static com.smuraha.service.enums.CallBackParams.C;

@Service
@RequiredArgsConstructor
public class CurrencyButtonsUnsubscribe implements CurrencyButtons {
    private final AppUserRepo userRepo;
    private final JsonMapper jsonMapper;

    @Override
    public List<List<InlineKeyboardButton>> getCurrencyButtons(Update update) throws JsonProcessingException {
        User from = update.getMessage().getFrom();
        Long userId = from.getId();
        AppUser appUser = userRepo.findByTelegramUserIdWithJPQLFetch(userId);
        List<Subscription> subscriptions = appUser.getSubscriptions();
        if (subscriptions == null || subscriptions.isEmpty()) {
            return null;
        }

        List<Currencies> currencies = new ArrayList<>();
        subscriptions.forEach(subscription -> currencies.add(subscription.getCurrency()));

        List<InlineKeyboardButton> currenciesKeyBoard = new ArrayList<>();
        for (Currencies currency : currencies) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(currency.name());
            HashMap<CallBackParams, String> params = new HashMap<>();
            params.put(C, currency.name());
            inlineKeyboardButton.setCallbackData(jsonMapper.writeCustomCallBackAsString(
                    new CustomCallBack(UNS, params)
            ));
            currenciesKeyBoard.add(inlineKeyboardButton);
        }
        List<List<InlineKeyboardButton>> currencyButtons = new ArrayList<>();
        currencyButtons.add(currenciesKeyBoard);
        return currencyButtons;
    }
}
