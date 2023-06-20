package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface CurrencyButtons {
    List<List<InlineKeyboardButton>> getCurrencyButtons(Update update) throws JsonProcessingException;
}
