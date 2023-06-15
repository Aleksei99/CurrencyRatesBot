package com.smuraha.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.enums.Currencies;
import com.smuraha.service.AnswerProducer;
import com.smuraha.service.CallBackService;
import com.smuraha.service.JsoupParserService;
import com.smuraha.service.MainService;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.enums.CallBackParams;
import com.smuraha.service.enums.Commands;
import com.smuraha.service.util.JsonMapper;
import com.smuraha.service.util.TelegramUI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.smuraha.service.enums.CallBackKeys.CH_CUR;
import static com.smuraha.service.enums.CallBackParams.C;

@Service
@Slf4j
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final AnswerProducer answerProducer;
    private final JsoupParserService jsoupParserService;
    private final CallBackService callBackService;
    private final TelegramUI telegramUI;
    private final JsonMapper jsonMapper;

    @Override
    public void processUserInput(Update update) {
        ///TODO реализовать обработку
        Long chatId = update.getMessage().getChatId();
        String answer = """
                Пока не реализовано
                """;
        sendTextAnswer(answer, chatId);
    }

    @Override
    public void processCommand(Update update) {
        Message message = update.getMessage();
        String userCommand = message.getText();
        Long chatId = message.getChatId();
        try {
            Commands command = Commands.getCommand(userCommand);
            switch (command) {
                case UPDATE -> {
                    try {
                        updateCurrencies();
                    } catch (IOException e) {
                        sendTextAnswer("Ошибка обновления курсов!", chatId);
                        log.error("Ошибка обновления курсов!", e);
                        return;
                    }
                    sendTextAnswer("Курс валют успешно обновлен!", chatId);
                }
                case RATES -> {
                    String text = "Выберите валюту: ";
                    List<List<InlineKeyboardButton>> currencyButtons;
                    try {
                        currencyButtons = getCurrencyButtons();
                    } catch (JsonProcessingException e) {
                        sendTextAnswer("Ошибка при выборе валюты!", chatId);
                        log.error("Ошибка при выборе валюты!", e);
                        return;
                    }
                    SendMessage sendMessage = telegramUI.getMessageWithButtons(currencyButtons, text);
                    sendMessage.setChatId(chatId);
                    answerProducer.produce(sendMessage);
                }
                case HELP -> {
                    sendTextAnswer("Пока не реализовано!", chatId);
                }
                case RATES_STAT -> {
                    sendTextAnswer("Пока не реализовано!", chatId);
                }
            }
        } catch (UnsupportedOperationException e) {
            log.error("Пользователь ввел не существующую команду");
            sendTextAnswer(e.getMessage(), chatId);
        }
    }

    @Override
    public void processCallback(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getFrom().getId();
        String queryData = callbackQuery.getData();
        if (!queryData.equals("IGNORE")) {
            try {
                SendMessage sendMessage = callBackService.process(jsonMapper.readCustomCallBack(queryData));
                sendMessage.setChatId(chatId);
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId);
                deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
                answerProducer.produce(deleteMessage);
                answerProducer.produce(sendMessage);
            } catch (JsonProcessingException e) {
                sendTextAnswer("Внутренняя ошибка сервера!", chatId);
                log.error("Ошибка парсинга", e);
            }
        }
    }

    private List<List<InlineKeyboardButton>> getCurrencyButtons() throws JsonProcessingException {
        List<InlineKeyboardButton> currenciesKeyBoard = new ArrayList<>();
        for (Currencies currency : Currencies.values()) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(currency.name());
            HashMap<CallBackParams, String> params = new HashMap<>();
            params.put(C, currency.name());
            inlineKeyboardButton.setCallbackData(jsonMapper.writeCustomCallBackAsString(
                    new CustomCallBack(CH_CUR, params)
            ));
            currenciesKeyBoard.add(inlineKeyboardButton);
        }
        List<List<InlineKeyboardButton>> currencyButtons = new ArrayList<>();
        currencyButtons.add(currenciesKeyBoard);
        return currencyButtons;
    }

    private void updateCurrencies() throws IOException {
        jsoupParserService.parseAndUpdate("https://myfin.by/currency");
    }

    private void sendTextAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        answerProducer.produce(sendMessage);
    }
}
