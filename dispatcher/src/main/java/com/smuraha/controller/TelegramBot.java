package com.smuraha.controller;

import com.smuraha.configuration.TelegramConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramConfig config;
    private final UpdateController updateController;


    @PostConstruct
    public void init() {
        updateController.registerBot(this);
    }

    public TelegramBot(TelegramConfig config, UpdateController updateController) {
        super(config.getToken());
        this.config = config;
        this.updateController = updateController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }

    public void sendMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Error occurred, during sending message", e);
            }
        }
    }
}
