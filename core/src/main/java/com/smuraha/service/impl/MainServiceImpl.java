package com.smuraha.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.AppUser;
import com.smuraha.model.dto.UpdateWithUserDto;
import com.smuraha.model.enums.UserState;
import com.smuraha.service.*;
import com.smuraha.service.enums.Commands;
import com.smuraha.service.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final AnswerProducer answerProducer;
    private final JsoupParserService jsoupParserService;
    private final CallBackService callBackService;
    private final TelegramUI telegramUI;
    private final List<CurrencyButtons> currencyButtons;
    private final UserInputService userInputService;

    @Override
    public void processUserInput(UpdateWithUserDto updateDto) {
        Update update = updateDto.getUpdate();
        Long chatId = update.getMessage().getChatId();
        AppUser user = updateDto.getUser();
        if (user.getUserState().equals(UserState.WAIT_FOR_TIME_PICK)) {
            sendTextAnswer(userInputService.setupSubscriptionSchedule(updateDto), chatId);
        }
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
                    sendTextAnswer("Курс валют обновлен!", chatId);
                }
                case RATES -> {
                    String text = "Выберите валюту: ";
                    List<List<InlineKeyboardButton>> ratesCurrencyButtons;
                    try {
                        ratesCurrencyButtons = currencyButtons.stream()
                                .filter(o -> o instanceof CurrencyButtonsRates)
                                .findAny().get()
                                .getCurrencyButtons(update);

                    } catch (JsonProcessingException e) {
                        sendTextAnswer("Ошибка при выборе валюты!", chatId);
                        log.error("Ошибка при выборе валюты!", e);
                        return;
                    }
                    SendMessage sendMessage = telegramUI.getMessageWithButtons(ratesCurrencyButtons, text);
                    sendMessage.setChatId(chatId);
                    answerProducer.produce(sendMessage);
                }
                case HELP, START -> {
                    sendTextAnswer("""
                            👋  Данный бот по вашему запросу предоставит актуальный курс валют
                            ▶  Для того чтобы получить курс  💰  нажмите /rates
                            ▶  Для получения оповещения  ✓✉  о изменении курса той или иной валюты
                            нажмите /subscribe
                            ▶  Для отключения оповещения  ✕✉  нажмите /unsubscribe
                            ▶  Для просмотра статистики  📈  по курсу нажмите /rates_stat
                            """, chatId);
                }
                case RATES_STAT -> {
                    String text = "Выберите валюту: ";
                    List<List<InlineKeyboardButton>> ratesStatCurrencyButtons;
                    try {
                        ratesStatCurrencyButtons = currencyButtons.stream()
                                .filter(o -> o instanceof CurrencyButtonsChart)
                                .findAny().get()
                                .getCurrencyButtons(update);
                    } catch (JsonProcessingException e) {
                        sendTextAnswer("Ошибка при выборе валюты!", chatId);
                        log.error("Ошибка при выборе валюты!", e);
                        return;
                    }
                    SendMessage sendMessage = telegramUI.getMessageWithButtons(ratesStatCurrencyButtons, text);
                    sendMessage.setChatId(chatId);
                    answerProducer.produce(sendMessage);
                }
                case SUBSCRIBE -> {
                    if (message.getChat().getType().equals("private")) {
                        String text = "Выберите валюту на которую хотите подписаться: ";
                        List<List<InlineKeyboardButton>> subscribeCurrencyButtons;
                        try {
                            subscribeCurrencyButtons = currencyButtons.stream()
                                    .filter(o -> o instanceof CurrencyButtonsSubscribe)
                                    .findAny().get()
                                    .getCurrencyButtons(update);
                        } catch (JsonProcessingException e) {
                            sendTextAnswer("Ошибка при выборе валюты!", chatId);
                            log.error("Ошибка при выборе валюты!", e);
                            return;
                        }
                        if (subscribeCurrencyButtons == null) {
                            sendTextAnswer("Вы уже подписаны на все валюты", chatId);
                            return;
                        }
                        SendMessage sendMessage = telegramUI.getMessageWithButtons(subscribeCurrencyButtons, text);
                        sendMessage.setChatId(chatId);
                        answerProducer.produce(sendMessage);
                    } else {
                        sendTextAnswer("Доступно только в приватном чате с ботом!", chatId);
                    }
                }
                case UNSUBSCRIBE -> {
                    if (message.getChat().getType().equals("private")) {
                        String text = "Выберите валюту от которой хотите отписаться: ";
                        List<List<InlineKeyboardButton>> unsubscribeCurrencyButtons;
                        try {
                            unsubscribeCurrencyButtons = currencyButtons.stream()
                                    .filter(o -> o instanceof CurrencyButtonsUnsubscribe)
                                    .findAny().get()
                                    .getCurrencyButtons(update);
                        } catch (JsonProcessingException e) {
                            sendTextAnswer("Ошибка при выборе валюты!", chatId);
                            log.error("Ошибка при выборе валюты!", e);
                            return;
                        }
                        if (unsubscribeCurrencyButtons == null) {
                            sendTextAnswer("Вы ещё не подписаны ни на одну из валют", chatId);
                            return;
                        }
                        SendMessage sendMessage = telegramUI.getMessageWithButtons(unsubscribeCurrencyButtons, text);
                        sendMessage.setChatId(chatId);
                        answerProducer.produce(sendMessage);
                    } else {
                        sendTextAnswer("Доступно только в приватном чате с ботом!", chatId);
                    }
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
        Long chatId = callbackQuery.getMessage().getChatId();
        String queryData = callbackQuery.getData();
        if (!queryData.equals("IGNORE")) {
            try {
                SendMessage sendMessage = callBackService.process(update);
                if (sendMessage == null) {
                    return;
                }
                InlineKeyboardMarkup replyMarkup = (InlineKeyboardMarkup) sendMessage.getReplyMarkup();
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
                editMessageText.setText(sendMessage.getText());
                editMessageText.setParseMode(ParseMode.HTML);
                editMessageText.setReplyMarkup(replyMarkup);
                answerProducer.produce(editMessageText);
            } catch (JsonProcessingException e) {
                sendTextAnswer("Внутренняя ошибка сервера!", chatId);
                log.error("Ошибка парсинга", e);
            } catch (IOException e) {
                sendTextAnswer("Внутренняя ошибка сервера!", chatId);
                log.error("Ошибка отправки графика", e);
            } catch (SchedulerException e) {
                sendTextAnswer("Внутренняя ошибка сервера!", chatId);
                log.error("Ошибка остановки шедулера", e);
            }
        }
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
