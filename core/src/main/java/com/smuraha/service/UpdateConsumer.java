package com.smuraha.service;

import com.smuraha.model.dto.UpdateWithUserDto;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateConsumer {
    void consumeUserInput(UpdateWithUserDto updateDto);
    void consumeCommand(Update update);
    void consumeCallback(Update update);
}
