package com.smuraha.service.util;

import com.smuraha.model.Bank;
import com.smuraha.model.CurrencyRate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Service
public class TelegramUIImpl implements TelegramUI {

    @Override
    public SendMessage getMessageWithButtons(List<List<InlineKeyboardButton>> currencyButtons, String text) {
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(currencyButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setText(text);
        return sendMessage;
    }

    @Override
    public String getBankFormedRates(Bank bank) {
        StringBuilder builder = new StringBuilder();
        CurrencyRate rate = bank.getRates().get(0);
        builder.append("<b>").append(bank.getBankName()).append("</b>").append("\n")
                .append("• Сдать ").append(rate.getCurrency()).append(" : ").append(rate.getRateBuy()).append("\n")
                .append("• Купить ").append(rate.getCurrency()).append(" : ").append(rate.getRateSell()).append("\n")
                .append("\n");
        return builder.toString();
    }
}
