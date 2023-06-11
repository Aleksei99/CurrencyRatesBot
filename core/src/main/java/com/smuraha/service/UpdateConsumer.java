package com.smuraha.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateConsumer {
    void consumeUserInput(Update update);
    void consumeCommand(Update update);
}
