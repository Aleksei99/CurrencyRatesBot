package com.smuraha.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {

    void processUserInput(Update update);
    void processCommand(Update update);
}
