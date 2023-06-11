package com.smuraha.service.impl;

import com.smuraha.service.AnswerProducer;
import com.smuraha.service.MainService;
import com.smuraha.service.enums.Commands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final AnswerProducer answerProducer;

    @Override
    public void processUserInput(Update update) {
        ///TODO реализовать обработку
        Long chatId = update.getMessage().getChatId();
        String answer = """
                Пока не реализовано
                """;
        sendAnswer(answer, chatId);
    }

    @Override
    public void processCommand(Update update) {
        Message message = update.getMessage();
        String userCommand = message.getText();
        try {
            Commands command = Commands.getCommand(userCommand);
            switch (command) {
                case UPDATE -> {
                    updateCurrencies();
                    sendAnswer("Курс валют успешно обновлен!", message.getChatId());
                }
            }
        } catch (UnsupportedOperationException e) {
            log.error("Пользователь ввел не существующую команду");
            sendAnswer(e.getMessage(), message.getChatId());
        }
    }

    private void updateCurrencies() {
        ///TODO реализовать
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        answerProducer.produce(sendMessage);
    }
}
