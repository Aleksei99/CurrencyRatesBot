package com.smuraha.service.impl;

import com.smuraha.controller.UpdateController;
import com.smuraha.service.AnswerConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.smuraha.RabbitQueue.ANSWER_QUEUE;

@Service
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateController updateController;

    @Override
    @RabbitListener(queues = {ANSWER_QUEUE})
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }
}
