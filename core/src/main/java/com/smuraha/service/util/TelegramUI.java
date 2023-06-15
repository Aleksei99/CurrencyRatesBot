package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.Bank;
import com.smuraha.service.enums.CallBackKeys;
import com.smuraha.service.enums.CallBackParams;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;

public interface TelegramUI {
    SendMessage getMessageWithButtons(List<List<InlineKeyboardButton>> buttons, String text);
    String getBankFormedRates(Bank bank);
    List<InlineKeyboardButton> getCustomPager(CallBackKeys key, Map<CallBackParams, String> params, int page,int totalPages) throws JsonProcessingException;
}
