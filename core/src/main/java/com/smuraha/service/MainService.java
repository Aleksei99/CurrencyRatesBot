package com.smuraha.service;

import com.smuraha.model.dto.UpdateWithUserDto;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {

    void processUserInput(UpdateWithUserDto updateDto);

    void processCommand(Update update);

    void processCallback(Update update);
}
