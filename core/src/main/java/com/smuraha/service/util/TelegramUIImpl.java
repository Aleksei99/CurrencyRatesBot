package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.Bank;
import com.smuraha.model.CurrencyRate;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.enums.CallBackKeys;
import com.smuraha.service.enums.CallBackParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.smuraha.service.enums.CallBackKeys.CB;
import static com.smuraha.service.enums.CallBackParams.P;

@Service
@RequiredArgsConstructor
public class TelegramUIImpl implements TelegramUI {

    private final JsonMapper jsonMapper;

    @Override
    public SendMessage getMessageWithButtons(List<List<InlineKeyboardButton>> currencyButtons, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
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
        builder.append("<b>").append(bank.getBankName()).append("</b> \uD83D\uDCB0").append("\n")
                .append("❌ Сдать ").append(rate.getCurrency()).append(" : ").append(rate.getRateBuy()).append("\n")
                .append("✅ Купить ").append(rate.getCurrency()).append(" : ").append(rate.getRateSell()).append("\n")
                .append("\uD83D\uDD57").append(" ").append(rate.getLastUpdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n")
                .append("\n");
        return builder.toString();
    }

    @Override
    public List<InlineKeyboardButton> getCustomPager(CallBackKeys key, Map<CallBackParams, String> params, int page,int totalPages) throws JsonProcessingException {
        List<InlineKeyboardButton> pager = new ArrayList<>();
        if(page>0) {
            InlineKeyboardButton prev = new InlineKeyboardButton();
            prev.setText("←");
            params.put(P, (page - 1) + "");
            prev.setCallbackData(jsonMapper.writeCustomCallBackAsString(
                    new CustomCallBack(key, params)
            ));
            pager.add(prev);
        }
        InlineKeyboardButton cur = new InlineKeyboardButton();
        cur.setText(""+(page+1)+"/"+totalPages);
        cur.setCallbackData("IGNORE");
        pager.add(cur);
        if(page<totalPages-1){
            InlineKeyboardButton next = new InlineKeyboardButton();
            next.setText("→");
            params.put(P, (page + 1) + "");
            next.setCallbackData(jsonMapper.writeCustomCallBackAsString(
                    new CustomCallBack(key, params)
            ));
            pager.add(next);
        }
        return pager;
    }
}
