package com.smuraha.service.util;

import com.smuraha.model.Bank;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface TelegramUI {
    SendMessage getMessageWithButtons(List<List<InlineKeyboardButton>> currencyButtons, String text);
    String getBankFormedRates(Bank bank);
}
