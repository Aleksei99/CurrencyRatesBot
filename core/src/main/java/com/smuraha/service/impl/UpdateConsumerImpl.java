package com.smuraha.service.impl;

import com.smuraha.service.MainService;
import com.smuraha.service.UpdateConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.smuraha.RabbitQueue.*;

@Service
@RequiredArgsConstructor
public class UpdateConsumerImpl implements UpdateConsumer {

    private final MainService mainService;

    @Override
    @RabbitListener(queues = {USER_INPUT_QUEUE})
    public void consumeUserInput(Update update) {
        mainService.processUserInput(update);
    }

    @Override
    @RabbitListener(queues = {COMMAND_QUEUE})
    public void consumeCommand(Update update) {
        mainService.processCommand(update);
    }

    @Override
    @RabbitListener(queues = {CALLBACK_QUEUE})
    public void consumeCallback(Update update) {
        mainService.processCallback(update);
    }
}
