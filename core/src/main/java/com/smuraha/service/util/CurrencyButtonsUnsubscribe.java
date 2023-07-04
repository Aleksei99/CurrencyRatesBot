package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.AppUser;
import com.smuraha.model.Subscription;
import com.smuraha.model.enums.Currencies;
import com.smuraha.repository.AppUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.smuraha.service.enums.CallBackKeys.UNS;

@Service
@RequiredArgsConstructor
public class CurrencyButtonsUnsubscribe implements CurrencyButtons {
    private final AppUserRepo userRepo;
    private final TelegramUI telegramUI;

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

        return telegramUI.getCurrencyButtons(currencies, UNS);
    }
}
